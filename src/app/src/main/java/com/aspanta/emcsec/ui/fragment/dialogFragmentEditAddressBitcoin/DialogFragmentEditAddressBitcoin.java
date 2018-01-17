package com.aspanta.emcsec.ui.fragment.dialogFragmentEditAddressBitcoin;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.presenter.bitcoinAddressPresenter.IBitcoinAddressesPresenter;


public class  DialogFragmentEditAddressBitcoin extends DialogFragment {

    static IBitcoinAddressesPresenter mIBitcoinAddressesPresenter;
    static BtcAddress mAddressPojo;
    private Button mBtnSave, mBtnCancel;
    private EditText mEtLabel;

    public static DialogFragmentEditAddressBitcoin newInstance(IBitcoinAddressesPresenter bitcoinAddressesPresenter,
                                                               BtcAddress addressPojo) {
        DialogFragmentEditAddressBitcoin fragment = new DialogFragmentEditAddressBitcoin();
        mIBitcoinAddressesPresenter = bitcoinAddressesPresenter;
        mAddressPojo = addressPojo;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_edit_address_bitcoin, container, false);
        init(view);
        setCancelable(false);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mEtLabel.setText(mAddressPojo.getLabel());
        mBtnSave.setOnClickListener(o -> {
            mAddressPojo.setLabel(mEtLabel.getText().toString());
            mIBitcoinAddressesPresenter.editAddress(mAddressPojo);
            dismiss();
        });
        mBtnCancel.setOnClickListener(o -> dismiss());
        return view;
    }

    private void init(View view) {
        mBtnSave = view.findViewById(R.id.btn_save_edit_address_bitcoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_edit_address_bitooin);
        mEtLabel = view.findViewById(R.id.et_label_edit_address_bitcoin);
    }
}

