package com.aspanta.emcsec.presenter.dashboardPresenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.model.IModel;
import com.aspanta.emcsec.model.ModelImpl;
import com.aspanta.emcsec.model.apiTCP.TcpClientBtc;
import com.aspanta.emcsec.model.apiTCP.TcpClientEmc;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.dashboardFragment.IDashboardFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.ui.activities.MainActivity.showAlertDialog;
import static com.aspanta.emcsec.tools.InternetConnection.internetConnectionChecking;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;


public class DashboardPresenter implements IDashboardPresenter {

    final String TAG = getClass().getName();

    private Subscription mSubscriptionCourseEmc, mSubscriptionCourseBtc;
    private IModel mIModel = new ModelImpl();
    private List<String> mListEmcAddresses = new ArrayList<>();
    private List<String> mListEmcAddressesForChange = new ArrayList<>();
    private List<String> mListBtcAddresses = new ArrayList<>();
    private List<String> mListBtcAddressesForChange = new ArrayList<>();
    private String courseEmc, courseBtc, balanceEmc, balanceBtc;
    private TcpClientEmc mTcpClientEmc;
    private TcpClientBtc mTcpClientBtc;
    private int countEmc, countBtc;
    private long satoshiSumEmc, satoshiSumBtc;
    private ConnectTaskEmc mConnectTaskEmc;
    private ConnectTaskBtc mConnectTaskBtc;

    private IDashboardFragment mIDashboardFragment;
    private Context mContext;
    private String currency;

    public DashboardPresenter(Context mContext, IDashboardFragment mIDashboardFragment) {
        this.mIDashboardFragment = mIDashboardFragment;
        this.mContext = mContext;

        mListBtcAddresses.clear();
        for (BtcAddress btcAddress : App.getDbInstance().btcAddressDao().getAll()) {
            mListBtcAddresses.add(btcAddress.getAddress());
        }

        for (BtcAddressForChange btcAddressForChange : App.getDbInstance().btcAddressForChangeDao().getAll()) {
            mListBtcAddresses.add(btcAddressForChange.getAddress());
            mListBtcAddressesForChange.add(btcAddressForChange.getAddress());
        }

        mListEmcAddresses.clear();
        for (EmcAddress emcAddress : App.getDbInstance().emcAddressDao().getAll()) {
            mListEmcAddresses.add(emcAddress.getAddress());
        }

        for (EmcAddressForChange emcAddressForChange : App.getDbInstance().emcAddressForChangeDao().getAll()) {
            mListEmcAddresses.add(emcAddressForChange.getAddress());
            mListEmcAddressesForChange.add(emcAddressForChange.getAddress());
        }
    }

    @Override
    public void getBalance() {

        currency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        Log.d("CURRENT_CURRENCY", currency);

        courseEmc = null;
        courseBtc = null;
        balanceEmc = null;
        balanceBtc = null;

        countEmc = 0;
        countBtc = 0;
        satoshiSumEmc = 0;
        satoshiSumBtc = 0;

        totalProgressDashboard += 50;

        mIDashboardFragment.showPleaseWaitDialog();

        if (internetConnectionChecking(mContext)) {
            Log.d("internet", "true");
            courseAndBalanceListener();
            mConnectTaskEmc = new ConnectTaskEmc();
            mConnectTaskEmc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            mConnectTaskBtc = new ConnectTaskBtc();
            mConnectTaskBtc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            getCourseEmc();
            getCourseBtc();
        } else {
            Log.d("internet", "false");
            mIDashboardFragment.hidePleaseWaitDialog();
            showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
        }
    }

    private void getCourseEmc() {
        mSubscriptionCourseEmc = mIModel.sendGetEmercoinCourseRequest(currency)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::responseHandlerCourseEmc, this::showResponseException);
    }

    private void getCourseBtc() {
        mSubscriptionCourseBtc = mIModel.sendGetBitcoinCourseRequest(currency)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::responseHandlerCourseBtc, this::showResponseException);
    }

    private String responseHandlerCourseEmc(Response<Object> response) {

        Log.d(TAG, "responseHandlerCourseEmc() started");
        if (response.isSuccessful()) {
            try {
                Log.d(TAG, "response.isSuccessful() started");

                JSONObject jsonObject = new JSONObject(response.body().toString());
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject quotes = data.getJSONObject("quotes");

                Log.d(TAG, "responseHandlerCourseBtc: " + quotes.toString());

                switch (currency) {
                    case "USD":
                        courseEmc = quotes.getJSONObject("USD").getString("price");
                        break;
                    case "EUR":
                        courseEmc = quotes.getJSONObject("EUR").getString("price");
                        break;
                    case "CNY":
                        courseEmc = quotes.getJSONObject("CNY").getString("price");
                        break;
                    case "RUB":
                        courseEmc = quotes.getJSONObject("RUB").getString("price");
                        break;
                }

//                courseEmc = jsonObject.getString("price_usd");

                BigDecimal emercoinDecimalCourse = new BigDecimal(courseEmc);
                SPHelper.getInstance().putStringValue(EMC_EXCHANGE_RATE_KEY,
                        String.valueOf(emercoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                return courseEmc;
            } catch (JSONException e) {
                e.printStackTrace();
                courseEmc = "";
                return courseEmc;
            }
        }
        courseEmc = "";
        return courseEmc;
    }

    private String responseHandlerCourseBtc(Response<Object> response) {

        Log.d(TAG, "responseHandlerCourseBtc() started");
        if (response.isSuccessful()) {
            try {
                Log.d(TAG, "response.isSuccessful() started");

                JSONObject jsonObject = new JSONObject(response.body().toString());
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject quotes = data.getJSONObject("quotes");

                Log.d(TAG, "responseHandlerCourseBtc: " + quotes.toString());

                switch (currency) {
                    case "USD":
                        courseBtc = quotes.getJSONObject("USD").getString("price");
                        break;
                    case "EUR":
                        courseBtc = quotes.getJSONObject("EUR").getString("price");
                        break;
                    case "CNY":
                        courseBtc = quotes.getJSONObject("CNY").getString("price");
                        break;
                    case "RUB":
                        courseBtc = quotes.getJSONObject("RUB").getString("price");
                        break;
                }

                BigDecimal bitcoinDecimalCourse = new BigDecimal(courseBtc);
                SPHelper.getInstance().putStringValue(BTC_EXCHANGE_RATE_KEY,
                        String.valueOf(bitcoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                return courseBtc;
            } catch (JSONException e) {
                e.printStackTrace();
                courseBtc = "";
                return courseBtc;
            }
        }

        courseBtc = "";
        return courseBtc;
    }

    private void showResponseException(Throwable throwable) {
        mIDashboardFragment.hidePleaseWaitDialog();
        showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
    }

    private void courseAndBalanceListener() {

        new CountDownTimer(10000, 200) {

            public void onTick(long millisUntilFinished) {

                if (countEmc == 25) {

                    BigDecimal balanceEmcBigDecimal = new BigDecimal(satoshiSumEmc);
                    balanceEmc = String.valueOf(balanceEmcBigDecimal.divide(new BigDecimal(1000000)));
                    SPHelper.getInstance().putStringValue(EMC_BALANCE_KEY, balanceEmc);

                    Log.d("BALANCE EMC", balanceEmc);
                    countEmc++;
                    mTcpClientEmc.stopClient();
                }

                if (countBtc == 25) {
                    BigDecimal balanceBtcBigDecimal = new BigDecimal(satoshiSumBtc);
                    balanceBtc = String.valueOf(balanceBtcBigDecimal.divide(new BigDecimal(100000000)));
                    SPHelper.getInstance().putStringValue(BTC_BALANCE_KEY, balanceBtc);

                    Log.d("BALANCE BTC", balanceBtc);

                    countBtc++;
                    mTcpClientBtc.stopClient();
                }

                if (!(courseEmc == null) && !(courseBtc == null) && !(balanceEmc == null) && !(balanceBtc == null)) {
                    if (courseEmc.equals("") || courseBtc.equals("")) {
                        mIDashboardFragment.hidePleaseWaitDialog();
                        showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
                        cancel();
                    }
                    try {
                        BigDecimal bitcoinDecimalCourse = new BigDecimal(courseBtc);
                        BigDecimal bitcoinDecimalBalance = new BigDecimal(balanceBtc);
                        BigDecimal bitcoinRoundBalanceUsd = bitcoinDecimalBalance.multiply(bitcoinDecimalCourse)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);

                        SPHelper.getInstance().putStringValue(BTC_EXCHANGE_RATE_KEY,
                                String.valueOf(bitcoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));
                        SPHelper.getInstance().putStringValue(BTC_BALANCE_IN_USD_KEY,
                                String.valueOf(bitcoinRoundBalanceUsd));

                        BigDecimal emercoinDecimalCourse = new BigDecimal(courseEmc);
                        BigDecimal emercoinDecimalBalance = new BigDecimal(balanceEmc);
                        BigDecimal emercoinRoundBalanceUsd = emercoinDecimalBalance.multiply(emercoinDecimalCourse)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);

                        SPHelper.getInstance().putStringValue(EMC_EXCHANGE_RATE_KEY,
                                String.valueOf(emercoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));
                        SPHelper.getInstance().putStringValue(EMC_BALANCE_IN_USD_KEY,
                                String.valueOf(emercoinRoundBalanceUsd));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mIDashboardFragment.hidePleaseWaitDialog();
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                mIDashboardFragment.hidePleaseWaitDialog();
                cancel();
            }
        }.start();
    }

    private class ConnectTaskEmc extends AsyncTask<String, String, TcpClientEmc> {

        @Override
        protected TcpClientEmc doInBackground(String... message) {

            mTcpClientEmc = new TcpClientEmc(response -> {
                //response received from server
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 1) {
                        get20Emc();
                    } else if (jsonObject.getInt("id") == 2) {

                        Log.d("countEmc", String.valueOf(countEmc));

                        Log.d("jsonObject", jsonObject.toString());
                        JSONObject balanceResult = new JSONObject(jsonObject.getString("result"));
                        Log.d("balanceResultJSON", balanceResult.toString());

                        String balanceConfirmed = balanceResult.getString("confirmed");
                        String balanceUnconfirmed = balanceResult.getString("unconfirmed");

                        Log.d("balanceUnconfirmed", balanceUnconfirmed);

                        if (balanceUnconfirmed.contains("-") | mListEmcAddressesForChange.contains(mListEmcAddresses.get(countEmc))) {
                            long totalValue = Long.valueOf(balanceConfirmed) + Long.valueOf(balanceUnconfirmed);
                            satoshiSumEmc += totalValue;
                        } else {
                            satoshiSumEmc += Long.valueOf(balanceConfirmed);
                        }
                        countEmc++;
                        if (countEmc < 25) {
                            get20Emc();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, mContext);
            mTcpClientEmc.run();

            return mTcpClientEmc;
        }

        void doProgress() {
            publishProgress("progress");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (values[0].contains("progress")) {
                if (mIDashboardFragment != null) {
                    mIDashboardFragment.setDownloadProgress(downloadProgressDashboard);
                    mIDashboardFragment.setTotalProgress(totalProgressDashboard);
                }
            }
        }

        @Override
        protected void onPostExecute(TcpClientEmc mTcpClientEmc) {
            super.onPostExecute(mTcpClientEmc);
            Log.d("onPostExecute", "onPostExecute started");
            if (!mTcpClientEmc.error.isEmpty()) {
                Log.d("error for dialog", mTcpClientEmc.error);
                MainActivity.showAlertDialog(mContext, mContext.getString(R.string.could_not_connect_for) + "Emercoin");
            }
        }
    }

    private class ConnectTaskBtc extends AsyncTask<String, String, TcpClientBtc> {

        @Override
        protected TcpClientBtc doInBackground(String... message) {

            mTcpClientBtc = new TcpClientBtc(response -> {
                //response received from server
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 3) {
                        get20Btc();
                    } else if (jsonObject.getInt("id") == 4) {

                        Log.d("countBtc", String.valueOf(countBtc));

                        JSONObject balanceResult = new JSONObject(jsonObject.getString("result"));
                        String balanceConfirmed = balanceResult.getString("confirmed");
                        String balanceUnconfirmed = balanceResult.getString("unconfirmed");

                        if (balanceUnconfirmed.contains("-") | mListBtcAddressesForChange.contains(mListBtcAddresses.get(countBtc))) {
                            int totalValue = Integer.valueOf(balanceConfirmed) + Integer.valueOf(balanceUnconfirmed);
                            satoshiSumBtc += totalValue;
                        } else {
                            satoshiSumBtc += Integer.valueOf(balanceConfirmed);
                        }
                        countBtc++;
                        if (countBtc < 25) {
                            get20Btc();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            mTcpClientBtc.run();
            return mTcpClientBtc;
        }

        void doProgress() {
            publishProgress("progress");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (values[0].contains("progress")) {
                if (mIDashboardFragment != null) {
                    mIDashboardFragment.setDownloadProgress(downloadProgressDashboard);
                    mIDashboardFragment.setTotalProgress(totalProgressDashboard);
                }
            }
        }

        @Override
        protected void onPostExecute(TcpClientBtc mTcpClientBtc) {
            super.onPostExecute(mTcpClientBtc);
            Log.d("onPostExecute", "onPostExecute started");
            if (!mTcpClientBtc.error.isEmpty()) {
                Log.d("error for dialog", mTcpClientBtc.error);
                MainActivity.showAlertDialog(mContext, mContext.getString(R.string.could_not_connect_for) + "Bitcoin");
            }
        }
    }

    private void get20Emc() {

        String emcAddress = mListEmcAddresses.get(countEmc);
        if (mTcpClientEmc != null) {
            downloadProgressDashboard++;
            mConnectTaskEmc.doProgress();
            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 2,\"method\":\"blockchain.address.get_balance\",\"params\":[\"" + emcAddress + "\"]}";
            mTcpClientEmc.sendMessage(jsonRequest);
        }
    }

    private void get20Btc() {

        String btcAddress = mListBtcAddresses.get(countBtc);
        if (mTcpClientBtc != null) {
            downloadProgressDashboard++;
            mConnectTaskBtc.doProgress();
            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 4,\"method\":\"blockchain.address.get_balance\",\"params\":[\"" + btcAddress + "\"]}";
            mTcpClientBtc.sendMessage(jsonRequest);
        }
    }

    @Override
    public void unsubscribe() {
        if (mSubscriptionCourseEmc != null) {
            if (!mSubscriptionCourseEmc.isUnsubscribed())
                mSubscriptionCourseEmc.unsubscribe();
        }

        if (mSubscriptionCourseBtc != null) {
            if (!mSubscriptionCourseBtc.isUnsubscribed())
                mSubscriptionCourseBtc.unsubscribe();
        }

        if (mTcpClientBtc != null) {
            mTcpClientBtc.stopClient();
        }

        if (mTcpClientEmc != null) {
            mTcpClientEmc.stopClient();
        }

        courseEmc = null;
        courseBtc = null;
        balanceEmc = null;
        balanceBtc = null;

        countEmc = 0;
        countBtc = 0;
        satoshiSumEmc = 0;
        satoshiSumBtc = 0;
    }
}