package com.aspanta.emcsec.ui.fragment.settingsFragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
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
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.presenter.settingsPresenter.SettingsPresenter;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPin.DialogFragmentEnterPinForSettings;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPin.DialogFragmentSetUpAndConfirmPin;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWait;

import java.util.ArrayList;
import java.util.List;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.PIN_CODE;
import static com.aspanta.emcsec.tools.Config.SET_PIN_CODE_OR_NOT;

public class SettingsFragment extends Fragment implements IBaseFragment, AdapterView.OnItemSelectedListener {

    private RelativeLayout mRlAddressesForChange, mRlServers;
    private Switch mSwitchPinCode;
    private Spinner mSpinnerCurrencySettings;
    private SettingsPresenter mPresenter;
    private DialogFragmentPleaseWait dialog;
    private ArrayAdapter dataAdapter;

    private List<String> listOfCurrencies = new ArrayList<>();

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
        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }
        mSpinnerCurrencySettings.setSelection(dataAdapter.getPosition(currentCurrency));

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

        if (SharedPreferencesHelper.getInstance().getStringValue(SET_PIN_CODE_OR_NOT).equals("yes")) {
            mSwitchPinCode.setChecked(true);
        } else {
            mSwitchPinCode.setChecked(false);
        }

        mSwitchPinCode.setTag("TAG");
        mSwitchPinCode.setOnCheckedChangeListener((compoundButton, b) -> {

                    if (mSwitchPinCode.getTag() != null) {
                        mSwitchPinCode.setTag(null);
                        return;
                    }

                    if (b) {
                        if (SharedPreferencesHelper.getInstance().getStringValue(PIN_CODE).equals("not")) {
                            DialogFragmentSetUpAndConfirmPin.newInstance(mSwitchPinCode).show(getFragmentManager(), "");
                        } else {
                            DialogFragmentEnterPinForSettings.newInstance(mSwitchPinCode).show(getFragmentManager(), "");
//                            SharedPreferencesHelper.getInstance().putStringValue(SET_PIN_CODE_OR_NOT, "yes");
                        }
                    } else {
                        DialogFragmentEnterPinForSettings.newInstance(mSwitchPinCode).show(getFragmentManager(), "");
//                        SharedPreferencesHelper.getInstance().putStringValue(SET_PIN_CODE_OR_NOT, "not");
                    }
                }
        );

        mSwitchPinCode.setOnTouchListener((v, event) -> {
            mSwitchPinCode.setTag(null);
            return false;
        });
        return view;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public String getCurrentTag() {
        return getClass().getName();
    }

    private void init(View view) {

        mRlAddressesForChange = view.findViewById(R.id.rl_addresses_for_the_change_settings);
        mRlServers = view.findViewById(R.id.rl_servers_settings);
        mSwitchPinCode = view.findViewById(R.id.switch_pin_code_settings);
        mSpinnerCurrencySettings = view.findViewById(R.id.spinner_currency_settings);
        mSpinnerCurrencySettings.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (listOfCurrencies.get(i).equals("RUR")) {
            if (!SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY).equals("RUB")) {
                mPresenter.getCourses(mSpinnerCurrencySettings, dataAdapter, "RUB");
            }
        } else {
            if (!SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY).equals(listOfCurrencies.get(i))) {
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
        mPresenter.unsubscribe();
    }
}