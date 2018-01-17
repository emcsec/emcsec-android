package com.aspanta.emcsec.ui.fragment.bitcoinOperationsFragment;

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
import com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment.BitcoinAddressesFragment;
import com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment;
import com.aspanta.emcsec.ui.fragment.historyFragment.bitcoinHistoryFragment.BitcoinHistoryFragment;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.bitcoinReceiveFragment.BitcoinReceiveFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendBitcoinFragment.SendBitcoinFragment;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;


public class BitcoinOperationsFragment extends Fragment implements IBaseFragment {

    public final String TAG = getClass().getName();

    private RelativeLayout mRlMyAddressesBitcoin, mRlSendBitcoin, mRlReceiveCoins, mRlHistory;
    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc;

    public static BitcoinOperationsFragment newInstance() {
        return new BitcoinOperationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bitcoin_operations, container, false);
        init(view);

        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceBtc.setText(SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
        String wholeRowBalanceBtc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> BTC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceBtc.setText(Html.fromHtml(wholeRowBalanceBtc));

        mRlMyAddressesBitcoin.setOnClickListener(v ->
                ((MainActivity) getActivity())
                        .navigator(BitcoinAddressesFragment.newInstance(),
                                BitcoinAddressesFragment.newInstance().getCurrentTag()));

        mRlSendBitcoin.setOnClickListener(v ->
                ((MainActivity) getActivity())
                        .navigator(SendBitcoinFragment.newInstance(),
                                SendBitcoinFragment.newInstance().getCurrentTag()));

        mRlReceiveCoins.setOnClickListener(v ->
                ((MainActivity) getActivity())
                        .navigator(BitcoinReceiveFragment.newInstance("", ""),
                                BitcoinReceiveFragment.newInstance("", "").getCurrentTag()));

        mRlHistory.setOnClickListener(v ->
                ((MainActivity) getActivity())
                        .navigator(BitcoinHistoryFragment.newInstance(),
                                BitcoinHistoryFragment.newInstance().getCurrentTag())
        );

        return view;
    }

    private void init(View view) {
        mRlMyAddressesBitcoin = view.findViewById(R.id.rl_my_addresses_dashboard_bitcoin);
        mRlSendBitcoin = view.findViewById(R.id.rl_send_coins_dashboard_bitcoin);
        mRlReceiveCoins = view.findViewById(R.id.rl_receive_coins_dashboard_bitcoin);
        mRlHistory = view.findViewById(R.id.rl_history_btc);

        mTvBalanceBtc = view.findViewById(R.id.tv_balance_btc);
        mTvWholeRowBalanceBtc = view.findViewById(R.id.tv_whole_row_btc);
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
