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
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsChangeAddressesFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.adapter.AddressesForChangeAdapterEmc;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_EMC;

public class DialogFragmentAddressesForTheChangeEmc extends DialogFragment {

    private Button mBtnSave, mBtnCancel;
    private RecyclerView mRvChangeAddressesEmc;
    private List<EmcAddressForChange> mListEmcAddresses;
    private static String sCurrentAddress;
    private AddressesForChangeAdapterEmc mAddressesForChangeAdapterEmc;
    public static String addressForChangeEmc = SPHelper.getInstance().getStringValue(CHANGE_ADDRESS_EMC);

    static SettingsChangeAddressesFragment sSettingsChangeAddressesFragment;

    public static DialogFragmentAddressesForTheChangeEmc newInstance(SettingsChangeAddressesFragment settingsChangeAddressesFragment, String currentAddress) {
        DialogFragmentAddressesForTheChangeEmc fragment = new DialogFragmentAddressesForTheChangeEmc();
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
        View view = inflater.inflate(R.layout.dialog_fragment_addresses_for_the_change_emc, container, false);
        init(view);
        setCancelable(false);
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mListEmcAddresses = App.getDbInstance().emcAddressForChangeDao().getAll();
        setEmcAddressesList(mListEmcAddresses);

        mBtnSave.setOnClickListener(o -> {
            SPHelper.getInstance().putStringValue(CHANGE_ADDRESS_EMC, addressForChangeEmc);
            sSettingsChangeAddressesFragment.setAddressForChangeEmc();
            dismiss();
        });
        mBtnCancel.setOnClickListener(o -> dismiss());
        return view;
    }

    private void init(View view) {
        mRvChangeAddressesEmc = view.findViewById(R.id.rv_change_addresses_emc);
        mRvChangeAddressesEmc.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvChangeAddressesEmc.setHasFixedSize(true);

        mBtnSave = view.findViewById(R.id.btn_save_address_for_the_change_emercoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_address_for_the_change_emercoin);
    }

    public void setEmcAddressesList(List<EmcAddressForChange> emcAddressesList) {

        mAddressesForChangeAdapterEmc =
                new AddressesForChangeAdapterEmc(emcAddressesList, sCurrentAddress);
        mRvChangeAddressesEmc.setAdapter(mAddressesForChangeAdapterEmc);
    }
}
