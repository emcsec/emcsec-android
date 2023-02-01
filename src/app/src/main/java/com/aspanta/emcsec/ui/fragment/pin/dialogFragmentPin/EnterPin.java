package com.aspanta.emcsec.ui.fragment.pin.dialogFragmentPin;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.ui.fragment.pin.FingerprintUiHelper;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import static com.aspanta.emcsec.tools.Config.ENABLE;
import static com.aspanta.emcsec.tools.Config.FROM;
import static com.aspanta.emcsec.tools.Config.PIN_CODE;

public class EnterPin extends DialogFragment implements View.OnClickListener, FingerprintUiHelper.Callback {

    private static final String TAG = EnterPin.class.getSimpleName();

    private ImageView mIvEnterPin1, mIvEnterPin2, mIvEnterPin3, mIvEnterPin4, mIvEnterPin5, mIvEnterPin6;
    private TextView mTvEnterPin1, mTvEnterPin2, mTvEnterPin3, mTvEnterPin4, mTvEnterPin5, mTvEnterPin6,
            mTvEnterPin7, mTvEnterPin8, mTvEnterPin9, mTvEnterPin0, mTvEnterPinC;

    private Button mBtnOk, mBtnCancel;

    private String pinCodeEnter = "";
    private String pinCodeSaved = SPHelper.getInstance().getStringValue(PIN_CODE);

    private LinearLayout mLlDotsEnter;
    private ImageView ivFingerprint;
    private TextView tvFingerprint;
    private FingerprintManager mFingerprintManager;
    private FingerprintUiHelper mFingerprintUiHelper;
    private EnterPinCallback callback;
    SPHelper mSPHelper = SPHelper.getInstance();
    private int from;

    public static EnterPin newInstance(int from) {
        EnterPin enterPin = new EnterPin();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM, from);
        enterPin.setArguments(bundle);
        return enterPin;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_enter_pin, container, false);
        init(view);

        setCancelable(false);
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        Bundle bundle = getArguments();
        from = bundle.getInt(FROM);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initLayoutForFingerprint();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintUiHelper.stopListening();
        }
    }

    private void initLayoutForFingerprint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mFingerprintManager == null) {
                mFingerprintManager = (FingerprintManager) getContext().getSystemService(Context.FINGERPRINT_SERVICE);

            }
            if (mFingerprintUiHelper == null) {
                mFingerprintUiHelper = new FingerprintUiHelper.FingerprintUiHelperBuilder(mFingerprintManager)
                        .build(ivFingerprint, tvFingerprint, this);
            }

            try {
                if (mFingerprintUiHelper.isFingerprintAuthAvailable()
                        && mSPHelper.getEnableFingerprint() == ENABLE) {
                    ivFingerprint.setVisibility(View.VISIBLE);
                    tvFingerprint.setVisibility(View.VISIBLE);
                    mFingerprintUiHelper.startListening();
                } else {
                    ivFingerprint.setVisibility(View.INVISIBLE);
                    tvFingerprint.setVisibility(View.INVISIBLE);
                }
            } catch (SecurityException e) {
                ivFingerprint.setVisibility(View.INVISIBLE);
                tvFingerprint.setVisibility(View.INVISIBLE);
            }
        } else {
            ivFingerprint.setVisibility(View.INVISIBLE);
            tvFingerprint.setVisibility(View.INVISIBLE);
        }
    }

    private void init(View view) {

        ivFingerprint = view.findViewById(R.id.iv_fingerprint);
        tvFingerprint = view.findViewById(R.id.tv_fingerprint_message);

        mIvEnterPin1 = view.findViewById(R.id.iv_enter_pin_1);
        mIvEnterPin2 = view.findViewById(R.id.iv_enter_pin_2);
        mIvEnterPin3 = view.findViewById(R.id.iv_enter_pin_3);
        mIvEnterPin4 = view.findViewById(R.id.iv_enter_pin_4);
        mIvEnterPin5 = view.findViewById(R.id.iv_enter_pin_5);
        mIvEnterPin6 = view.findViewById(R.id.iv_enter_pin_6);

        mTvEnterPin1 = view.findViewById(R.id.tv_1_enter_a_pin);
        mTvEnterPin1.setOnClickListener(this);
        mTvEnterPin2 = view.findViewById(R.id.tv_2_enter_a_pin);
        mTvEnterPin2.setOnClickListener(this);
        mTvEnterPin3 = view.findViewById(R.id.tv_3_enter_a_pin);
        mTvEnterPin3.setOnClickListener(this);
        mTvEnterPin4 = view.findViewById(R.id.tv_4_enter_a_pin);
        mTvEnterPin4.setOnClickListener(this);
        mTvEnterPin5 = view.findViewById(R.id.tv_5_enter_a_pin);
        mTvEnterPin5.setOnClickListener(this);
        mTvEnterPin6 = view.findViewById(R.id.tv_6_enter_a_pin);
        mTvEnterPin6.setOnClickListener(this);
        mTvEnterPin7 = view.findViewById(R.id.tv_7_enter_a_pin);
        mTvEnterPin7.setOnClickListener(this);
        mTvEnterPin8 = view.findViewById(R.id.tv_8_enter_a_pin);
        mTvEnterPin8.setOnClickListener(this);
        mTvEnterPin9 = view.findViewById(R.id.tv_9_enter_a_pin);
        mTvEnterPin9.setOnClickListener(this);
        mTvEnterPin0 = view.findViewById(R.id.tv_0_enter_a_pin);
        mTvEnterPin0.setOnClickListener(this);
        mTvEnterPinC = view.findViewById(R.id.tv_c_enter_a_pin);
        mTvEnterPinC.setOnClickListener(this);

        mLlDotsEnter = view.findViewById(R.id.ll_dots_enter);

        mBtnOk = view.findViewById(R.id.btn_yes_enter_a_pin);
        mBtnOk.setOnClickListener(v -> {
            callback.onPinEntered(from);
            dismiss();
        });

        mBtnCancel = view.findViewById(R.id.btn_cancel_enter_a_pin);
        mBtnCancel.setOnClickListener(v -> {
            callback.onPinCanceled(from);
            dismiss();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_1_enter_a_pin:
                pinCodeEnter += "1";
                checkValidation();
                break;
            case R.id.tv_2_enter_a_pin:
                pinCodeEnter += "2";
                checkValidation();
                break;
            case R.id.tv_3_enter_a_pin:
                pinCodeEnter += "3";
                checkValidation();
                break;
            case R.id.tv_4_enter_a_pin:
                pinCodeEnter += "4";
                checkValidation();
                break;
            case R.id.tv_5_enter_a_pin:
                pinCodeEnter += "5";
                checkValidation();
                break;
            case R.id.tv_6_enter_a_pin:
                pinCodeEnter += "6";
                checkValidation();
                break;
            case R.id.tv_7_enter_a_pin:
                pinCodeEnter += "7";
                checkValidation();
                break;
            case R.id.tv_8_enter_a_pin:
                pinCodeEnter += "8";
                checkValidation();
                break;
            case R.id.tv_9_enter_a_pin:
                pinCodeEnter += "9";
                checkValidation();
                break;
            case R.id.tv_0_enter_a_pin:
                pinCodeEnter += "0";
                checkValidation();
                break;
            case R.id.tv_c_enter_a_pin:
                if (pinCodeEnter.length() > 0) {
                    pinCodeEnter = pinCodeEnter.substring(0, pinCodeEnter.length() - 1);
                }
                updateProgressForEnter(pinCodeEnter.length());
                break;
        }
    }

    private void updateProgressForEnter(int length) {

        switch (length) {
            case 1:
                mIvEnterPin1.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 2:
                mIvEnterPin1.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 3:
                mIvEnterPin1.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 4:
                mIvEnterPin1.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 5:
                mIvEnterPin1.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 6:
                mIvEnterPin1.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_full);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_full);
                mBtnOk.setEnabled(true);
                break;
            case 0:
                mIvEnterPin1.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
        }
    }

    private void checkValidation() {

        if (pinCodeEnter.length() > 6) {
            pinCodeEnter = pinCodeEnter.substring(0, pinCodeEnter.length() - 1);
        }

        if (pinCodeEnter.length() == 6 && !pinCodeEnter.equals(pinCodeSaved)) {
            pinCodeEnter = "";
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(1)
                    .playOn(mLlDotsEnter);
            updateProgressForEnter(pinCodeEnter.length());
        } else {
            updateProgressForEnter(pinCodeEnter.length());
        }
    }

    @Override
    public void onAuthenticated() {
        callback.onPinEntered(from);
        dismiss();
    }

    @Override
    public void onError() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EnterPinCallback) {
            callback = (EnterPinCallback) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement EnterPinCallback");
        }
    }

    public interface EnterPinCallback {
        void onPinEntered(int from);

        void onPinCanceled(int from);
    }
}
