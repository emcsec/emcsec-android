package com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.presenter.bitcoinAddressPresenter.BitcoinAddressesPresenter;
import com.aspanta.emcsec.presenter.bitcoinAddressPresenter.IBitcoinAddressesPresenter;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment.adapter.BitcoinAddressesAdapter;
import com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment.adapter.BitcoinAddressesForChangeAdapter;
import com.aspanta.emcsec.ui.fragment.bitcoinOperationsFragment.BitcoinOperationsFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentExportPriv.DialogFragmentExportPriv;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;


public class BitcoinAddressesFragment extends Fragment implements IBitcoinAddressesFragment,
        BitcoinAddressesAdapter.OnAddressClickedListener {

    public final String TAG = getClass().getName();

    private RecyclerView mRecyclerView;
    private RecyclerView mRvAddressesForChange;
    private IBitcoinAddressesPresenter mPresenter;
    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc;
    private BitcoinAddressesAdapter mAdresserAdapter;
    private List<BtcAddress> mListAddresses;
    private List<BtcAddressForChange> mBtcAddressesForChange;

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

        String currentCurrency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceBtc.setText(SPHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
        String wholeRowBalanceBtc = "<b>~" + SPHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> BTC = <b>" + SPHelper.getInstance().getStringValue(BTC_EXCHANGE_RATE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceBtc.setText(Html.fromHtml(wholeRowBalanceBtc));

        mPresenter.getAddressesList();
        setAddressesForChange();

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
        mAdresserAdapter = new BitcoinAddressesAdapter(addresses, mPresenter, this);
        mRecyclerView.setAdapter(mAdresserAdapter);

    }

    @SuppressLint("StaticFieldLeak")
    private void setAddressesForChange() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mBtcAddressesForChange = App.getDbInstance().btcAddressForChangeDao().getAll();
                BitcoinAddressesForChangeAdapter adapter =
                        new BitcoinAddressesForChangeAdapter(mBtcAddressesForChange, BitcoinAddressesFragment.this);

                getActivity().runOnUiThread(() -> {
                    mRvAddressesForChange.setAdapter(adapter);
                });
                return null;
            }
        }.execute();

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
        mAdresserAdapter = new BitcoinAddressesAdapter(mListAddresses, mPresenter, this);
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
        mRecyclerView.setNestedScrollingEnabled(false);

        mRvAddressesForChange = view.findViewById(R.id.rv_bitcoin_addresses_change);
        mRvAddressesForChange.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvAddressesForChange.setHasFixedSize(true);
        mRvAddressesForChange.setNestedScrollingEnabled(false);


        mTvBalanceBtc = view.findViewById(R.id.tv_balance_btc);
        mTvWholeRowBalanceBtc = view.findViewById(R.id.tv_whole_row_btc);
    }

    @Override
    public void exportPriv(String address) {

        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        bundle.putString("coin", "btc");

        DialogFragmentExportPriv dialogFragmentExportPriv = DialogFragmentExportPriv.newInstance();
        dialogFragmentExportPriv.setArguments(bundle);
        dialogFragmentExportPriv.show(getFragmentManager(), "");
    }
}