package com.aspanta.emcsec.ui.fragment.dialogFragmentPin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import static com.aspanta.emcsec.R.id.tv_0_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_1_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_2_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_3_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_4_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_5_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_6_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_7_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_8_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_9_enter_a_pin;
import static com.aspanta.emcsec.R.id.tv_c_enter_a_pin;
import static com.aspanta.emcsec.tools.Config.PIN_CODE;


public class DialogFragmentEnterPin extends DialogFragment implements View.OnClickListener{

    ImageView mIvEnterPin1, mIvEnterPin2, mIvEnterPin3, mIvEnterPin4, mIvEnterPin5, mIvEnterPin6;

    TextView mTvEnterPin1, mTvEnterPin2, mTvEnterPin3, mTvEnterPin4, mTvEnterPin5, mTvEnterPin6,
            mTvEnterPin7, mTvEnterPin8, mTvEnterPin9, mTvEnterPin0, mTvEnterPinC;

    Button mBtnOk, mBtnCancelEnterPin;

    String pinCodeEnter = "";
    String pinCodeSaved = SharedPreferencesHelper.getInstance().getStringValue(PIN_CODE);

    LinearLayout mLlDotsEnter;

    public static DialogFragmentEnterPin newInstance() {
        return new DialogFragmentEnterPin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_enter_pin, container, false);
        init(view);

        setCancelable(false);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }



        return view;
    }



    private void init(View view) {

        mIvEnterPin1 = view.findViewById(R.id.iv_enter_pin_1);
        mIvEnterPin2 = view.findViewById(R.id.iv_enter_pin_2);
        mIvEnterPin3 = view.findViewById(R.id.iv_enter_pin_3);
        mIvEnterPin4 = view.findViewById(R.id.iv_enter_pin_4);
        mIvEnterPin5 = view.findViewById(R.id.iv_enter_pin_5);
        mIvEnterPin6 = view.findViewById(R.id.iv_enter_pin_6);

        mTvEnterPin1 = view.findViewById(tv_1_enter_a_pin);
        mTvEnterPin1.setOnClickListener(this);
        mTvEnterPin2 = view.findViewById(tv_2_enter_a_pin);
        mTvEnterPin2.setOnClickListener(this);
        mTvEnterPin3 = view.findViewById(tv_3_enter_a_pin);
        mTvEnterPin3.setOnClickListener(this);
        mTvEnterPin4 = view.findViewById(tv_4_enter_a_pin);
        mTvEnterPin4.setOnClickListener(this);
        mTvEnterPin5 = view.findViewById(tv_5_enter_a_pin);
        mTvEnterPin5.setOnClickListener(this);
        mTvEnterPin6 = view.findViewById(tv_6_enter_a_pin);
        mTvEnterPin6.setOnClickListener(this);
        mTvEnterPin7 = view.findViewById(tv_7_enter_a_pin);
        mTvEnterPin7.setOnClickListener(this);
        mTvEnterPin8 = view.findViewById(tv_8_enter_a_pin);
        mTvEnterPin8.setOnClickListener(this);
        mTvEnterPin9 = view.findViewById(tv_9_enter_a_pin);
        mTvEnterPin9.setOnClickListener(this);
        mTvEnterPin0 = view.findViewById(tv_0_enter_a_pin);
        mTvEnterPin0.setOnClickListener(this);
        mTvEnterPinC = view.findViewById(tv_c_enter_a_pin);
        mTvEnterPinC.setOnClickListener(this);

        mLlDotsEnter = view.findViewById(R.id.ll_dots_enter);

        mBtnOk = view.findViewById(R.id.btn_yes_enter_a_pin);
        mBtnOk.setOnClickListener(c -> {
            dismiss();
        });

        mBtnCancelEnterPin = view.findViewById(R.id.btn_cancel_enter_a_pin);
        mBtnCancelEnterPin.setOnClickListener(c -> {
            getActivity().finishAffinity();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_1_enter_a_pin:
                pinCodeEnter += "1";
                checkValidation();
                break;
            case tv_2_enter_a_pin:
                pinCodeEnter += "2";
                checkValidation();
                break;
            case tv_3_enter_a_pin:
                pinCodeEnter += "3";
                checkValidation();
                break;
            case tv_4_enter_a_pin:
                pinCodeEnter += "4";
                checkValidation();
                break;
            case tv_5_enter_a_pin:
                pinCodeEnter += "5";
                checkValidation();
                break;
            case tv_6_enter_a_pin:
                pinCodeEnter += "6";
                checkValidation();
                break;
            case tv_7_enter_a_pin:
                pinCodeEnter += "7";
                checkValidation();
                break;
            case tv_8_enter_a_pin:
                pinCodeEnter += "8";
                checkValidation();
                break;
            case tv_9_enter_a_pin:
                pinCodeEnter += "9";
                checkValidation();
                break;
            case tv_0_enter_a_pin:
                pinCodeEnter += "0";
                checkValidation();
                break;
            case tv_c_enter_a_pin:
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
                mIvEnterPin1.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 2:
                mIvEnterPin1.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 3:
                mIvEnterPin1.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 4:
                mIvEnterPin1.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 5:
                mIvEnterPin1.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin5.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnOk.setEnabled(false);
                break;
            case 6:
                mIvEnterPin1.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin2.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin3.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin4.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin5.setBackgroundResource(R.drawable.dote_full);
                mIvEnterPin6.setBackgroundResource(R.drawable.dote_full);
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
}
