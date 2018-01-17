package com.aspanta.emcsec.ui.fragment.dialogFragmentConfirmOperation;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.presenter.enterAnAddressBitcoinPresenter.IEnterAnAddressBitcoinPresenter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DialogFragmentConfirmOperationBitcoin extends DialogFragment {

    private Button mBtnYes, mBtnCancel;
    private TextView mTvAsk;
    static String sAmount;
    static String sFeePerKb;
    static String sAddress;

    static IEnterAnAddressBitcoinPresenter sPresenter;

    public static DialogFragmentConfirmOperationBitcoin newInstance(
            IEnterAnAddressBitcoinPresenter presenter,
            String amount, String feePerKb, String address) {
        sPresenter = presenter;
        sAmount = amount;
        sFeePerKb = feePerKb;
        sAddress = address;
        return new DialogFragmentConfirmOperationBitcoin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_confirm_operation_bitcoin, container, false);
        setCancelable(false);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        init(view);

        if (sAmount.contains(".")) {
            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(340);
            sAmount = df.format(Double.parseDouble(sAmount));
        } else {
            sAmount = String.valueOf(Integer.parseInt(sAmount));
        }

        mTvAsk.setText(getContext().getString(R.string.do_you_want_to_send) + sAmount + " BTC?");

        mBtnYes.setOnClickListener(o -> {
            sPresenter.sendBitcoin(sAddress, sAmount, sFeePerKb);
//            sPresenter.sendBitcoin(new BitcoinTransactionPojo(sAddress, sAmount, sFeePerKb, ""));
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
        mBtnYes = view.findViewById(R.id.btn_yes_confirm_operation_bitcoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_confirm_operation_bitcoin);
        mTvAsk = view.findViewById(R.id.tv_question_confirm_operation);
    }
}
