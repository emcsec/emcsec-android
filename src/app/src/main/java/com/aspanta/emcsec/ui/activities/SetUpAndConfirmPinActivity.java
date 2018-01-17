package com.aspanta.emcsec.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import static com.aspanta.emcsec.R.id.tv_0_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_0_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_1_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_1_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_2_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_2_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_3_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_3_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_4_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_4_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_5_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_5_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_6_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_6_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_7_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_7_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_8_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_8_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_9_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_9_set_a_pin;
import static com.aspanta.emcsec.R.id.tv_c_confirm_a_pin;
import static com.aspanta.emcsec.R.id.tv_c_set_a_pin;
import static com.aspanta.emcsec.tools.Config.PIN_CODE;
import static com.aspanta.emcsec.tools.Config.SET_PIN_CODE_OR_NOT;


public class SetUpAndConfirmPinActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout mRlSetUpPin, mRlConfirmUpPin;

    ImageView mIvSetPin1, mIvSetPin2, mIvSetPin3, mIvSetPin4, mIvSetPin5, mIvSetPin6,
            mIvConfirmPin1, mIvConfirmPin2, mIvConfirmPin3, mIvConfirmPin4, mIvConfirmPin5, mIvConfirmPin6;

    TextView mTvSetPin1, mTvSetPin2, mTvSetPin3, mTvSetPin4, mTvSetPin5, mTvSetPin6, mTvSetPin7,
            mTvSetPin8, mTvSetPin9, mTvSetPin0, mTvSetPinC,
            mTvConfirmPin1, mTvConfirmPin2, mTvConfirmPin3, mTvConfirmPin4, mTvConfirmPin5,
            mTvConfirmPin6, mTvConfirmPin7, mTvConfirmPin8, mTvConfirmPin9, mTvConfirmPin0, mTvConfirmPinC;

    Button mBtnYes, mBtnCancelSetPin, mBtnConfirm, mBtnCancelConfirmPin;

    String pinCodeSet = "";
    String pinCodeConfirm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_and_confirm_pin);
        init();
    }

    private void init() {

        mRlSetUpPin = findViewById(R.id.rl_set_a_pin);
        mRlConfirmUpPin = findViewById(R.id.rl_confirm_a_pin);

        mIvSetPin1 = findViewById(R.id.iv_set_pin_1);
        mIvSetPin2 = findViewById(R.id.iv_set_pin_2);
        mIvSetPin3 = findViewById(R.id.iv_set_pin_3);
        mIvSetPin4 = findViewById(R.id.iv_set_pin_4);
        mIvSetPin5 = findViewById(R.id.iv_set_pin_5);
        mIvSetPin6 = findViewById(R.id.iv_set_pin_6);

        mIvConfirmPin1 = findViewById(R.id.iv_confirm_pin_1);
        mIvConfirmPin2 = findViewById(R.id.iv_confirm_pin_2);
        mIvConfirmPin3 = findViewById(R.id.iv_confirm_pin_3);
        mIvConfirmPin4 = findViewById(R.id.iv_confirm_pin_4);
        mIvConfirmPin5 = findViewById(R.id.iv_confirm_pin_5);
        mIvConfirmPin6 = findViewById(R.id.iv_confirm_pin_6);

        mTvSetPin1 = findViewById(tv_1_set_a_pin);
        mTvSetPin1.setOnClickListener(this);
        mTvSetPin2 = findViewById(tv_2_set_a_pin);
        mTvSetPin2.setOnClickListener(this);
        mTvSetPin3 = findViewById(tv_3_set_a_pin);
        mTvSetPin3.setOnClickListener(this);
        mTvSetPin4 = findViewById(tv_4_set_a_pin);
        mTvSetPin4.setOnClickListener(this);
        mTvSetPin5 = findViewById(tv_5_set_a_pin);
        mTvSetPin5.setOnClickListener(this);
        mTvSetPin6 = findViewById(tv_6_set_a_pin);
        mTvSetPin6.setOnClickListener(this);
        mTvSetPin7 = findViewById(tv_7_set_a_pin);
        mTvSetPin7.setOnClickListener(this);
        mTvSetPin8 = findViewById(tv_8_set_a_pin);
        mTvSetPin8.setOnClickListener(this);
        mTvSetPin9 = findViewById(tv_9_set_a_pin);
        mTvSetPin9.setOnClickListener(this);
        mTvSetPin0 = findViewById(tv_0_set_a_pin);
        mTvSetPin0.setOnClickListener(this);
        mTvSetPinC = findViewById(tv_c_set_a_pin);
        mTvSetPinC.setOnClickListener(this);

        mTvConfirmPin1 = findViewById(tv_1_confirm_a_pin);
        mTvConfirmPin1.setOnClickListener(this);
        mTvConfirmPin2 = findViewById(tv_2_confirm_a_pin);
        mTvConfirmPin2.setOnClickListener(this);
        mTvConfirmPin3 = findViewById(tv_3_confirm_a_pin);
        mTvConfirmPin3.setOnClickListener(this);
        mTvConfirmPin4 = findViewById(tv_4_confirm_a_pin);
        mTvConfirmPin4.setOnClickListener(this);
        mTvConfirmPin5 = findViewById(tv_5_confirm_a_pin);
        mTvConfirmPin5.setOnClickListener(this);
        mTvConfirmPin6 = findViewById(tv_6_confirm_a_pin);
        mTvConfirmPin6.setOnClickListener(this);
        mTvConfirmPin7 = findViewById(tv_7_confirm_a_pin);
        mTvConfirmPin7.setOnClickListener(this);
        mTvConfirmPin8 = findViewById(tv_8_confirm_a_pin);
        mTvConfirmPin8.setOnClickListener(this);
        mTvConfirmPin9 = findViewById(tv_9_confirm_a_pin);
        mTvConfirmPin9.setOnClickListener(this);
        mTvConfirmPin0 = findViewById(tv_0_confirm_a_pin);
        mTvConfirmPin0.setOnClickListener(this);
        mTvConfirmPinC = findViewById(tv_c_confirm_a_pin);
        mTvConfirmPinC.setOnClickListener(this);

        mBtnConfirm = findViewById(R.id.btn_yes_confirm_a_pin);
        mBtnConfirm.setOnClickListener(c -> {
            SharedPreferencesHelper.getInstance().putStringValue(SET_PIN_CODE_OR_NOT, "yes");
            SharedPreferencesHelper.getInstance().putStringValue(PIN_CODE, pinCodeConfirm);
            finish();
        });

        mBtnCancelConfirmPin = findViewById(R.id.btn_cancel_confirm_a_pin);
        mBtnCancelConfirmPin.setOnClickListener(c -> {
            SharedPreferencesHelper.getInstance().putStringValue(SET_PIN_CODE_OR_NOT, "not");
            SharedPreferencesHelper.getInstance().putStringValue(PIN_CODE, "not");
            finish();
        });

        mBtnYes = findViewById(R.id.btn_yes_set_a_pin);
        mBtnYes.setOnClickListener(c -> {
            mRlSetUpPin.setVisibility(View.GONE);
            mRlConfirmUpPin.setVisibility(View.VISIBLE);
        });
        mBtnCancelSetPin = findViewById(R.id.btn_cancel_set_a_pin);
        mBtnCancelSetPin.setOnClickListener(c -> {
            SharedPreferencesHelper.getInstance().putStringValue(SET_PIN_CODE_OR_NOT, "not");
            SharedPreferencesHelper.getInstance().putStringValue(PIN_CODE, "not");
            finish();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_1_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "1";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_2_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "2";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_3_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "3";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_4_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "4";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_5_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "5";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_6_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "6";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_7_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "7";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_8_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "8";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_9_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "9";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_0_set_a_pin:
                if (pinCodeSet.length() == 6) {
                    break;
                } else {
                    pinCodeSet += "0";
                    updateProgressForSet(pinCodeSet.length());
                    break;
                }
            case tv_c_set_a_pin:
                if (pinCodeSet.length() > 0) {
                    pinCodeSet = pinCodeSet.substring(0, pinCodeSet.length() - 1);
                }
                updateProgressForSet(pinCodeSet.length());
                break;
            case tv_1_confirm_a_pin:
                pinCodeConfirm += "1";
                checkValidation();
                break;
            case tv_2_confirm_a_pin:
                pinCodeConfirm += "2";
                checkValidation();
                break;
            case tv_3_confirm_a_pin:
                pinCodeConfirm += "3";
                checkValidation();
                break;
            case tv_4_confirm_a_pin:
                pinCodeConfirm += "4";
                checkValidation();
                break;
            case tv_5_confirm_a_pin:
                pinCodeConfirm += "5";
                checkValidation();
                break;
            case tv_6_confirm_a_pin:
                pinCodeConfirm += "6";
                checkValidation();
                break;
            case tv_7_confirm_a_pin:
                pinCodeConfirm += "7";
                checkValidation();
                break;
            case tv_8_confirm_a_pin:
                pinCodeConfirm += "8";
                checkValidation();
                break;
            case tv_9_confirm_a_pin:
                pinCodeConfirm += "9";
                checkValidation();
                break;
            case tv_0_confirm_a_pin:
                pinCodeConfirm += "0";
                checkValidation();
                break;
            case tv_c_confirm_a_pin:
                if (pinCodeConfirm.length() > 0) {
                    pinCodeConfirm = pinCodeConfirm.substring(0, pinCodeConfirm.length() - 1);
                }
                updateProgressForConfirm(pinCodeConfirm.length());
                break;
        }
    }

    private void updateProgressForSet(int length) {

        switch (length) {
            case 1:
                mIvSetPin1.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin2.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnYes.setEnabled(false);
                break;
            case 2:
                mIvSetPin1.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin2.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnYes.setEnabled(false);
                break;
            case 3:
                mIvSetPin1.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin2.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin3.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnYes.setEnabled(false);
                break;
            case 4:
                mIvSetPin1.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin2.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin3.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin4.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnYes.setEnabled(false);
                break;
            case 5:
                mIvSetPin1.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin2.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin3.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin4.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin5.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnYes.setEnabled(false);
                break;
            case 6:
                mIvSetPin1.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin2.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin3.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin4.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin5.setBackgroundResource(R.drawable.dote_full);
                mIvSetPin6.setBackgroundResource(R.drawable.dote_full);
                mBtnYes.setEnabled(true);
                break;
            case 0:
                mIvSetPin1.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin2.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvSetPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnYes.setEnabled(false);
                break;
        }
    }

    private void updateProgressForConfirm(int length) {
        switch (length) {
            case 1:
                mIvConfirmPin1.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin2.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnConfirm.setEnabled(false);
                break;
            case 2:
                mIvConfirmPin1.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin2.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnConfirm.setEnabled(false);
                break;
            case 3:
                mIvConfirmPin1.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin2.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin3.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnConfirm.setEnabled(false);
                break;
            case 4:
                mIvConfirmPin1.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin2.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin3.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin4.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnConfirm.setEnabled(false);
                break;
            case 5:
                mIvConfirmPin1.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin2.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin3.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin4.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin5.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnConfirm.setEnabled(false);
                break;
            case 6:
                mIvConfirmPin1.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin2.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin3.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin4.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin5.setBackgroundResource(R.drawable.dote_full);
                mIvConfirmPin6.setBackgroundResource(R.drawable.dote_full);
                mBtnConfirm.setEnabled(true);
                break;
            case 0:
                mIvConfirmPin1.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin2.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin3.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin4.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin5.setBackgroundResource(R.drawable.dot_empty);
                mIvConfirmPin6.setBackgroundResource(R.drawable.dot_empty);
                mBtnConfirm.setEnabled(false);
                break;
        }
    }

    private void checkValidation() {

        if (pinCodeConfirm.length() > 6) {
            pinCodeConfirm = pinCodeConfirm.substring(0, pinCodeConfirm.length() - 1);
        }

        if (pinCodeConfirm.length() == 6 && !pinCodeConfirm.equals(pinCodeSet)) {
            pinCodeConfirm = "";
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(1)
                    .playOn(findViewById(R.id.ll_dots_confirm));
            updateProgressForConfirm(pinCodeConfirm.length());
        } else {
            updateProgressForConfirm(pinCodeConfirm.length());
        }
    }
}