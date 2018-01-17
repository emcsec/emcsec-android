package com.aspanta.emcsec.presenter.settingsPresenter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.model.IModel;
import com.aspanta.emcsec.model.ModelImpl;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsFragment;

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
import static com.aspanta.emcsec.tools.Config.showAlertDialog;
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
//        currency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        currency = selectedCurrency;
        settingsFragment.showPleaseWaitDialog();
        if (internetConnectionChecking(mContext)) {
            Log.d("getCourses", "getCourses started");
            getCourseEmc();
            getCourseBtc();
        } else {
            settingsFragment.hidePleaseWaitDialog();
            showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
            mSpinner.setSelection(mArrayAdapter.getPosition(SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY)));
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

                SharedPreferencesHelper.getInstance().putStringValue(CURRENT_CURRENCY, currency);

                BigDecimal emercoinDecimalCourse = new BigDecimal(courseEmc);
                SharedPreferencesHelper.getInstance().putStringValue(EMC_COURSE_KEY,
                        String.valueOf(emercoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                BigDecimal emercoinDecimalBalance = new BigDecimal(SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_KEY));
                BigDecimal emercoinRoundBalanceUsd = emercoinDecimalBalance.multiply(emercoinDecimalCourse)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);

                SharedPreferencesHelper.getInstance().putStringValue(EMC_BALANCE_IN_USD_KEY,
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

                SharedPreferencesHelper.getInstance().putStringValue(CURRENT_CURRENCY, currency);

                BigDecimal bitcoinDecimalCourse = new BigDecimal(courseBtc);
                SharedPreferencesHelper.getInstance().putStringValue(BTC_COURSE_KEY,
                        String.valueOf(bitcoinDecimalCourse.setScale(2, BigDecimal.ROUND_HALF_UP)));

                BigDecimal bitcoinDecimalBalance = new BigDecimal(SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY));
                BigDecimal bitcoinRoundBalanceUsd = bitcoinDecimalBalance.multiply(bitcoinDecimalCourse)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);

                SharedPreferencesHelper.getInstance().putStringValue(BTC_BALANCE_IN_USD_KEY,
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

        mSpinner.setSelection(mArrayAdapter.getPosition(SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY)));
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

