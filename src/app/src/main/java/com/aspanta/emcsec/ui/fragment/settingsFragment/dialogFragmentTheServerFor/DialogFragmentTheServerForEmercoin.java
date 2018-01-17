package com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentTheServerFor;

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
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsServersFragment;
import com.aspanta.emcsec.ui.fragment.settingsFragment.SettingsServersFragmentForGenerateActivity;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.aspanta.emcsec.tools.Config.SERVER_HOST_EMC;
import static com.aspanta.emcsec.tools.Config.SERVER_PORT_EMC;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;

public class DialogFragmentTheServerForEmercoin extends DialogFragment {

    private Button mBtnSave, mBtnCancel;
    private EditText mEtHost, mEtPort;
    private Observable<Boolean> hostObservable, portObservable;
    private Disposable disposable;
    static SettingsServersFragment sSettingsServersFragment;
    static SettingsServersFragmentForGenerateActivity sSettingsServersFragmentForGenerateActivity;

    public static DialogFragmentTheServerForEmercoin newInstance(
            IBaseFragment settingsServersFragment) {

        DialogFragmentTheServerForEmercoin fragment = new DialogFragmentTheServerForEmercoin();
        try {
            sSettingsServersFragment = (SettingsServersFragment) settingsServersFragment;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            sSettingsServersFragmentForGenerateActivity = (SettingsServersFragmentForGenerateActivity) settingsServersFragment;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_the_server_for_emercoin, container, false);
        init(view);

        mEtHost.setText(SharedPreferencesHelper.getInstance().getStringValue(SERVER_HOST_EMC));
        mEtPort.setText(SharedPreferencesHelper.getInstance().getIntValue(SERVER_PORT_EMC) + "");

        setCancelable(false);
        validateFields();

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mBtnSave.setOnClickListener(o -> {

            SharedPreferencesHelper.getInstance().putStringValue(SERVER_HOST_EMC, mEtHost.getText().toString());
            SharedPreferencesHelper.getInstance().putIntValue(SERVER_PORT_EMC, Integer.parseInt(mEtPort.getText().toString()));

            if (sSettingsServersFragment != null) {
                sSettingsServersFragment.setHostPortSSLEmc();
            }

            if (sSettingsServersFragmentForGenerateActivity != null) {
                sSettingsServersFragmentForGenerateActivity.setHostPortSSLEmc();
            }

            dismiss();
        });
        mBtnCancel.setOnClickListener(o -> dismiss());
        return view;
    }

    private void validateFields() {

        hostObservable = textChanges(mEtHost)
                .map(input -> !input.toString().isEmpty());

        portObservable = textChanges(mEtPort)
                .map(input -> !input.toString().isEmpty());

        disposable = Observable.combineLatest(
                hostObservable, portObservable,
                (hostBoolean, portBoolean) ->
                        hostBoolean && portBoolean)
                .subscribe(isValid -> mBtnSave.setEnabled(isValid));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void init(View view) {
        mBtnSave = view.findViewById(R.id.btn_save_server_emercoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_server_emercoin);
        mEtHost = view.findViewById(R.id.et_host_emercoin);
        mEtPort = view.findViewById(R.id.et_port_emercoin);
    }
}
