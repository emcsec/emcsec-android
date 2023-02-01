package com.aspanta.emcsec.presenter.settingsPresenter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.model.IModel;
import com.aspanta.emcsec.model.ModelImpl;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsFragment;

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

public class SettingsPresenter {

    final String TAG = getClass().getName();

    private Subscription mSubscriptionCourseEmc, mSubscriptionCourseBtc;
    private IModel mIModel = new ModelImpl();
    private List<EmcAddress> mListEmcAddresses = new ArrayList<>();
    private List<BtcAddress> mListBtcAddresses = new ArrayList<>();
    private String courseEmc, courseBtc;
    private Context mContext;
    private SettingsFragment settingsFragment;
    private String currency;
    private Spinner mSpinner;
    private ArrayAdapter mArrayAdapter;

    public SettingsPresenter(Context context, SettingsFragment settingsFragment) {
        this.mContext = context;
        this.settingsFragment = settingsFragment;
    }

    public void getCourses(Spinner spinner, ArrayAdapter arrayAdapter, String selectedCurrency) {

        mSpinner = spinner;
        mArrayAdapter = arrayAdapter;
//        currency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        currency = selectedCurrency;
        settingsFragment.showPleaseWaitDialog();
        if (internetConnectionChecking(mContext)) {
            Log.d("getCourses", "getCourses started");
            getCourseEmc();
            getCourseBtc();
        } else {
            settingsFragment.hidePleaseWaitDialog();
            showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
            mSpinner.setSelection(mArrayAdapter.getPosition(SPHelper.getInstance().getStringValue(CURRENT_CURRENCY)));
        }
    }

    private void getCourseEmc() {

        Log.d("getCourseEmc", "getCourseEmc started");
        mSubscriptionCourseEmc = mIModel.sendGetEmercoinCourseRequest(currency)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::responseHandlerCourseEmc, this::showResponseException);
    }

    private void getCourseBtc() {

        Log.d("getCourseBtc", "getCourseBtc started");
        mSubscriptionCourseBtc = mIModel.sendGetBitcoinCourseRequest(currency)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::responseHandlerCourseBtc, this::showResponseException);
    }

    private String responseHandlerCourseEmc(Response<Object> response) {

        Log.d(TAG, "responseHandlerCourseEmc() started");
        if (response.isSuccessful()) {
            try {
                Log.d(TAG, "response.isSuccessful() EMC started");
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

                SPHelper.getInstance().putStringValue(CURRENT_CURRENCY, currency);

                BigDecimal emercoinDecimalCourse = new BigDecimal(courseEmc);
                SPHelper.getInstance().putStringValue(EMC_EXCHANGE_RATE_KEY,
                        String.valueOf(emercoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                BigDecimal emercoinDecimalBalance = new BigDecimal(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY));
                BigDecimal emercoinRoundBalanceUsd = emercoinDecimalBalance.multiply(emercoinDecimalCourse)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);

                SPHelper.getInstance().putStringValue(EMC_BALANCE_IN_USD_KEY,
                        String.valueOf(emercoinRoundBalanceUsd));

                settingsFragment.hidePleaseWaitDialog();
                return courseEmc;
            } catch (JSONException e) {
                e.printStackTrace();
                settingsFragment.hidePleaseWaitDialog();
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
                Log.d(TAG, "response.isSuccessful() BTC started");

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

                SPHelper.getInstance().putStringValue(CURRENT_CURRENCY, currency);

                BigDecimal bitcoinDecimalCourse = new BigDecimal(courseBtc);
                SPHelper.getInstance().putStringValue(BTC_EXCHANGE_RATE_KEY,
                        String.valueOf(bitcoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                BigDecimal bitcoinDecimalBalance = new BigDecimal(SPHelper.getInstance().getStringValue(BTC_BALANCE_KEY));
                BigDecimal bitcoinRoundBalanceUsd = bitcoinDecimalBalance.multiply(bitcoinDecimalCourse)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);

                SPHelper.getInstance().putStringValue(BTC_BALANCE_IN_USD_KEY,
                        String.valueOf(bitcoinRoundBalanceUsd));

                settingsFragment.hidePleaseWaitDialog();
                return courseBtc;
            } catch (JSONException e) {
                e.printStackTrace();
                courseBtc = "";
                settingsFragment.hidePleaseWaitDialog();
                return courseBtc;
            }
        }

        courseBtc = "";
        return courseBtc;
    }

    private void showResponseException(Throwable throwable) {

        mSpinner.setSelection(mArrayAdapter.getPosition(SPHelper.getInstance().getStringValue(CURRENT_CURRENCY)));
        settingsFragment.hidePleaseWaitDialog();
        showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
    }

    public void unsubscribe() {
        if (mSubscriptionCourseEmc != null) {
            if (!mSubscriptionCourseEmc.isUnsubscribed())
                mSubscriptionCourseEmc.unsubscribe();
        }

        if (mSubscriptionCourseBtc != null) {
            if (!mSubscriptionCourseBtc.isUnsubscribed())
                mSubscriptionCourseBtc.unsubscribe();
        }
    }
}

