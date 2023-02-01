package com.aspanta.emcsec.ui.fragment.settingsFragment;

import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.presenter.settingsPresenter.SettingsPresenter;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWait;
import com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin.EnterPin;
import com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin.SetUpPin;

import java.util.ArrayList;
import java.util.List;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.DISABLE;
import static com.aspanta.emcsec.tools.Config.ENABLE;
import static com.aspanta.emcsec.tools.Config.FROM_SETTINGS;
import static com.aspanta.emcsec.tools.StringUtils.getCurrentCurrency;

public class SettingsFragment extends Fragment implements IBaseFragment, AdapterView.OnItemSelectedListener {

    private RelativeLayout mRlAddressesForChange, mRlServers, mRlFingerprint;
    private Switch mSwitchPinCode;
    private Switch mSwitchFingerprint;
    private Spinner mSpinnerCurrencySettings;
    private SettingsPresenter mPresenter;
    private DialogFragmentPleaseWait dialog;
    private ArrayAdapter dataAdapter;
    private FingerprintManager mFingerprintManager;
    private boolean isFingerprintAvailable = false;

    private List<String> listOfCurrencies = new ArrayList<>();
    SPHelper mSPHelper = SPHelper.getInstance();

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingsPresenter(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);

        listOfCurrencies.add("USD");
        listOfCurrencies.add("EUR");
        listOfCurrencies.add("CNY");
        listOfCurrencies.add("RUR");

        dataAdapter = new ArrayAdapter(getContext(), R.layout.item_spinner_menu_currency, listOfCurrencies);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner_menu_currency);

        mSpinnerCurrencySettings.setAdapter(dataAdapter);
        mSpinnerCurrencySettings.setOnItemSelectedListener(this);
        mSpinnerCurrencySettings.setSelection(dataAdapter.getPosition(getCurrentCurrency()));

        mRlAddressesForChange.setOnClickListener(c -> {
            SettingsChangeAddressesFragment settingsChangeAddressesFragment = SettingsChangeAddressesFragment.newInstance();
            ((MainActivity) getActivity())
                    .navigator(settingsChangeAddressesFragment,
                            settingsChangeAddressesFragment.getCurrentTag());
        });

        mRlServers.setOnClickListener(c -> {
            SettingsServersFragment settingsServersFragment = SettingsServersFragment.newInstance();
            ((MainActivity) getActivity())
                    .navigator(settingsServersFragment,
                            settingsServersFragment.getCurrentTag());
        });

        if (mSPHelper.getEnablePin() == ENABLE) {
            mSwitchPinCode.setChecked(true);
            mSwitchFingerprint.setEnabled(true);
        } else {
            mSwitchPinCode.setChecked(false);
            mSwitchFingerprint.setEnabled(false);
        }

        mSwitchPinCode.setOnClickListener(v -> {
            if (mSPHelper.getPin().isEmpty()) {
                SetUpPin.newInstance(FROM_SETTINGS).show(getFragmentManager(), "");
            } else {
                EnterPin.newInstance(FROM_SETTINGS).show(getFragmentManager(), "");
            }
        });

        mSwitchFingerprint.setOnClickListener(v ->
                new Thread(() -> {
                    int enable = mSPHelper.getEnableFingerprint();
                    if (enable == ENABLE) {
                        mSPHelper.enableFingerprint(DISABLE);
                        new Handler(Looper.getMainLooper()).post(() ->
                                mSwitchPinCode.setEnabled(true));
                    } else {
                        mSPHelper.enableFingerprint(ENABLE);
                        new Handler(Looper.getMainLooper()).post(() ->
                                mSwitchPinCode.setEnabled(false));
                    }
                }).start()
        );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initLayoutForFingerprint();
    }

    private void initLayoutForFingerprint() {
        if (!isFingerprintAuthAvailable()) {
            mRlFingerprint.setVisibility(View.GONE);
        } else {
            mRlFingerprint.setVisibility(View.VISIBLE);
            if (mSPHelper.getEnableFingerprint() == ENABLE) {
                mSwitchPinCode.setEnabled(false);
                mSwitchFingerprint.setChecked(true);
            } else {
                mSwitchPinCode.setEnabled(true);
                mSwitchFingerprint.setChecked(false);
            }
        }
    }

    public boolean isFingerprintAuthAvailable() throws SecurityException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mFingerprintManager == null) {
                mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            }

            isFingerprintAvailable = mFingerprintManager.isHardwareDetected()
                    && mFingerprintManager.hasEnrolledFingerprints()
                    && ((KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE)).isDeviceSecure();
        }
        return isFingerprintAvailable;
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).navigatorBackPressed(DashboardFragment.newInstance(""));
    }

    @Override
    public String getCurrentTag() {
        return getClass().getName();
    }

    private void init(View view) {

        mRlAddressesForChange = view.findViewById(R.id.rl_addresses_for_the_change_settings);
        mRlServers = view.findViewById(R.id.rl_servers_settings);
        mRlFingerprint = view.findViewById(R.id.rl_fingerprint);
        mSwitchPinCode = view.findViewById(R.id.switch_pin_code_settings);
        mSwitchFingerprint = view.findViewById(R.id.switch_fingerprint);
        mSpinnerCurrencySettings = view.findViewById(R.id.spinner_currency_settings);
        mSpinnerCurrencySettings.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (listOfCurrencies.get(i).equals("RUR")) {
            if (!SPHelper.getInstance().getStringValue(CURRENT_CURRENCY).equals("RUB")) {
                mPresenter.getCourses(mSpinnerCurrencySettings, dataAdapter, "RUB");
            }
        } else {
            if (!SPHelper.getInstance().getStringValue(CURRENT_CURRENCY).equals(listOfCurrencies.get(i))) {
                mPresenter.getCourses(mSpinnerCurrencySettings, dataAdapter, listOfCurrencies.get(i));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void showPleaseWaitDialog() {
        dialog = DialogFragmentPleaseWait.newInstance();
        dialog.show(getFragmentManager(), "");
    }

    public void hidePleaseWaitDialog() {
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubscribe();
    }

    public Switch getSwitchFingerprint() {
        return mSwitchFingerprint;
    }

    public Switch getSwitchPinCode() {
        return mSwitchPinCode;
    }
}