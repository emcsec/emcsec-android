package com.aspanta.emcsec.ui.fragment.dialogFragmentSetUpPin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPin.DialogFragmentSetUpAndConfirmPin;

import static com.aspanta.emcsec.tools.Config.PIN_CODE;
import static com.aspanta.emcsec.tools.Config.SET_PIN_CODE_OR_NOT;


public class DialogFragmentSetUpPin extends DialogFragment {

    private Button mBtnYes, mBtnCancel;

    public static DialogFragmentSetUpPin newInstance() {

        return new DialogFragmentSetUpPin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_set_up_pin, container, false);
        init(view);

        setCancelable(false);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mBtnYes.setOnClickListener(o -> {
            SharedPreferencesHelper.getInstance().putStringValue(SET_PIN_CODE_OR_NOT, "yes");
            DialogFragmentSetUpAndConfirmPin.newInstance(null).show(getFragmentManager(), "");
            dismiss();
        });

        mBtnCancel.setOnClickListener(o -> {
            SharedPreferencesHelper.getInstance().putStringValue(PIN_CODE, "not");
            SharedPreferencesHelper.getInstance().putStringValue(SET_PIN_CODE_OR_NOT, "not");
            dismiss();
        });
        return view;
    }

    private void init(View view) {
        mBtnYes = view.findViewById(R.id.btn_ok_would_you_like_to_set_up_pin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_would_you_like_to_set_up_pin);
    }
}
