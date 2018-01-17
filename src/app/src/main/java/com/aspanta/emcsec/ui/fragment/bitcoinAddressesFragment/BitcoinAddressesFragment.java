package com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.presenter.bitcoinAddressPresenter.BitcoinAddressesPresenter;
import com.aspanta.emcsec.presenter.bitcoinAddressPresenter.IBitcoinAddressesPresenter;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment.adapter.BitcoinAddressesAdapter;
import com.aspanta.emcsec.ui.fragment.bitcoinOperationsFragment.BitcoinOperationsFragment;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;


public class BitcoinAddressesFragment extends Fragment implements IBitcoinAddressesFragment {

    public final String TAG = getClass().getName();

    private RecyclerView mRecyclerView;
    private IBitcoinAddressesPresenter mPresenter;
    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc;
    private BitcoinAddressesAdapter mAdresserAdapter;
    private List<BtcAddress> mListAddresses;

    public static BitcoinAddressesFragment newInstance() {
        return new BitcoinAddressesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new BitcoinAddressesPresenter(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bitcoin_addresses, container, false);
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

        mPresenter.getAddressesList();

        return view;
    }

    @Override
    public void onBackPressed() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.navigatorBackPressed(BitcoinOperationsFragment.newInstance());
    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }

    @Override
    public void setAddressesList(List<BtcAddress> addresses) {

        mListAddresses = addresses;
        mAdresserAdapter = new BitcoinAddressesAdapter(addresses, mPresenter);
        mRecyclerView.setAdapter(mAdresserAdapter);

    }

    @Override
    public void showPleaseWaitDialog() {
    }

    @Override
    public void hidePleaseWaitDialog() {
    }

    @Override
    public List<BtcAddress> getAddressesList() {
        return mListAddresses;
    }

    @Override
    public void updateAddresses() {
        mAdresserAdapter = new BitcoinAddressesAdapter(mListAddresses, mPresenter);
        mRecyclerView.setAdapter(mAdresserAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void init(View view) {

        mRecyclerView = view.findViewById(R.id.rv_bitcoin_addresses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mTvBalanceBtc = view.findViewById(R.id.tv_balance_btc);
        mTvWholeRowBalanceBtc = view.findViewById(R.id.tv_whole_row_btc);
    }
}

