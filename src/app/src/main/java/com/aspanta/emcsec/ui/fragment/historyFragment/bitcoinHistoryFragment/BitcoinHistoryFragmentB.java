package com.aspanta.emcsec.ui.fragment.historyFragment.bitcoinHistoryFragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.historyPojos.BtcTransaction;
import com.aspanta.emcsec.presenter.historyPresenter.HistoryBitcoinPresenter;
import com.aspanta.emcsec.presenter.historyPresenter.IHistoryPresenter;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWaitWithProgress;
import com.aspanta.emcsec.ui.fragment.historyFragment.adapter.TransactionsBitcoinAdapter;
import com.reginald.swiperefresh.CustomSwipeRefreshLayout;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.showAlertDialog;
import static com.aspanta.emcsec.tools.InternetConnection.internetConnectionChecking;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;


public class BitcoinHistoryFragmentB extends Fragment implements IBitcoinHistoryFragment {

    private IHistoryPresenter mPresenter;
    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc, mTvNoTransactions;
    private RecyclerView mRvHistoryBtc;
    TransactionsBitcoinAdapter transactionsBitcoinAdapter;
    private DialogFragmentPleaseWaitWithProgress dialog;
    private List<BtcTransaction> mBtcTransactionList = App.getDbInstance().btcTransactionDao().getAll();
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public static BitcoinHistoryFragmentB newInstance() {
        return new BitcoinHistoryFragmentB();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            SharedPreferencesHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new HistoryBitcoinPresenter(getContext(), this, null);
        }
//        setUserVisibleHint(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bitcoin_history_b, container, false);
        init(v);

        if (!mBtcTransactionList.isEmpty()) {
            setBtcTransactionsList(mBtcTransactionList);
        }

        mSwipeRefreshLayout.setOnRefreshListener(() ->
        {
            if (internetConnectionChecking(getContext())) {
                downloadProgressDashboard = 0;
                totalProgressDashboard = 0;
                mPresenter.getTransactionHistory();
                mSwipeRefreshLayout.refreshComplete();
            } else {
                String error = getString(R.string.could_not_connect);
                showAlertDialog(getContext(), error);
            }
        });

        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceBtc.setText(SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
        String wholeRowBalanceBtc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> BTC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceBtc.setText(Html.fromHtml(wholeRowBalanceBtc));

        String finalCurrentCurrency = currentCurrency;
        listener = (sharedPreferences, key) -> {
            mTvBalanceBtc.setText(SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
            mTvWholeRowBalanceBtc.setText(Html.fromHtml("<b>~" + SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                    finalCurrentCurrency + " (" + "<b>1 </b> BTC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY) + " </b>" +
                    finalCurrentCurrency + ")"));

        };
        SharedPreferencesHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);


        return v;
    }

    public String getCurrentTag() {
        return getClass().getName();
    }

    private void init(View v) {

        mRvHistoryBtc = v.findViewById(R.id.rv_history_bitcoin);
        mRvHistoryBtc.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvHistoryBtc.setHasFixedSize(true);

        mTvNoTransactions = v.findViewById(R.id.tv_no_transactions);
        mRvHistoryBtc = v.findViewById(R.id.rv_history_bitcoin);

        mTvBalanceBtc = v.findViewById(R.id.tv_balance_btc);
        mTvWholeRowBalanceBtc = v.findViewById(R.id.tv_whole_row_btc);

        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_bitcoin_history_b);
    }

    public void setBtcTransactionsList(List<BtcTransaction> btcTransactionsList) {

        mTvNoTransactions.setVisibility(View.GONE);

        if (btcTransactionsList.isEmpty()) {
            mTvNoTransactions.setVisibility(View.VISIBLE);
        }

        mBtcTransactionList = btcTransactionsList;

        transactionsBitcoinAdapter =
                new TransactionsBitcoinAdapter(mBtcTransactionList, getContext());
        mRvHistoryBtc.setAdapter(transactionsBitcoinAdapter);
    }

    public void showPleaseWaitDialog() {
        dialog = DialogFragmentPleaseWaitWithProgress.newInstance();
        dialog.show(getFragmentManager(), "");
    }

    public void hidePleaseWaitDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void setDownloadProgress(int count) {
        if (dialog != null) {
            dialog.setDownloadProgress(count);
        }
    }

    @Override
    public void setTotalProgress(int count) {
        if (dialog != null) {
            dialog.setTotalProgress(count);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            SharedPreferencesHelper.getInstance().getSharedPreferencesLink().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

//    @Override
//    public void setMenuVisibility(final boolean visible) {
//        super.setMenuVisibility(visible);
//
//        if (mPresenter == null) {
//            mPresenter = new HistoryBitcoinPresenter(getContext(), this, null);
//        }
//
//        if (visible) {
//            if (mBtcTransactionList.isEmpty()) {
//                mPresenter.getTransactionHistory();
//            }
//        }
//    }
}