package com.aspanta.emcsec.ui.fragment.dialogFragmentAreYouSureWantToClose;

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
import com.aspanta.emcsec.ui.activities.MainActivity;

public class DialogFragmentAreYouSureWantToClose extends DialogFragment {

    private Button mBtnYes, mBtnCancel;

    public static DialogFragmentAreYouSureWantToClose newInstance() {
        return new DialogFragmentAreYouSureWantToClose();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_are_you_sure_want_close, container, false);
        setCancelable(false);
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        init(view);

        mBtnYes.setOnClickListener(o -> {
            ((MainActivity)getActivity()).loqOut();
            dismiss();
        });
        mBtnCancel.setOnClickListener(o -> dismiss());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void init(View view) {
        mBtnYes = view.findViewById(R.id.btn_yes_close_the_wallet);
        mBtnCancel = view.findViewById(R.id.btn_cancel_close_the_wallet);
    }
}
