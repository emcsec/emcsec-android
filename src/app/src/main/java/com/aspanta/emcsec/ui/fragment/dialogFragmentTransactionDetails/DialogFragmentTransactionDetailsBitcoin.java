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
import com.aspanta.emcsec.db.room.historyPojos.BtcTransaction;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment.EnterAnAddressBitcoinFragment;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.bitcoinReceiveFragment.BitcoinReceiveFragment;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DialogFragmentTransactionDetailsBitcoin extends DialogFragment {

    static BtcTransaction sBtcTransaction;
    private Button mBtnRepeat, mBtnClose;
    private LinearLayout mLlFee;
    private TextView mTvDate, mTvAddress, mTvCategory, mTvAmount, mTvFee, mTvTrnsactionId, mTvBlock;

    public static DialogFragmentTransactionDetailsBitcoin newInstance(BtcTransaction btcTransaction) {
        DialogFragmentTransactionDetailsBitcoin fragment = new DialogFragmentTransactionDetailsBitcoin();
        sBtcTransaction = btcTransaction;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_transaction_detail_bitcoin, container, false);
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
        if (sBtcTransaction.getDate().equals("")) {
            dateString = "unconfirmed";
        } else {
            Date date = new Date(Long.parseLong(sBtcTransaction.getDate()) * 1000);
            dateString = formatter.format(date);
        }

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);

        BigDecimal satoshiBigDecimal = new BigDecimal(Long.parseLong(sBtcTransaction.getAmount()));
        String resultAmount = df.format(satoshiBigDecimal.divide(new BigDecimal(100000000)));

        if (sBtcTransaction.getCategory().equals("Self")) {
            mTvAddress.setText(getString(R.string.na));
        } else {
            mTvAddress.setText(sBtcTransaction.getAddress());
        }

        mTvAddress.setOnClickListener(c -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", sBtcTransaction.getAddress());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });

        switch (sBtcTransaction.getCategory()) {
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

        mTvAmount.setText(resultAmount + " BTC");

        String resultFee;
        if (sBtcTransaction.getFee().equals("None")) {
            resultFee = "None";
        } else {
            BigDecimal feeBigDecimal = new BigDecimal(Long.parseLong(
                    sBtcTransaction.getFee().isEmpty() ? "0" : sBtcTransaction.getFee()));
            resultFee = df.format(feeBigDecimal.divide(new BigDecimal(100000000)));
        }

        if (resultFee.equals("None")) {
            mLlFee.setVisibility(View.GONE);
        } else {
            mTvFee.setText("-" + resultFee + " BTC");
        }

        mTvTrnsactionId.setText(sBtcTransaction.getTx_id());
        mTvTrnsactionId.setOnClickListener(c -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", sBtcTransaction.getTx_id());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });

        if (sBtcTransaction.getBlock().equals("0") | sBtcTransaction.getBlock().equals("-1")) {
            mTvBlock.setText(getContext().getString(R.string.unconfirmed));
            mTvDate.setText(getContext().getString(R.string.unconfirmed));
        } else {
            mTvBlock.setText(sBtcTransaction.getBlock());
            mTvDate.setText(dateString);
        }

        mBtnRepeat.setOnClickListener(o -> {

            if (sBtcTransaction.getCategory().equals("Send") | sBtcTransaction.getCategory().equals("Self")) {
                EnterAnAddressBitcoinFragment enterAnAddressBitcoinFragment
                        = EnterAnAddressBitcoinFragment.newInstance(sBtcTransaction.getAddress(), resultAmount.replace("-", ""), "branch");
                ((MainActivity) getActivity()).navigator(enterAnAddressBitcoinFragment, enterAnAddressBitcoinFragment.getCurrentTag());
            } else {
                BitcoinReceiveFragment bitcoinReceiveFragment = BitcoinReceiveFragment.newInstance(sBtcTransaction.getAddress(), resultAmount);
                ((MainActivity) getActivity()).navigator(bitcoinReceiveFragment, bitcoinReceiveFragment.getCurrentTag());
            }
            dismiss();
        });
        mBtnClose.setOnClickListener(o -> dismiss());
        return view;
    }

    private void init(View view) {
        mTvDate = view.findViewById(R.id.tv_date_transaction_detail_bitcoin);
        mTvAddress = view.findViewById(R.id.tv_address_transaction_detail_bitcoin);
        mTvCategory = view.findViewById(R.id.tv_category_transaction_detail_bitcoin);
        mTvAmount = view.findViewById(R.id.tv_amount_transaction_detail_bitcoin);
        mTvFee = view.findViewById(R.id.tv_fee_transaction_detail_bitcoin);
        mLlFee = view.findViewById(R.id.ll_fee);
        mTvTrnsactionId = view.findViewById(R.id.tv_tx_id_transaction_detail_bitcoin);
        mTvBlock = view.findViewById(R.id.tv_block_transaction_detail_bitcoin);

        mBtnRepeat = view.findViewById(R.id.btn_repeat_transaction_detail_bitcoin);
        mBtnClose = view.findViewById(R.id.btn_close_transaction_detail_bitcoin);
    }
}
