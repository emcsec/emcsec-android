package com.aspanta.emcsec.ui.fragment.historyFragment.emercoinHistoryFragment;


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
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;
import com.aspanta.emcsec.presenter.historyPresenter.HistoryEmercoinPresenter;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWaitWithProgress;
import com.aspanta.emcsec.ui.fragment.historyFragment.adapter.TransactionsEmercoinAdapter;
import com.reginald.swiperefresh.CustomSwipeRefreshLayout;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.ui.activities.MainActivity.showAlertDialog;
import static com.aspanta.emcsec.tools.InternetConnection.internetConnectionChecking;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;


public class EmercoinHistoryFragmentB extends Fragment implements IEmercoinHistoryFragment {

    HistoryEmercoinPresenter mPresenter;
    private TextView mTvBalanceEmc, mTvWholeRowBalanceEmc, mTvNoTransactions;
    private RecyclerView mRvHistoryEmc;
    private TransactionsEmercoinAdapter transactionsEmercoinAdapter;
    private DialogFragmentPleaseWaitWithProgress dialog;
    private List<EmcTransaction> mEmcTransactionList = App.getDbInstance().emcTransactionDao().getAll();
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public static EmercoinHistoryFragmentB newInstance() {
        return new EmercoinHistoryFragmentB();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            SPHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new HistoryEmercoinPresenter(getActivity(), this, null);
        }
//        setUserVisibleHint(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_emercoin_history_b, container, false);
        init(v);

        if (!mEmcTransactionList.isEmpty()) {
            setEmcTransactionsList(mEmcTransactionList);
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
                showAlertDialog(getActivity(), error);
                mSwipeRefreshLayout.refreshComplete();

            }
        });

        String currentCurrency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceEmc.setText(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        String wholeRowBalanceEmc = "<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        String finalCurrentCurrency = currentCurrency;
        listener = (sharedPreferences, key) -> {
            mTvBalanceEmc.setText(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
            mTvWholeRowBalanceEmc.setText(Html.fromHtml("<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                    finalCurrentCurrency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                    finalCurrentCurrency + ")"));
        };
        SPHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);

        return v;
    }

    public String getCurrentTag() {
        return getClass().getName();
    }

    private void init(View v) {

        mRvHistoryEmc = v.findViewById(R.id.rv_history_emercoin);
        mRvHistoryEmc.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvHistoryEmc.setHasFixedSize(true);

        mTvNoTransactions = v.findViewById(R.id.tv_no_transactions);

        mTvBalanceEmc = v.findViewById(R.id.tv_balance_emc);
        mTvWholeRowBalanceEmc = v.findViewById(R.id.tv_whole_row_emc);

        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_emercoin_history_b);
    }

    public void setEmcTransactionsList(List<EmcTransaction> emcTransactionsList) {

        mTvNoTransactions.setVisibility(View.GONE);

        if (emcTransactionsList.isEmpty()) {
            mTvNoTransactions.setVisibility(View.VISIBLE);
        }

        mEmcTransactionList = emcTransactionsList;

        transactionsEmercoinAdapter =
                new TransactionsEmercoinAdapter(mEmcTransactionList, getContext());
        mRvHistoryEmc.setAdapter(transactionsEmercoinAdapter);
    }

    public void showPleaseWaitDialog() {
        dialog = DialogFragmentPleaseWaitWithProgress.newInstance();
        dialog.show(getFragmentManager(), "");
    }

    public void hidePleaseWaitDialog() {
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
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
            SPHelper.getInstance().getSharedPreferencesLink().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

//    @Override
//    public void setMenuVisibility(final boolean visible) {
//        super.setMenuVisibility(visible);
//
//        if (mPresenter == null) {
//            mPresenter = new HistoryEmercoinPresenter(getContext(), this, null);
//        }
//
//        if (visible) {
//            if (mEmcTransactionList.isEmpty()) {
//                mPresenter.getTransactionHistory();
//            }
//        }
//    }
}