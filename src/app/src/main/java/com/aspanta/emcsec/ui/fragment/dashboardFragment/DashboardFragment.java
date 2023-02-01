package com.aspanta.emcsec.ui.fragment.dashboardFragment;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.presenter.dashboardPresenter.DashboardPresenter;
import com.aspanta.emcsec.presenter.dashboardPresenter.IDashboardPresenter;
import com.aspanta.emcsec.presenter.historyPresenter.HistoryBitcoinPresenter;
import com.aspanta.emcsec.presenter.historyPresenter.HistoryEmercoinPresenter;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.bitcoinOperationsFragment.BitcoinOperationsFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWaitWithProgress;
import com.aspanta.emcsec.ui.fragment.emercoinOperationsFragment.EmercoinOperationsFragment;
import com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin.DialogSetUpPin;
import com.reginald.swiperefresh.CustomSwipeRefreshLayout;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.ui.activities.MainActivity.isDataDownloaded;


public class DashboardFragment extends Fragment implements IDashboardFragment {

    public final String TAG = getClass().getName();

    IDashboardPresenter mDashboardPresenter;

    private RelativeLayout mRlItem1, mRlEmercoin, mRlBitcoin;
    private LinearLayout mLlMyMoney;
    private ImageView mIvItem1Wallet;
    private ImageSwitcher mIvItem1UpDone;
    private TextView mTvMyMoney;
    private TextView mTvBalanceBitcoin;
    private TextView mTvBalanceEmercoin;
    private TextView mTvWholeRowBalanceBtc;
    private TextView mTvWholeRowBalanceEmc;
    public CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private DialogFragmentPleaseWaitWithProgress dialog;
    private DialogFragmentPleaseWaitWithProgress dialogBtcHistory;
    private DialogFragmentPleaseWaitWithProgress dialogEmcHistory;
    private HistoryBitcoinPresenter mHistoryBitcoinPresenter;
    private HistoryEmercoinPresenter mHistoryEmercoinPresenter;
    private static String mFrom;
    public static int downloadProgressDashboard = 0;
    public static int totalProgressDashboard = 0;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    SPHelper mSPHelper = SPHelper.getInstance();

    public static DashboardFragment newInstance(String from) {
        mFrom = from;
        return new DashboardFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            SPHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDashboardPresenter == null) {
            mDashboardPresenter = new DashboardPresenter(getContext(), this);
        }
        mHistoryBitcoinPresenter = new HistoryBitcoinPresenter(getActivity(), null, this);
        mHistoryEmercoinPresenter = new HistoryEmercoinPresenter(getActivity(), null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        init(v);

        if (!mSPHelper.isAlreadyShown()) {
            Log.d(TAG, "!mSPHelper.isAlreadyShown(): ");
            DialogSetUpPin.newInstance().show(getFragmentManager(), "");
            mSPHelper.setAlreadyShown(true);
        }

        String currentCurrency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceEmercoin.setText(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        mTvBalanceBitcoin.setText(SPHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");

        String wholeRowBalanceEmc = "<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        String wholeRowBalanceBtc = "<b>~" + SPHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> BTC = <b>" + SPHelper.getInstance().getStringValue(BTC_EXCHANGE_RATE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceBtc.setText(Html.fromHtml(wholeRowBalanceBtc));

        String finalCurrentCurrency = currentCurrency;
        listener = (sharedPreferences, key) -> {
            mTvWholeRowBalanceEmc.setText(Html.fromHtml("<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                    finalCurrentCurrency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                    finalCurrentCurrency + ")"));

            mTvWholeRowBalanceBtc.setText(Html.fromHtml("<b>~" + SPHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                    finalCurrentCurrency + " (" + "<b>1 </b> BTC = <b>" + SPHelper.getInstance().getStringValue(BTC_EXCHANGE_RATE_KEY) + " </b>" +
                    finalCurrentCurrency + ")"));

            mTvBalanceEmercoin.setText(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
            mTvBalanceBitcoin.setText(SPHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");

        };
        SPHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);

        if (!isDataDownloaded) {
            if (mFrom.equals("seed")) {
                downloadProgressDashboard = 0;
                totalProgressDashboard = 0;
                mDashboardPresenter.getBalance();
                mHistoryBitcoinPresenter.getTransactionHistory();
                mHistoryEmercoinPresenter.getTransactionHistory();
            }
            isDataDownloaded = true;
        }

        mRlItem1.setOnClickListener(view -> showOrHideLayoutMyMoney());

        mRlEmercoin.setOnClickListener(view -> {
            ((MainActivity) getActivity())
                    .navigator(EmercoinOperationsFragment.newInstance(),
                            EmercoinOperationsFragment.newInstance().getCurrentTag());
        });

        mRlBitcoin.setOnClickListener(view ->
                ((MainActivity) getActivity())
                        .navigator(BitcoinOperationsFragment.newInstance(),
                                BitcoinOperationsFragment.newInstance().getCurrentTag()));

        mSwipeRefreshLayout.setOnRefreshListener(() ->
        {
            downloadProgressDashboard = 0;
            totalProgressDashboard = 0;
            mDashboardPresenter.getBalance();
            mSwipeRefreshLayout.refreshComplete();
        });

        changeColorItem1();

        return v;
    }

    private void showOrHideLayoutMyMoney() {
        if (!mLlMyMoney.isShown()) {
            mRlItem1.setBackgroundResource(R.drawable.dashboard_disable_item1);
            mLlMyMoney.setVisibility(View.VISIBLE);
            mIvItem1UpDone.showNext();
            changeColorItem1();
        } else {
            mRlItem1.setBackgroundResource(R.drawable.dashboard_enabled_item1);
            mLlMyMoney.setVisibility(View.GONE);
            mIvItem1UpDone.showNext();
            mTvMyMoney.setTextColor(Color.WHITE);
            mIvItem1Wallet.getDrawable().mutate().setColorFilter(null);
        }
    }

    private void changeColorItem1() {
        mTvMyMoney.setTextColor(ContextCompat.getColor(getContext(),
                R.color.colorPrimary));
        mIvItem1Wallet.getDrawable().mutate().setColorFilter(ContextCompat
                .getColor(getActivity().getApplicationContext(),
                        R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
    }

    public void init(View v) {

        mTvBalanceEmercoin = v.findViewById(R.id.tv_dashboard_item1_balance_emc);
        mTvWholeRowBalanceEmc = v.findViewById(R.id.tv_dashboard_whole_row_emc);

        mTvBalanceBitcoin = v.findViewById(R.id.tv_dashboard_item1_balance_btc);
        mTvWholeRowBalanceBtc = v.findViewById(R.id.tv_dashboard_whole_row_btc);

        mLlMyMoney = v.findViewById(R.id.ll_dashboard_item1);
        mTvMyMoney = v.findViewById(R.id.tv_dashboard_header_item1);
        mIvItem1UpDone = v.findViewById(R.id.iv_dashboard_item1_up_done);
        mRlItem1 = v.findViewById(R.id.dashboard_header_item1);
        mRlEmercoin = v.findViewById(R.id.rl_emercoin_dashboard);
        mRlBitcoin = v.findViewById(R.id.rl_bitcoin_dashboard);
        mIvItem1Wallet = v.findViewById(R.id.iv_dashboard_item1_wallet);
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_dashboard_main);

    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }


    @Override
    public void showErrorDialog(String mDetail) {
    }

    @Override
    public void showProgressBar(boolean show) {
    }

    @Override
    public void setEmercoinBalance(String mBalance) {
    }

    @Override
    public void setBitcoinBalance(String mBalance) {
    }

    @Override
    public void setBitcoinCourse(String bitcoinCourse) {
    }

    @Override
    public void setEmercoinCourse(String emercoinCourse) {
    }

    @Override
    public void setBitcoinBalanceUsd(String mStrBitcoinBalance) {
    }

    @Override
    public void setEmercoinBalanceUsd(String mStrEmercoinBalance) {
    }

    @Override
    public void showPleaseWaitDialog() {
        dialog = DialogFragmentPleaseWaitWithProgress.newInstance();
        dialog.show(getFragmentManager(), "DashboardBalance");
    }

    @Override
    public void hidePleaseWaitDialog() {
        Log.d("hidePleaseWaitDialog", "hidePleaseWaitDialog");
        if (dialog != null) {
            Log.d("DIALOG", "dialog != null");
            dialog.dismissAllowingStateLoss();
        }
    }

    public void showPleaseWaitDialogForBitcoinHistoryPresenter() {
        dialogBtcHistory = DialogFragmentPleaseWaitWithProgress.newInstance();
        dialogBtcHistory.show(getFragmentManager(), "BtcHistory");
    }

    public void hidePleaseWaitDialogForBitcoinHistoryPresenter() {
        if (dialogBtcHistory != null) {
            dialogBtcHistory.dismissAllowingStateLoss();
        }
    }

    public void showPleaseWaitDialogForEmercoinHistoryPresenter() {
        dialogEmcHistory = DialogFragmentPleaseWaitWithProgress.newInstance();
        dialogEmcHistory.show(getFragmentManager(), "EmcHistory");
    }

    public void hidePleaseWaitDialogForEmercoinHistoryPresenter() {
        if (dialogEmcHistory != null) {
            dialogEmcHistory.dismissAllowingStateLoss();
        }
    }

    @Override
    public void setDownloadProgress(int count) {

        if (dialog != null) {
            dialog.setDownloadProgress(count);
        }

        if (dialogBtcHistory != null) {
            dialogBtcHistory.setDownloadProgress(count);
        }

        if (dialogEmcHistory != null) {
            dialogEmcHistory.setDownloadProgress(count);
        }
    }

    @Override
    public void setTotalProgress(int count) {

        if (dialog != null) {
            dialog.setTotalProgress(count);
        }

        if (dialogBtcHistory != null) {
            dialogBtcHistory.setTotalProgress(count);
        }

        if (dialogEmcHistory != null) {
            dialogEmcHistory.setTotalProgress(count);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDashboardPresenter.unsubscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            SPHelper.getInstance().getSharedPreferencesLink().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }
}
