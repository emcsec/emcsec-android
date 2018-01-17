package com.aspanta.emcsec.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.db.room.historyPojos.BtcTransaction;
import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;
import com.aspanta.emcsec.model.IModel;
import com.aspanta.emcsec.model.ModelImpl;
import com.aspanta.emcsec.model.apiTCP.TcpClientBtc;
import com.aspanta.emcsec.model.apiTCP.TcpClientEmc;
import com.aspanta.emcsec.presenter.historyPresenter.HistoryBitcoinPresenter;
import com.aspanta.emcsec.presenter.historyPresenter.HistoryEmercoinPresenter;
import com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring.GenerateRestoreSeedActivity;

import org.json.JSONArray;
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
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.SEED;
import static com.aspanta.emcsec.tools.Config.showAlertDialog;
import static com.aspanta.emcsec.tools.InternetConnection.internetConnectionChecking;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;


public class SplashActivity extends AppCompatActivity {

    final String TAG = getClass().getName();

    private Subscription mSubscriptionCourseEmc, mSubscriptionCourseBtc;
    private IModel mIModel = new ModelImpl();
    List<String> mListEmcAddresses = new ArrayList<>();
    List<String> mListBtcAddresses = new ArrayList<>();

    String courseEmc, courseBtc, balanceEmc, balanceBtc;
    TcpClientEmc mTcpClientEmc;
    TcpClientBtc mTcpClientBtc;
    public int countEmc, countBtc;
    public int satoshiSumEmc, satoshiSumBtc;
    ConnectTaskEmc connectTaskEmc;
    ConnectTaskBtc connectTaskBtc;
    String currency;
    private HistoryBitcoinPresenter mHistoryBitcoinPresenter;
    private HistoryEmercoinPresenter mHistoryEmercoinPresenter;
    public static List<EmcTransaction> mEmcTransactionsListForSplashActivity;
    public static List<BtcTransaction> mBtcTransactionsListForSplashActivity;
    private TextView mTvDownloadProgress, mTvTotalProgress;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();

        mHistoryBitcoinPresenter = new HistoryBitcoinPresenter(this, null, null);
        mHistoryEmercoinPresenter = new HistoryEmercoinPresenter(this, null, null);

        currency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        Log.d("SEED: ", SharedPreferencesHelper.getInstance().getStringValue(SEED));
        String seed = SharedPreferencesHelper.getInstance().getStringValue(SEED);

        if (internetConnectionChecking(this)) {
            if (seed.equals("?") || seed.isEmpty()) {
                startActivity(new Intent(this, GenerateRestoreSeedActivity.class));
            } else {
                downloadProgressDashboard = 0;
                totalProgressDashboard = 50;
                courseAndBalanceListener();
                connectTaskEmc = new ConnectTaskEmc();
                connectTaskEmc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                connectTaskBtc = new ConnectTaskBtc();
                connectTaskBtc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                mHistoryBitcoinPresenter.getTransactionHistory();
                mHistoryEmercoinPresenter.getTransactionHistory();
                getCourseEmc();
                getCourseBtc();
            }
        } else {
            if (seed.equals("?") || seed.isEmpty()) {
                startActivity(new Intent(this, GenerateRestoreSeedActivity.class));
            } else {
                showErrorDialog();
                startMainActivity();
            }
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
                JSONArray jsonArray = new JSONArray(response.body().toString());
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                switch (currency) {
                    case "USD":
                        courseEmc = jsonObject.getString("price_usd");
                        break;
                    case "EUR":
                        courseEmc = jsonObject.getString("price_eur");
                        break;
                    case "CNY":
                        courseEmc = jsonObject.getString("price_cny");
                        break;
                    case "RUB":
                        courseEmc = jsonObject.getString("price_rub");
                        break;
                }

                BigDecimal emercoinDecimalCourse = new BigDecimal(courseEmc);
                SharedPreferencesHelper.getInstance().putStringValue(EMC_COURSE_KEY,
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
                JSONArray jsonArray = new JSONArray(response.body().toString());
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                switch (currency) {
                    case "USD":
                        courseBtc = jsonObject.getString("price_usd");
                        break;
                    case "EUR":
                        courseBtc = jsonObject.getString("price_eur");
                        break;
                    case "CNY":
                        courseBtc = jsonObject.getString("price_cny");
                        break;
                    case "RUB":
                        courseBtc = jsonObject.getString("price_rub");
                        break;
                }

                BigDecimal bitcoinDecimalCourse = new BigDecimal(courseBtc);
                SharedPreferencesHelper.getInstance().putStringValue(BTC_COURSE_KEY,
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
        showErrorDialog();
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(getString(R.string.could_not_connect))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                    startMainActivity();
                })
                .create()
                .show();
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class).putExtra("from", ""));
        finish();
    }

    void courseAndBalanceListener() {

        new CountDownTimer(100000, 200) {

            public void onTick(long millisUntilFinished) {

                if (countEmc == 25) {

                    BigDecimal balanceEmcBigDecimal = new BigDecimal(satoshiSumEmc);
                    balanceEmc = String.valueOf(balanceEmcBigDecimal.divide(new BigDecimal(1000000)));
                    SharedPreferencesHelper.getInstance().putStringValue(EMC_BALANCE_KEY, balanceEmc);

                    Log.d("BALANCE EMC", balanceEmc);
                    countEmc++;
                    mTcpClientEmc.stopClient();
                }

                if (countBtc == 25) {
                    BigDecimal balanceBtcBigDecimal = new BigDecimal(satoshiSumBtc);
                    balanceBtc = String.valueOf(balanceBtcBigDecimal.divide(new BigDecimal(100000000)));
                    SharedPreferencesHelper.getInstance().putStringValue(BTC_BALANCE_KEY, balanceBtc);

                    Log.d("BALANCE BTC", balanceBtc);
                    countBtc++;
                    mTcpClientBtc.stopClient();
                }

                if (courseEmc != null & courseBtc != null & balanceEmc != null & balanceBtc != null) {
                    if (mEmcTransactionsListForSplashActivity != null & mBtcTransactionsListForSplashActivity != null) {
                        mEmcTransactionsListForSplashActivity.clear();
                        mBtcTransactionsListForSplashActivity.clear();
                        if (courseEmc.equals("") || courseBtc.equals("")) {
                            showAlertDialog(SplashActivity.this, getString(R.string.could_not_connect));
                            startMainActivity();
                            cancel();
                        }

                        try {
                            BigDecimal bitcoinDecimalCourse = new BigDecimal(courseBtc);
                            BigDecimal bitcoinDecimalBalance = new BigDecimal(balanceBtc);
                            BigDecimal bitcoinRoundBalanceUsd = bitcoinDecimalBalance.multiply(bitcoinDecimalCourse)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP);

                            SharedPreferencesHelper.getInstance().putStringValue(BTC_COURSE_KEY,
                                    String.valueOf(bitcoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                            SharedPreferencesHelper.getInstance().putStringValue(BTC_BALANCE_IN_USD_KEY,
                                    String.valueOf(bitcoinRoundBalanceUsd));

                            BigDecimal emercoinDecimalCourse = new BigDecimal(courseEmc);
                            BigDecimal emercoinDecimalBalance = new BigDecimal(balanceEmc);
                            BigDecimal emercoinRoundBalanceUsd = emercoinDecimalBalance.multiply(emercoinDecimalCourse)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP);

                            SharedPreferencesHelper.getInstance().putStringValue(EMC_COURSE_KEY,
                                    String.valueOf(emercoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                            SharedPreferencesHelper.getInstance().putStringValue(EMC_BALANCE_IN_USD_KEY,
                                    String.valueOf(emercoinRoundBalanceUsd));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (mTcpClientBtc != null) {
                            mTcpClientBtc.stopClient();
                        }

                        if (mTcpClientEmc != null) {
                            mTcpClientEmc.stopClient();
                        }

                        startMainActivity();
                        cancel();
                    }
                }
            }

            @Override
            public void onFinish() {
                Log.d("Timeout", "Timeout");
                startMainActivity();
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

                        Log.d("onProgressUpdate() countEmc", String.valueOf(countEmc));

                        JSONObject balanceResult = new JSONObject(jsonObject.getString("result"));
                        String balanceConfirmed = balanceResult.getString("confirmed");
                        String balanceUnconfirmed = balanceResult.getString("unconfirmed");

                        if (balanceUnconfirmed.contains("-")) {
                            int totalValue = Integer.valueOf(balanceConfirmed) + Integer.valueOf(balanceUnconfirmed);
                            satoshiSumEmc += totalValue;
                        } else {
                            satoshiSumEmc += Integer.valueOf(balanceConfirmed);
                        }
                        countEmc++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, SplashActivity.this.getApplicationContext());
            mTcpClientEmc.run();
            return mTcpClientEmc;
        }

        @Override
        protected void onPostExecute(TcpClientEmc tcpClientEmc) {
            super.onPostExecute(tcpClientEmc);
            Log.d("onPostExecute", "onPostExecute started");
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

                        Log.d("onProgressUpdate() countBtc", String.valueOf(countBtc));

                        JSONObject balanceResult = new JSONObject(jsonObject.getString("result"));
                        String balanceConfirmed = balanceResult.getString("confirmed");
                        String balanceUnconfirmed = balanceResult.getString("unconfirmed");

                        if (balanceUnconfirmed.contains("-")) {
                            int totalValue = Integer.valueOf(balanceConfirmed) + Integer.valueOf(balanceUnconfirmed);
                            satoshiSumBtc += totalValue;
                        } else {
                            satoshiSumBtc += Integer.valueOf(balanceConfirmed);
                        }
                        countBtc++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            mTcpClientBtc.run();

            return mTcpClientBtc;
        }

        @Override
        protected void onPostExecute(TcpClientBtc mTcpClientBtc) {
            super.onPostExecute(mTcpClientBtc);
            Log.d("onPostExecute", "onPostExecute started");
        }
    }

    public void get20Emc() {

        Log.d("get20Emc()", "started");
        for (EmcAddress emcAddress : App.getDbInstance().emcAddressDao().getAll()) {
            mListEmcAddresses.add(emcAddress.getAddress());
        }

        for (EmcAddressForChange emcAddressForChange : App.getDbInstance().emcAddressForChangeDao().getAll()) {
            mListEmcAddresses.add(emcAddressForChange.getAddress());
        }

        for (String emcAddress : mListEmcAddresses) {

            Log.d("mListEmcAddresses", mListEmcAddresses.toString());

            if (mTcpClientEmc != null) {
                downloadProgressDashboard++;
                Log.d("mTcpClientEmc", emcAddress);
                String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 2,\"method\":\"blockchain.address.get_balance\",\"params\":[\"" + emcAddress + "\"]}";
                mTcpClientEmc.sendMessage(jsonRequest);
                Log.d("mTcpClientEmc", jsonRequest);
            }
        }
    }

    public void get20Btc() {

        Log.d("get20Btc()", "started");
        for (BtcAddress btcAddress : App.getDbInstance().btcAddressDao().getAll()) {
            mListBtcAddresses.add(btcAddress.getAddress());
        }

        for (BtcAddressForChange btcAddressForChange : App.getDbInstance().btcAddressForChangeDao().getAll()) {
            mListBtcAddresses.add(btcAddressForChange.getAddress());
        }

        for (String btcAddress : mListBtcAddresses) {

            if (mTcpClientBtc != null) {
                downloadProgressDashboard++;
                Log.d("mTcpClientBtc", btcAddress);
                String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 4,\"method\":\"blockchain.address.get_balance\",\"params\":[\"" + btcAddress + "\"]}";
                mTcpClientBtc.sendMessage(jsonRequest);
                Log.d("mTcpClientBtc", jsonRequest);
            }
        }
    }

    private void init() {
        mTvDownloadProgress = findViewById(R.id.tv_download_progress_splash);
        mTvTotalProgress = findViewById(R.id.tv_total_progress_splash);
    }

    public void setDownloadProgress(int count) {
        mTvDownloadProgress.setText(String.valueOf(count));
    }

    public void setTotalProgress(int count) {
        mTvTotalProgress.setText(String.valueOf(count));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscriptionCourseEmc != null) {
            if (!mSubscriptionCourseEmc.isUnsubscribed())
                mSubscriptionCourseEmc.unsubscribe();
        }

        if (mSubscriptionCourseBtc != null) {
            if (!mSubscriptionCourseBtc.isUnsubscribed())
                mSubscriptionCourseBtc.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}