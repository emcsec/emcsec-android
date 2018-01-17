package com.aspanta.emcsec.ui.fragment.emercoinAddressesFragment;

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
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.presenter.emercoinAddressesPresenter.EmercoinAddressesPresenter;
import com.aspanta.emcsec.presenter.emercoinAddressesPresenter.IEmercoinAddressesPresenter;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.emercoinAddressesFragment.adapter.EmercoinAddressesAdapter;
import com.aspanta.emcsec.ui.fragment.emercoinOperationsFragment.EmercoinOperationsFragment;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_COURSE_KEY;


public class EmercoinAddressesFragment extends Fragment implements IEmercoinAddressesFragment {

    public final String TAG = getClass().getName();

    private IEmercoinAddressesPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private TextView mTvBalanceEmc, mTvWholeRowBalanceEmc;
    private EmercoinAddressesAdapter mAdresserAdapter;
    private List<EmcAddress> mListAddresses;

    public static EmercoinAddressesFragment newInstance() {
        return new EmercoinAddressesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new EmercoinAddressesPresenter(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emercoin_addresses, container, false);
        init(view);

        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceEmc.setText(SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        String wholeRowBalanceEmc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(EMC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        mPresenter.getAddressesList();

        return view;
    }

    @Override
    public void onBackPressed() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.navigatorBackPressed(EmercoinOperationsFragment.newInstance());
    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }

    @Override
    public void setAddressesList(List<EmcAddress> addressesList) {
        mListAddresses = addressesList;
        mAdresserAdapter = new EmercoinAddressesAdapter(addressesList, mPresenter);
        mRecyclerView.setAdapter(mAdresserAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void init(View view) {

        mRecyclerView = view.findViewById(R.id.rv_emercoin_addresses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mTvBalanceEmc = view.findViewById(R.id.tv_balance_emc);
        mTvWholeRowBalanceEmc = view.findViewById(R.id.tv_whole_row_emc);
    }

    @Override
    public void showPleaseWaitDialog() {
    }

    @Override
    public void hidePleaseWaitDialog() {
    }

    @Override
    public List<EmcAddress> getAddressesList() {
        return mListAddresses;
    }

    @Override
    public void updateAddresses() {
        mAdresserAdapter = new EmercoinAddressesAdapter(mListAddresses, mPresenter);
        mRecyclerView.setAdapter(mAdresserAdapter);
    }
}