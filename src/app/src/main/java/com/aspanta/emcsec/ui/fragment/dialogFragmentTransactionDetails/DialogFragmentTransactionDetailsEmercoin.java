package com.aspanta.emcsec.ui.fragment.dialogFragmentTransactionDetails;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment.EnterAnAddressEmercoinFragment;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.emercoinReceiveFragment.EmercoinReceiveFragment;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DialogFragmentTransactionDetailsEmercoin extends DialogFragment {

    static EmcTransaction sEmcTransaction;
    private Button mBtnRepeat, mBtnClose;
    private LinearLayout mLlFee;
    private TextView mTvDate, mTvAddress, mTvCategory, mTvAmount, mTvFee, mTvTrnsactionId, mTvBlock;

    public static DialogFragmentTransactionDetailsEmercoin newInstance(EmcTransaction emcTransaction) {
        DialogFragmentTransactionDetailsEmercoin fragment = new DialogFragmentTransactionDetailsEmercoin();
        sEmcTransaction = emcTransaction;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_transaction_detail_emercoin, container, false);
        init(view);
        setCancelable(false);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);

        String dateString;
        if (sEmcTransaction.getDate().equals("")) {
            dateString = getContext().getString(R.string.unconfirmed);
        } else {
            Date date = new Date(Long.parseLong(sEmcTransaction.getDate()) * 1000);
            dateString = formatter.format(date);
        }

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);

        BigDecimal satoshiBigDecimal = new BigDecimal(Long.parseLong(sEmcTransaction.getAmount()));
        String resultAmount = df.format(satoshiBigDecimal.divide(new BigDecimal(1000000)));

        mTvDate.setText(dateString);

        if (sEmcTransaction.getCategory().equals("Self")) {
            mTvAddress.setText(getString(R.string.na));
        } else {
            mTvAddress.setText(sEmcTransaction.getAddress());
        }

        mTvAddress.setOnClickListener(c -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", sEmcTransaction.getAddress());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });

        switch (sEmcTransaction.getCategory()) {
            case "Self":
                mTvCategory.setText(getString(R.string.self_detail));
                break;
            case "Send":
                mTvCategory.setText(getString(R.string.send_detail));
                break;
            case "Receive":
                mTvCategory.setText(getString(R.string.receive_detail));
                break;
            default:
                mTvCategory.setText("");
                break;
        }

        mTvAmount.setText(resultAmount + " EMC");

        String resultFee;
        if (sEmcTransaction.getFee().equals("None")) {
            resultFee = "None";
        } else {
            BigDecimal feeBigDecimal = new BigDecimal(Long.parseLong(
                    sEmcTransaction.getFee().isEmpty() ? "0" : sEmcTransaction.getFee()));
            resultFee = df.format(feeBigDecimal.divide(new BigDecimal(1000000)));
        }

        if (resultFee.equals("None")) {
            mLlFee.setVisibility(View.GONE);
        } else {
            mTvFee.setText("-" + resultFee + " EMC");
        }
        mTvTrnsactionId.setText(sEmcTransaction.getTx_id());
        mTvTrnsactionId.setOnClickListener(c -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", sEmcTransaction.getTx_id());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });

        if (sEmcTransaction.getBlock().equals("0") | sEmcTransaction.getBlock().equals("-1")) {
            mTvBlock.setText(getContext().getString(R.string.unconfirmed));
        } else {
            mTvBlock.setText(sEmcTransaction.getBlock());
        }

        mBtnRepeat.setOnClickListener(o -> {
            if (sEmcTransaction.getCategory().equals("Send") | sEmcTransaction.getCategory().equals("Self")) {
                EnterAnAddressEmercoinFragment enterAnAddressEmercoinFragment
                        = EnterAnAddressEmercoinFragment.newInstance(sEmcTransaction.getAddress(), resultAmount.replace("-", ""), "branch");
                ((MainActivity) getActivity()).navigator(enterAnAddressEmercoinFragment, enterAnAddressEmercoinFragment.getCurrentTag());
            } else {
                EmercoinReceiveFragment emercoinReceiveFragment = EmercoinReceiveFragment.newInstance(sEmcTransaction.getAddress(), resultAmount);
                ((MainActivity) getActivity()).navigator(emercoinReceiveFragment, emercoinReceiveFragment.getCurrentTag());
            }
            dismiss();
        });
        mBtnClose.setOnClickListener(o -> dismiss());
        return view;
    }

    private void init(View view) {
        mTvDate = view.findViewById(R.id.tv_date_transaction_detail_emercoin);
        mTvAddress = view.findViewById(R.id.tv_address_transaction_detail_emercoin);
        mTvCategory = view.findViewById(R.id.tv_category_transaction_detail_emercoin);
        mTvAmount = view.findViewById(R.id.tv_amount_transaction_detail_emercoin);
        mTvFee = view.findViewById(R.id.tv_fee_transaction_detail_emercoin);
        mLlFee = view.findViewById(R.id.ll_fee);
        mTvTrnsactionId = view.findViewById(R.id.tv_tx_id_transaction_detail_emercoin);
        mTvBlock = view.findViewById(R.id.tv_block_transaction_detail_emercoin);


        mBtnRepeat = view.findViewById(R.id.btn_repeat_transaction_detail_emercoin);
        mBtnClose = view.findViewById(R.id.btn_close_transaction_detail_emercoin);
    }
}

