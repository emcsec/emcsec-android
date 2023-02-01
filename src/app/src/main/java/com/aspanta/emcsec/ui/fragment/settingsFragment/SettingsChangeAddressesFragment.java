package com.aspanta.emcsec.ui.fragment.settingsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.DialogFragmentAddressesForTheChangeBtc;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.DialogFragmentAddressesForTheChangeEmc;

import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_BTC;
import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_EMC;


public class SettingsChangeAddressesFragment extends Fragment implements IBaseFragment {

    private ImageView mIvEditChangeAddressEmc, mIvEditChangeAddressBtc;
    private TextView mTvAddressChangeBtc, mTvAddressChangeEmc;
    private RelativeLayout mRlAddressChangeEmc, mRlAddressChangeBtc;

    public SettingsChangeAddressesFragment() {
    }

    public static SettingsChangeAddressesFragment newInstance() {
        return new SettingsChangeAddressesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings_change_addresses, container, false);
        init(view);

        mIvEditChangeAddressEmc.setOnClickListener(c ->
                DialogFragmentAddressesForTheChangeEmc.newInstance(this, mTvAddressChangeEmc.getText().toString())
                        .show(getFragmentManager(), "")
        );

        mIvEditChangeAddressBtc.setOnClickListener(c ->
                DialogFragmentAddressesForTheChangeBtc.newInstance(this, mTvAddressChangeBtc.getText().toString())
                        .show(getFragmentManager(), "")
        );

        mRlAddressChangeEmc.setOnClickListener(c ->
                DialogFragmentAddressesForTheChangeEmc.newInstance(this, mTvAddressChangeEmc.getText().toString())
                        .show(getFragmentManager(), "")
        );

        mRlAddressChangeBtc.setOnClickListener(c ->
                DialogFragmentAddressesForTheChangeBtc.newInstance(this, mTvAddressChangeBtc.getText().toString())
                        .show(getFragmentManager(), "")
        );

        return view;
    }

    @Override
    public void onBackPressed() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.navigatorBackPressed(SettingsFragment.newInstance());
    }

    @Override
    public String getCurrentTag() {
        return null;
    }

    private void init(View view) {
        mIvEditChangeAddressBtc = view.findViewById(R.id.iv_edit_change_address_btc);
        mIvEditChangeAddressEmc = view.findViewById(R.id.iv_edit_change_address_emc);
        mTvAddressChangeBtc = view.findViewById(R.id.tv_address_for_the_change_btc);
        mTvAddressChangeEmc = view.findViewById(R.id.tv_address_for_the_change_emc);
        mRlAddressChangeEmc = view.findViewById(R.id.rl_address_for_the_change_in_emc);
        mRlAddressChangeBtc = view.findViewById(R.id.rl_address_for_the_change_in_btc);

        mTvAddressChangeBtc.setText(SPHelper.getInstance().getStringValue(CHANGE_ADDRESS_BTC));
        mTvAddressChangeEmc.setText(SPHelper.getInstance().getStringValue(CHANGE_ADDRESS_EMC));


    }

    public void setAddressForChangeBtc() {
        mTvAddressChangeBtc.setText(SPHelper.getInstance().getStringValue(CHANGE_ADDRESS_BTC));
    }

    public void setAddressForChangeEmc() {
        mTvAddressChangeEmc.setText(SPHelper.getInstance().getStringValue(CHANGE_ADDRESS_EMC));
    }
}
