package com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.presenter.enterAnAddressEmercoinPresenter.EnterAnAddressEmercoinPresenter;
import com.aspanta.emcsec.presenter.enterAnAddressEmercoinPresenter.IEnterAnAddressEmercoinPresenter;
import com.aspanta.emcsec.tools.EmercoinNetwork;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.dialogFragmentConfirmOperation.DialogFragmentConfirmOperationEmercoin;
import com.aspanta.emcsec.ui.fragment.dialogFragmentOperationDone.DialogFragmentOperationDone;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWaitWithProgress;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.SendCoinFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendEmercoinFragment.SendEmercoinFragment;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.LegacyAddress;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.REGEX_AMOUNT;
import static com.aspanta.emcsec.tools.Config.REGEX_EMC_ADDRESS;
import static com.aspanta.emcsec.tools.Config.REGEX_WHOLE_AMOUNT;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;

public class EnterAnAddressEmercoinFragment extends Fragment implements IEnterAnAddressEmercoinFragment {

    CompositeDisposable disposable = new CompositeDisposable();
    private Observable<Boolean> addressObservable, amountObservable;
    private Disposable addressDisposable, amountDisposable;

    private TextView mTvBalanceEmc, mTvWholeRowBalanceEmc;
    private EditText mEtAddressEmc, mEtAmountEmc;
    private Button mBtnSend;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    static String sFromFragment;

    private DialogFragmentPleaseWaitWithProgress dialog;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private IEnterAnAddressEmercoinPresenter mPresenter;

    public static EnterAnAddressEmercoinFragment newInstance(String param1, String param2, String fromFragmant) {
        EnterAnAddressEmercoinFragment fragment = new EnterAnAddressEmercoinFragment();
        sFromFragment = fromFragmant;
        if (param1 != null && param2 != null) {
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            SPHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPresenter = new EnterAnAddressEmercoinPresenter(getContext(), this);

        View v = inflater.inflate(R.layout.fragment_enter_an_address_emercoin, container, false);
        init(v);

        String currentCurrency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceEmc.setText(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        String wholeRowBalanceEmc = "<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        String finalCurrentCurrency = currentCurrency;
        listener = (sharedPreferences, key) -> {
            mTvBalanceEmc.setText(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
            mTvWholeRowBalanceEmc.setText(Html.fromHtml("<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                    finalCurrentCurrency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                    finalCurrentCurrency + ")"));
        };
        SPHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);

        if (mParam1 != null && mParam2 != null) {
            mEtAddressEmc.setText(mParam1);
            mEtAmountEmc.setText(mParam2);
        }
        mBtnSend.setOnClickListener(view -> {
            try {
                LegacyAddress.fromBase58(EmercoinNetwork.get(), mEtAddressEmc.getText().toString());
                DialogFragmentConfirmOperationEmercoin.
                        newInstance(mPresenter, mEtAmountEmc.getText().toString(),
                                mEtAddressEmc.getText().toString()).show(getFragmentManager(), "");
            } catch (AddressFormatException afe) {
                MainActivity.showAlertDialog(getContext(), getString(R.string.enter_the_valid_address));
            }
        });

        validateFields(mEtAddressEmc, mEtAmountEmc, mBtnSend);
        return v;
    }

    public void init(View view) {
        mEtAmountEmc = view.findViewById(R.id.et_enter_an_address_emc_frag_amount);
        mEtAddressEmc = view.findViewById(R.id.et_enter_an_address_emc_frag_address);
        mBtnSend = view.findViewById(R.id.btn_enter_an_address_emc_frag_send);

        mTvBalanceEmc = view.findViewById(R.id.tv_balance_emc);
        mTvWholeRowBalanceEmc = view.findViewById(R.id.tv_whole_row_emc);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            SPHelper.getInstance().getSharedPreferencesLink().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void validateFields(EditText etAddress, EditText etAmount, Button btnSend) {

        addressObservable = textChanges(etAddress)
                .map(inputText -> addressValidation(inputText.toString()));

        amountObservable = textChanges(etAmount)
                .map(inputText -> amountValidation(inputText.toString()));


        addressDisposable = addressObservable
                .subscribe(b -> {
                    if (b) {
                        setDefaultUnderline(etAddress);
                    } else {
                        if (etAddress.getText().toString().isEmpty()) {
                            setDefaultUnderline(etAddress);
                        } else {
                            setErrorUnderline(etAddress);
                        }
                    }
                });

        amountDisposable = amountObservable
                .subscribe(b -> {
                    if (b) {
                        setDefaultUnderline(etAmount);
                    } else {
                        if (etAmount.getText().toString().isEmpty()) {
                            setDefaultUnderline(etAmount);
                        } else {
                            setErrorUnderline(etAmount);
                        }
                    }
                });

        disposable.addAll(addressDisposable, amountDisposable);
        disposable.add(Observable
                .combineLatest(
                        addressObservable, amountObservable,
                        (addressBoolean, amountBoolean) ->
                                addressBoolean && amountBoolean)
                .subscribe(btnSend::setEnabled));
    }

    public boolean addressValidation(String address) {
        return address.matches(REGEX_EMC_ADDRESS);
    }

    public boolean amountValidation(String amount) {

        if (!amount.isEmpty() & amount.matches(REGEX_WHOLE_AMOUNT)) {
            return true;
        }

        if (amount.matches(REGEX_AMOUNT)) {
            String[] p = amount.split("\\.");
            if (p[0].length() < 10 && p[1].length() < 7) {
                if (Double.valueOf(amount.toString()) >= 0.01)
                    return true;
            }
        }
        return false;
    }

    private void setErrorUnderline(EditText editText) {
        editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_error_bg));
    }

    private void setDefaultUnderline(EditText editText) {
        editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_default_bg));
    }

    @Override
    public void onBackPressed() {
        if (sFromFragment.equals("branch")) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendEmercoinFragment.newInstance());
        } else {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendCoinFragment.newInstance(0));
        }
    }

    @Override
    public String getCurrentTag() {
        return this.getClass().getName();
    }

    @Override
    public void showPleaseWaitDialog() {
        dialog = DialogFragmentPleaseWaitWithProgress.newInstance();
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void hidePleaseWaitDialog() {
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void showSuccessDialog() {

        DialogFragmentOperationDone dialogFragment =
                DialogFragmentOperationDone
                        .newInstance(mPresenter, null);

        getFragmentManager()
                .beginTransaction()
                .add(dialogFragment, dialogFragment.getCurrentTag())
                .commitAllowingStateLoss();
    }

    @Override
    public void setBalanceUsd(String usdBalance) {
    }

    @Override
    public void setBalance(String balance) {
        mTvBalanceEmc.setText(balance);
    }

    @Override
    public void setDownloadProgress(int count) {
        if (dialog != null) {
            dialog.setDownloadProgress(count);
        }
    }

    @Override
    public void setTotalProgress(int count) {
        if (dialog != null) {
            dialog.setTotalProgress(count);
        }
    }
}
