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
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;
import com.aspanta.emcsec.presenter.historyPresenter.HistoryEmercoinPresenter;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWaitWithProgress;
import com.aspanta.emcsec.ui.fragment.emercoinOperationsFragment.EmercoinOperationsFragment;
import com.aspanta.emcsec.ui.fragment.historyFragment.adapter.TransactionsEmercoinAdapter;
import com.reginald.swiperefresh.CustomSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.showAlertDialog;
import static com.aspanta.emcsec.tools.InternetConnection.internetConnectionChecking;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;


public class EmercoinHistoryFragment extends Fragment implements IEmercoinHistoryFragment, IBaseFragment {

    HistoryEmercoinPresenter mPresenter;
    private TextView mTvBalanceEmc, mTvWholeRowBalanceEmc, mTvNoTransactions;
    private RecyclerView mRvHistoryEmc;
    TransactionsEmercoinAdapter transactionsEmercoinAdapter;
    private DialogFragmentPleaseWaitWithProgress dialog;
    private List<EmcTransaction> mEmcTransactionList = new ArrayList<>();
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public static EmercoinHistoryFragment newInstance() {
        return new EmercoinHistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new HistoryEmercoinPresenter(getContext(), this, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            SharedPreferencesHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emercoin_history, container, false);
        init(v);

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

        mTvBalanceEmc.setText(SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        String wholeRowBalanceEmc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(EMC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        String finalCurrentCurrency = currentCurrency;
        listener = (sharedPreferences, key) -> {
            mTvBalanceEmc.setText(SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
            mTvWholeRowBalanceEmc.setText(Html.fromHtml("<b>~" + SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                    finalCurrentCurrency + " (" + "<b>1 </b> EMC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(EMC_COURSE_KEY) + " </b>" +
                    finalCurrentCurrency + ")"));
        };
        SharedPreferencesHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);

        mEmcTransactionList = App.getDbInstance().emcTransactionDao().getAll();
        setEmcTransactionsList(mEmcTransactionList);

        return v;
    }

    public void onBackPressed() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.navigatorBackPressed(EmercoinOperationsFragment.newInstance());
    }

    public void onDestroy() {
        super.onDestroy();
//        mPresenter.unsubscribe();
    }

    @Override
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

        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_emercoin_history);
    }

    public void setEmcTransactionsList(List<EmcTransaction> emcTransactionsList) {

        mTvNoTransactions.setVisibility(View.GONE);

        if (emcTransactionsList.isEmpty()) {
            mTvNoTransactions.setVisibility(View.VISIBLE);
        }

        transactionsEmercoinAdapter =
                new TransactionsEmercoinAdapter(emcTransactionsList, getContext());
        mRvHistoryEmc.setAdapter(transactionsEmercoinAdapter);
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
}