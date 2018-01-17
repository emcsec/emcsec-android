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
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentTheServerFor.DialogFragmentTheServerForBitcoin;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentTheServerFor.DialogFragmentTheServerForEmercoin;

import static com.aspanta.emcsec.tools.Config.SERVER_HOST_BTC;
import static com.aspanta.emcsec.tools.Config.SERVER_HOST_EMC;
import static com.aspanta.emcsec.tools.Config.SERVER_PORT_BTC;
import static com.aspanta.emcsec.tools.Config.SERVER_PORT_EMC;


public class SettingsServersFragment extends Fragment implements IBaseFragment {

    private TextView mTvHostEmc, mTvPortEmc, mTvHostBtc, mTvPortBtc;
    private ImageView mIvEditServerEmc, mIvEditServerBtc;
    private RelativeLayout mRlServerEmc, mRlServerBtc;

    public SettingsServersFragment() {
    }

    public static SettingsServersFragment newInstance() {
        return new SettingsServersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_servers, container, false);
        init(view);

        setHostPortSSLEmc();
        setHostPortSSLBtc();

        mIvEditServerEmc.setOnClickListener(c ->
                DialogFragmentTheServerForEmercoin.newInstance(this)
                        .show(getFragmentManager(), "")
        );

        mIvEditServerBtc.setOnClickListener(c ->
                DialogFragmentTheServerForBitcoin.newInstance(this)
                        .show(getFragmentManager(), "")
        );

        mRlServerEmc.setOnClickListener(c ->
                DialogFragmentTheServerForEmercoin.newInstance(this)
                        .show(getFragmentManager(), "")
        );

        mRlServerBtc.setOnClickListener(c ->
                DialogFragmentTheServerForBitcoin.newInstance(this)
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

        mIvEditServerEmc = view.findViewById(R.id.iv_edit_server_emc);
        mIvEditServerBtc = view.findViewById(R.id.iv_edit_server_btc);
        mTvHostEmc = view.findViewById(R.id.tv_host_emercoin);
        mTvPortEmc = view.findViewById(R.id.tv_port_emercoin);
        mTvHostBtc = view.findViewById(R.id.tv_host_bitcoin);
        mTvPortBtc = view.findViewById(R.id.tv_port_bitcoin);
        mRlServerEmc = view.findViewById(R.id.rl_the_server_for_emc);
        mRlServerBtc = view.findViewById(R.id.rl_the_server_for_btc);
    }

    public void setHostPortSSLEmc() {

        mTvHostEmc.setText(SharedPreferencesHelper.getInstance().getStringValue(SERVER_HOST_EMC));
        mTvPortEmc.setText(SharedPreferencesHelper.getInstance().getIntValue(SERVER_PORT_EMC) + "");
    }

    public void setHostPortSSLBtc() {

        mTvHostBtc.setText(SharedPreferencesHelper.getInstance().getStringValue(SERVER_HOST_BTC));
        mTvPortBtc.setText(SharedPreferencesHelper.getInstance().getIntValue(SERVER_PORT_BTC) + "");
    }
}
