package com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin;

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
import com.aspanta.emcsec.db.SPHelper;

import static com.aspanta.emcsec.tools.Config.ENABLE;

public class DialogSetUpFingerprint extends DialogFragment {

    private Button mBtnYes, mBtnCancel;

    public static DialogSetUpFingerprint newInstance() {
        return new DialogSetUpFingerprint();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_set_up_fingerprint, container, false);
        init(view);
        setCancelable(false);
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mBtnYes.setOnClickListener(v -> {
            SPHelper.getInstance().enableFingerprint(ENABLE);
            dismiss();
        });

        mBtnCancel.setOnClickListener(v -> dismiss());
        return view;
    }

    private void init(View view) {
        mBtnYes = view.findViewById(R.id.btn_ok);
        mBtnCancel = view.findViewById(R.id.btn_cancel);
    }
}
