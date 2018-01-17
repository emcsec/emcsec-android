package com.aspanta.emcsec.ui.fragment.dialogFragmentOperationDone;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.presenter.enterAnAddressBitcoinPresenter.IEnterAnAddressBitcoinPresenter;
import com.aspanta.emcsec.presenter.enterAnAddressEmercoinPresenter.IEnterAnAddressEmercoinPresenter;

public class DialogFragmentOperationDone extends DialogFragment {

    private static IEnterAnAddressEmercoinPresenter sEnterAnAddressEmercoinPresenter;
    private static IEnterAnAddressBitcoinPresenter sEnterAnAddressBitcoinPresenter;

    public static DialogFragmentOperationDone newInstance(IEnterAnAddressEmercoinPresenter enterAnAddressEmercoinPresenter, IEnterAnAddressBitcoinPresenter enterAnAddressBitcoinPresenter) {
        sEnterAnAddressEmercoinPresenter = enterAnAddressEmercoinPresenter;
        sEnterAnAddressBitcoinPresenter = enterAnAddressBitcoinPresenter;
        return new DialogFragmentOperationDone();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_operation_done, container, false);
        setCancelable(false);
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        view.findViewById(R.id.btn_ok).setOnClickListener(c -> {
            if (sEnterAnAddressEmercoinPresenter != null) {
                sEnterAnAddressEmercoinPresenter.getEmcBalance();
            } else if (sEnterAnAddressBitcoinPresenter != null) {
                sEnterAnAddressBitcoinPresenter.getBtcBalance();
            }
            dismiss();
        });
        return view;
    }
}
