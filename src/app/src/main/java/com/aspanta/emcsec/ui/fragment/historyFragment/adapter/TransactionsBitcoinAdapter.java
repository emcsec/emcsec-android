package com.aspanta.emcsec.ui.fragment.historyFragment.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.historyPojos.BtcTransaction;
import com.aspanta.emcsec.ui.fragment.dialogFragmentTransactionDetails.DialogFragmentTransactionDetailsBitcoin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TransactionsBitcoinAdapter extends RecyclerView.Adapter<TransactionsBitcoinAdapter.TransactionsBtcViewHolder> {

    private List<BtcTransaction> mBtcTransactionList;
    private Context mContext;


    public TransactionsBitcoinAdapter(List<BtcTransaction> mAddresses, Context context) {
        this.mBtcTransactionList = mAddresses;
        this.mContext = context;
    }

    @Override
    public TransactionsBtcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);
        return new TransactionsBtcViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(TransactionsBtcViewHolder holder, int position) {

        final BtcTransaction btcTransaction = mBtcTransactionList.get(position);

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

        String dateString;
        if (btcTransaction.getDate().equals("")) {
            dateString = mContext.getString(R.string.unconfirmed);
        } else {
            Date date = new Date(Long.parseLong(btcTransaction.getDate()) * 1000);
            dateString = formatter.format(date);
        }

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);
        BigDecimal satoshiBigDecimal = new BigDecimal(Long.parseLong(btcTransaction.getAmount()));
        String resultAmount = df.format(satoshiBigDecimal.divide(new BigDecimal(100000000)));

        String resultFee;
        if (btcTransaction.getFee().equals("None") | btcTransaction.getFee().equals("")) {
            resultFee = "None";
        } else {
            BigDecimal satoshiBigDecimalFee = new BigDecimal(Long.parseLong(btcTransaction.getFee()));
            resultFee = df.format(satoshiBigDecimalFee.divide(new BigDecimal(100000000)));
        }

        if (btcTransaction.getBlock().equals("0") | btcTransaction.getBlock().equals("-1")) {
            holder.mIvHourglass.setVisibility(View.VISIBLE);
            holder.mTvDate.setText(mContext.getString(R.string.unconfirmed));
        } else {
            holder.mTvDate.setText(dateString);
        }

        if (btcTransaction.getCategory().equals("Self")) {
            holder.mTvAddress.setText(mContext.getString(R.string.na));
            holder.mTvAmount.setText("-" + resultFee + " " + "BTC");
        } else {
            holder.mTvAddress.setText(btcTransaction.getAddress());
            holder.mTvAmount.setText(resultAmount + " " + "BTC");
        }

        switch (btcTransaction.getCategory()) {
            case "Receive":
                holder.mIvInOrOutTransaction.setImageDrawable(ContextCompat.getDrawable
                        (holder.mIvInOrOutTransaction.getContext(), R.drawable.ic_out_transaction));
                break;
            case "Send":
                holder.mIvInOrOutTransaction.setImageDrawable(ContextCompat.getDrawable
                        (holder.mIvInOrOutTransaction.getContext(), R.drawable.ic_in_transaction));
                break;
            default:
                holder.mIvInOrOutTransaction.setImageDrawable(ContextCompat.getDrawable
                        (holder.mIvInOrOutTransaction.getContext(), R.drawable.ic_self_transaction));
                break;
        }

        holder.mRlTransactionItem.setOnClickListener(c ->
                DialogFragmentTransactionDetailsBitcoin.newInstance(btcTransaction)
                        .show(((AppCompatActivity) mContext).getSupportFragmentManager(), ""));
    }

    @Override
    public int getItemCount() {
        return mBtcTransactionList.size();
    }

    class TransactionsBtcViewHolder extends RecyclerView.ViewHolder {

        TextView mTvDate, mTvAddress, mTvAmount;
        RelativeLayout mRlTransactionItem;
        ImageView mIvHourglass, mIvInOrOutTransaction;

        public TransactionsBtcViewHolder(View itemView) {
            super(itemView);
            mTvDate = itemView.findViewById(R.id.tv_date_item_transaction_history);
            mTvAddress = itemView.findViewById(R.id.tv_address_item_transaction_history);
            mTvAmount = itemView.findViewById(R.id.tv_amount_item_transaction_history);
            mRlTransactionItem = itemView.findViewById(R.id.rl_item_transaction);
            mIvHourglass = itemView.findViewById(R.id.iv_pending_transaction_item_transaction_history);
            mIvInOrOutTransaction = itemView.findViewById(R.id.iv_type_item_transaction_history);
        }
    }
}