package com.aspanta.emcsec.ui.fragment.emercoinOperationsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment;
import com.aspanta.emcsec.ui.fragment.emercoinAddressesFragment.EmercoinAddressesFragment;
import com.aspanta.emcsec.ui.fragment.historyFragment.emercoinHistoryFragment.EmercoinHistoryFragment;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.emercoinReceiveFragment.EmercoinReceiveFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendEmercoinFragment.SendEmercoinFragment;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_COURSE_KEY;

public class EmercoinOperationsFragment extends Fragment implements IBaseFragment {

    public static final String TAG = "EmercoinOperationsFragment";

    RelativeLayout mRlMyAddressEmercoin, mRlSendEmercoin, mRlReceiveCoins, mRlHistory;
    private TextView mTvBalanceEmc, mTvWholeRowBalanceEmc;

    public static EmercoinOperationsFragment newInstance() {
        return new EmercoinOperationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_emercoin_operations, container, false);
        init(v);

        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceEmc.setText(SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        String wholeRowBalanceEmc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(EMC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        mRlMyAddressEmercoin.setOnClickListener(view ->
                ((MainActivity) getActivity())
                        .navigator(EmercoinAddressesFragment.newInstance(),
                                EmercoinAddressesFragment.newInstance().getCurrentTag()));

        mRlSendEmercoin.setOnClickListener(view ->
                ((MainActivity) getActivity())
                        .navigator(SendEmercoinFragment.newInstance(),
                                SendEmercoinFragment.newInstance().getCurrentTag()));

        mRlReceiveCoins.setOnClickListener(view ->
                ((MainActivity) getActivity())
                        .navigator(EmercoinReceiveFragment.newInstance("", ""),
                                EmercoinReceiveFragment.newInstance("", "").getCurrentTag()));

        mRlHistory.setOnClickListener(view ->
                ((MainActivity) getActivity())
                        .navigator(EmercoinHistoryFragment.newInstance(),
                                EmercoinHistoryFragment.newInstance().getCurrentTag())
        );

        return v;
    }

    private void init(View v) {
        mRlSendEmercoin = v.findViewById(R.id.rl_send_coins_dashboard_emercoin);
        mRlMyAddressEmercoin = v.findViewById(R.id.rl_my_addresses_dashboard_emercoin);
        mRlReceiveCoins = v.findViewById(R.id.rl_receive_coins_dashboard_emercoin);
        mRlHistory = v.findViewById(R.id.rl_history_emc);

        mTvBalanceEmc = v.findViewById(R.id.tv_balance_emc);
        mTvWholeRowBalanceEmc = v.findViewById(R.id.tv_whole_row_emc);
    }

    @Override
    public void onBackPressed() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.navigatorBackPressed(DashboardFragment.newInstance(""));
    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }
}
