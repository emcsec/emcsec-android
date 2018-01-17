package com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.presenter.enterAnAddressBitcoinPresenter.EnterAnAddressBitcoinPresenter;
import com.aspanta.emcsec.presenter.enterAnAddressBitcoinPresenter.IEnterAnAddressBitcoinPresenter;
import com.aspanta.emcsec.tools.Config;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.dialogFragmentConfirmOperation.DialogFragmentConfirmOperationBitcoin;
import com.aspanta.emcsec.ui.fragment.dialogFragmentOperationDone.DialogFragmentOperationDone;
import com.aspanta.emcsec.ui.fragment.dialogFragmentPleaseWait.DialogFragmentPleaseWaitWithProgress;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.SendCoinFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendBitcoinFragment.SendBitcoinFragment;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.params.MainNetParams;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.LAST_FEE_VALUE;
import static com.aspanta.emcsec.tools.Config.REGEX_AMOUNT;
import static com.aspanta.emcsec.tools.Config.REGEX_BTC_ADDRESS;
import static com.aspanta.emcsec.tools.Config.REGEX_WHOLE_AMOUNT;
import static com.aspanta.emcsec.tools.Config.SEEKBAR_POSITION_KEY;
import static com.aspanta.emcsec.tools.Config.SEEKBAR_VALUE_KEY;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;

public class EnterAnAddressBitcoinFragment extends Fragment implements IEnterAnAddressBitcoinFragment {

    private final String TAG = getClass().getSimpleName();
    private final String DEFAULT_FEE = "0.00001";
    CompositeDisposable disposable = new CompositeDisposable();
    private Observable<Boolean> addressObservable, amountObservable, feeObservable;
    private Disposable addressDisposable, amountDisposable, feeDisposable;

    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc, mTvSeekValue;
    private EditText mEtAddressBtc, mEtAmountBtc, mEtFee;
    private Button mBtnSend;
    private SeekBar mSeekBar;
    private RadioButton mRbRecommended, mRbCustom;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    static String sFromFragment;

    private DialogFragmentPleaseWaitWithProgress dialog;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private IEnterAnAddressBitcoinPresenter mPresenter;

    public EnterAnAddressBitcoinFragment() {
        // Required empty public constructor
    }

    public static EnterAnAddressBitcoinFragment newInstance(String param1, String param2, String fromFragment) {
        sFromFragment = fromFragment;
        EnterAnAddressBitcoinFragment fragment = new EnterAnAddressBitcoinFragment();
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
            SharedPreferencesHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);
        }
        if (SharedPreferencesHelper.getInstance().getIntValue(SEEKBAR_POSITION_KEY) == -1
                && SharedPreferencesHelper.getInstance().getStringValue(SEEKBAR_VALUE_KEY).equals("")) {
            mSeekBar.setProgress(1);
            mTvSeekValue.setText("0.00148372");
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
        View v = inflater.inflate(R.layout.fragment_enter_an_address_bitcoin, container, false);
        init(v);
        mPresenter = new EnterAnAddressBitcoinPresenter(getContext(), this);

        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceBtc.setText(SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
        String wholeRowBalanceBtc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> BTC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceBtc.setText(Html.fromHtml(wholeRowBalanceBtc));

        String finalCurrentCurrency = currentCurrency;
        listener = (sharedPreferences, key) -> {
            mTvBalanceBtc.setText(SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
            mTvWholeRowBalanceBtc.setText(Html.fromHtml("<b>~" + SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                    finalCurrentCurrency + " (" + "<b>1 </b> BTC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY) + " </b>" +
                    finalCurrentCurrency + ")"));

        };
        SharedPreferencesHelper.getInstance().getSharedPreferencesLink().registerOnSharedPreferenceChangeListener(listener);

        if (mParam1 != null && mParam2 != null) {
            mEtAddressBtc.setText(mParam1);
            mEtAmountBtc.setText(mParam2);
        }

        mSeekBar.setProgress(SharedPreferencesHelper.getInstance().getIntValue(SEEKBAR_POSITION_KEY));
        mTvSeekValue.setText(SharedPreferencesHelper.getInstance().getStringValue(SEEKBAR_VALUE_KEY));

        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        String lastFeeValue = SharedPreferencesHelper.getInstance().getStringValue(LAST_FEE_VALUE);
        mEtFee.setEnabled(false);
        if (lastFeeValue.isEmpty() | lastFeeValue.equals("?")) {
            mEtFee.setText(SharedPreferencesHelper.getInstance().getStringValue(SEEKBAR_VALUE_KEY));
        } else {
            mEtFee.setText(lastFeeValue);
        }
        mRbRecommended.setOnClickListener(c -> {
            mSeekBar.setEnabled(true);
            if (lastFeeValue.isEmpty() | lastFeeValue.equals("?")) {
                mEtFee.setText(SharedPreferencesHelper.getInstance().getStringValue(SEEKBAR_VALUE_KEY));
            } else {
                mEtFee.setText(lastFeeValue);
            }
            mEtFee.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonDisabled));
            mEtFee.setEnabled(false);
        });

        mRbCustom.setOnClickListener(c -> {
            mSeekBar.setEnabled(false);
            if (lastFeeValue.isEmpty() | lastFeeValue.equals("?")) {
                mEtFee.setText(SharedPreferencesHelper.getInstance().getStringValue(SEEKBAR_VALUE_KEY));
            } else {
                mEtFee.setText(lastFeeValue);
            }
            mEtFee.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            mEtFee.setEnabled(true);
        });

        mBtnSend.setOnClickListener(view -> {
            try {
                Address.fromBase58(MainNetParams.get(), mEtAddressBtc.getText().toString());
                String fee;
                if (mRbRecommended.isChecked()) {
                    fee = mTvSeekValue.getText().toString();
                } else {
                    fee = mEtFee.getText().toString();
                }
                SharedPreferencesHelper.getInstance().putStringValue(LAST_FEE_VALUE, fee);
                Log.d("FEE", fee);
                DialogFragmentConfirmOperationBitcoin.
                        newInstance(mPresenter,
                                mEtAmountBtc.getText().toString(),
                                fee,
                                mEtAddressBtc.getText().toString())
                        .show(getFragmentManager(), "");
            } catch (AddressFormatException afe) {
                Config.showAlertDialog(getContext(), getString(R.string.enter_the_valid_address));
            }
        });

        validateFields(mEtAddressBtc, mEtAmountBtc, mEtFee, mBtnSend);
        return v;
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateBackground();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

    private void updateBackground() {
        switch (mSeekBar.getProgress()) {
            case 0:
                mTvSeekValue.setText("0.00136222");
                break;
            case 1:
                mTvSeekValue.setText("0.00148372");
                break;
            case 2:
                mTvSeekValue.setText("0.00162258");
                break;
            case 3:
                mTvSeekValue.setText("0.00179907");
                break;
            case 4:
                mTvSeekValue.setText("0.00199912");
                break;
            default:
                break;
        }
    }

    private void validateFields(EditText etAddress, EditText etAmount, EditText etFee, Button btnSend) {

        addressObservable = textChanges(etAddress)
                .map(inputText -> addressValidation(inputText.toString()));

        amountObservable = textChanges(etAmount)
                .map(inputText -> amountValidation(inputText.toString()));

        feeObservable = textChanges(mEtFee)
                .map(inputText -> feeValidation(inputText.toString()));

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

        feeDisposable = feeObservable
                .subscribe(isTrue -> {
                    if (isTrue) {
                        setDefaultUnderline(etFee);
                    } else {
                        if (etFee.getText().toString().isEmpty()) {
                            setDefaultUnderline(etFee);
                        } else {
                            setErrorUnderline(etFee);
                        }
                    }
                });

        disposable.addAll(addressDisposable, amountDisposable, feeDisposable);
        disposable.add(Observable
                .combineLatest(
                        addressObservable, amountObservable, feeObservable,
                        (addressBoolean, amountBoolean, feeBoolean) ->
                                addressBoolean && amountBoolean && feeBoolean)
                .subscribe(btnSend::setEnabled));
    }

    public boolean addressValidation(String address) {
        return address.matches(REGEX_BTC_ADDRESS);
    }

    private boolean feeValidation(String fee) {

        Log.d(TAG, "feeValidation");
        if (!mRbCustom.isChecked()) {
            Log.d(TAG, "!mRbCustom.isChecked()");
            return true;
        }

        if (!fee.isEmpty() & fee.matches(REGEX_WHOLE_AMOUNT)) {

            Log.d(TAG, "!fee.isEmpty() & fee.matches(REGEX_WHOLE_AMOUNT)");
            return true;
        }

        if (fee.matches(REGEX_AMOUNT)) {
            Log.d(TAG, "fee.matches(REGEX_AMOUNT)");
            String[] p = fee.split("\\.");
            if (p[0].length() < 10 && p[1].length() < 9) {
                if (Double.valueOf(fee) >= 0.00001) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean amountValidation(String amount) {

        if (!amount.isEmpty() & amount.matches(REGEX_WHOLE_AMOUNT)) {
            return true;
        }

        if (amount.matches(REGEX_AMOUNT)) {
            String[] p = amount.split("\\.");
            if (p[0].length() < 10 && p[1].length() < 9) {
                if (Double.valueOf(amount) > 0.0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setErrorUnderline(EditText editText) {

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//
//            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(),
//                    R.color.red));
//            editText.setBackgroundTintList(colorStateList);
//            editText.getBackground().mutate().setColorFilter
//                    (ContextCompat.getColor(getContext(), R.color.red), PorterDuff.Mode.SRC_ATOP);
//        } else {
//            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(),
//                    R.color.red));
//            ViewCompat.setBackgroundTintList(editText, colorStateList);
//        }
        editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_error_bg));
    }

    private void setDefaultUnderline(EditText editText) {

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//
//            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(),
//                    R.color.white));
//            editText.setBackgroundTintList(colorStateList);
//            editText.getBackground().mutate().setColorFilter
//                    (ContextCompat.getColor(getContext(), R.color.white),
//                            PorterDuff.Mode.SRC_ATOP);
//
//        } else {
//            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(),
//                    R.color.white));
//            ViewCompat.setBackgroundTintList(editText, colorStateList);
//        }

        editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_default_bg));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferencesHelper.getInstance()
                .putIntValue(SEEKBAR_POSITION_KEY, mSeekBar.getProgress());
        SharedPreferencesHelper.getInstance()
                .putStringValue(SEEKBAR_VALUE_KEY, mTvSeekValue.getText().toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            SharedPreferencesHelper.getInstance().getSharedPreferencesLink().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void onBackPressed() {
        if (sFromFragment.equals("branch")) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendBitcoinFragment.newInstance());
        } else {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendCoinFragment.newInstance(1));
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
            dialog.dismiss();
        }
    }

    @Override
    public void showSuccessDialog() {
        DialogFragmentOperationDone.newInstance(null, mPresenter)
                .show(getFragmentManager(), "");
    }

    @Override
    public void setBalance(String balance) {
        mTvBalanceBtc.setText(balance);
    }

    @Override
    public void setBalanceUsd(String balanceUsd) {
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

    public void init(View view) {
        mEtFee = view.findViewById(R.id.et_fee);
        mEtAddressBtc = view.findViewById(R.id.et_enter_an_address_btc_frag_address);
        mEtAmountBtc = view.findViewById(R.id.et_enter_an_address_btc_frag_amount);
        mBtnSend = view.findViewById(R.id.btn_enter_an_address_btc_frag_send);
        mSeekBar = view.findViewById(R.id.seek_bar_enter_an_address_btc_frag);
        mTvSeekValue = view.findViewById(R.id.tv_enter_an_address_btc_frag_seek_sum);

        mRbRecommended = view.findViewById(R.id.rb_recommended);
        mRbCustom = view.findViewById(R.id.rb_custom);

        mTvBalanceBtc = view.findViewById(R.id.tv_balance_btc);
        mTvWholeRowBalanceBtc = view.findViewById(R.id.tv_whole_row_btc);
    }
}