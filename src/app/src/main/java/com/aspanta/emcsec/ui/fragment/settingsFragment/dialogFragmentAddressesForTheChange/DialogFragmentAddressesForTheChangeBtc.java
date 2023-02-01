package com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsChangeAddressesFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.adapter.AddressesForChangeAdapterBtc;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_BTC;

public class DialogFragmentAddressesForTheChangeBtc extends DialogFragment {

    private Button mBtnSave, mBtnCancel;
    private RecyclerView mRvChangeAddressesBtc;
    private List<BtcAddressForChange> mListBtcAddresses;
    private static String sCurrentAddress;
    private AddressesForChangeAdapterBtc mAddressesForChangeAdapterBtc;
    public static String addressForChangeBtc = SPHelper.getInstance().getStringValue(CHANGE_ADDRESS_BTC);

    static SettingsChangeAddressesFragment sSettingsChangeAddressesFragment;

    public static DialogFragmentAddressesForTheChangeBtc newInstance(SettingsChangeAddressesFragment settingsChangeAddressesFragment, String currentAddress) {
        DialogFragmentAddressesForTheChangeBtc fragment = new DialogFragmentAddressesForTheChangeBtc();
        sSettingsChangeAddressesFragment = settingsChangeAddressesFragment;
        sCurrentAddress = currentAddress;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_addresses_for_the_change_btc, container, false);
        init(view);
        setCancelable(false);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mListBtcAddresses = App.getDbInstance().btcAddressForChangeDao().getAll();
        setBtcAddressesList(mListBtcAddresses);

        mBtnSave.setOnClickListener(o -> {
            SPHelper.getInstance().putStringValue(CHANGE_ADDRESS_BTC, addressForChangeBtc);
            sSettingsChangeAddressesFragment.setAddressForChangeBtc();
            dismiss();
        });
        mBtnCancel.setOnClickListener(o -> dismiss());
        return view;
    }

    private void init(View view) {

        mRvChangeAddressesBtc = view.findViewById(R.id.rv_change_addresses_btc);
        mRvChangeAddressesBtc.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvChangeAddressesBtc.setHasFixedSize(true);

        mBtnSave = view.findViewById(R.id.btn_save_address_for_the_change_bitcoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_address_for_the_change_bitcoin);
    }

    public void setBtcAddressesList(List<BtcAddressForChange> btcAddressesList) {

        mAddressesForChangeAdapterBtc =
                new AddressesForChangeAdapterBtc(btcAddressesList, sCurrentAddress);
        mRvChangeAddressesBtc.setAdapter(mAddressesForChangeAdapterBtc);
    }
}
