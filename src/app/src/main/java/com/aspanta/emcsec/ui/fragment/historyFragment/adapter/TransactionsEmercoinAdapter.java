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
import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;
import com.aspanta.emcsec.ui.fragment.dialogFragmentTransactionDetails.DialogFragmentTransactionDetailsEmercoin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TransactionsEmercoinAdapter extends RecyclerView.Adapter<TransactionsEmercoinAdapter.TransactionsEmcViewHolder> {

    private List<EmcTransaction> mEmcTransactionList;
    private Context mContext;

    public TransactionsEmercoinAdapter(List<EmcTransaction> mAddresses, Context context) {
        this.mEmcTransactionList = mAddresses;
        mContext = context;
    }

    @Override
    public TransactionsEmcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);

        return new TransactionsEmcViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(TransactionsEmcViewHolder holder, int position) {

        final EmcTransaction emcTransaction = mEmcTransactionList.get(position);

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        String dateString;
        if (emcTransaction.getDate().equals("")) {
            dateString = mContext.getString(R.string.unconfirmed);
        } else {
            Date date = new Date(Long.parseLong(emcTransaction.getDate()) * 1000);
            dateString = formatter.format(date);
        }

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);

        BigDecimal satoshiBigDecimal = new BigDecimal(Long.parseLong(emcTransaction.getAmount()));
        String resultAmount = df.format(satoshiBigDecimal.divide(new BigDecimal(1000000)));

        String resultFee;
        if (emcTransaction.getFee().equals("None") | emcTransaction.getFee().equals("")) {
            resultFee = "None";
        } else {
            BigDecimal satoshiBigDecimalFee = new BigDecimal(Long.parseLong(emcTransaction.getFee()));
            resultFee = df.format(satoshiBigDecimalFee.divide(new BigDecimal(1000000)));
        }

        if (emcTransaction.getBlock().equals("0") | emcTransaction.getBlock().equals("-1")) {
            holder.mIvHourglass.setVisibility(View.VISIBLE);
        }

        holder.mTvDate.setText(dateString);
        if (emcTransaction.getCategory().equals("Self")) {
            holder.mTvAddress.setText(mContext.getString(R.string.na));
            holder.mTvAmount.setText("-" + resultFee + " " + "EMC");
        } else {
            holder.mTvAddress.setText(emcTransaction.getAddress());
            holder.mTvAmount.setText(resultAmount + " " + "EMC");
        }

        switch (emcTransaction.getCategory()) {
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
                DialogFragmentTransactionDetailsEmercoin.newInstance(emcTransaction)
                        .show(((AppCompatActivity) mContext).getSupportFragmentManager(), ""));
    }

    @Override
    public int getItemCount() {
        return mEmcTransactionList.size();
    }

    class TransactionsEmcViewHolder extends RecyclerView.ViewHolder {

        TextView mTvDate, mTvAddress, mTvAmount;
        RelativeLayout mRlTransactionItem;
        ImageView mIvHourglass, mIvInOrOutTransaction;

        public TransactionsEmcViewHolder(View itemView) {
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