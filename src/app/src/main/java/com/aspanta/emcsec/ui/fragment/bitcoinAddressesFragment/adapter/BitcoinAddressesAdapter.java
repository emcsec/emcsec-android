package com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.presenter.bitcoinAddressPresenter.IBitcoinAddressesPresenter;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.COPY_MESSAGE;


public class BitcoinAddressesAdapter extends RecyclerView
        .Adapter<BitcoinAddressesAdapter.AddressesBitcoinViewHolder> {

    private List<BtcAddress> mListAddresses;
    private IBitcoinAddressesPresenter bitcoinAddressesPresenter;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private boolean mIsNewAddress;


    public BitcoinAddressesAdapter(List<BtcAddress> mAddresses,
                                   IBitcoinAddressesPresenter bitcoinAddressesPresenter) {
        this.bitcoinAddressesPresenter = bitcoinAddressesPresenter;
        this.mListAddresses = mAddresses;
        binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public AddressesBitcoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_addresses, parent, false);
        return new AddressesBitcoinViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddressesBitcoinViewHolder holder, int position) {
        if (mIsNewAddress) {
            if (position == 0)
                holder.llAddressItem.setBackgroundColor
                        (ContextCompat.getColor(holder.editLayout.getContext(), R.color.colorItemNewAddressBitcoin));
        }

        holder.tvLabel.setText(mListAddresses.get(position).getLabel());
        holder.tvAddress.setText(mListAddresses.get(position).getAddress());
        binderHelper.bind(holder.swipeLayout, mListAddresses.get(position).getAddress());
        holder.editLayout.setOnClickListener(view -> {
            bitcoinAddressesPresenter.showEditDialog(mListAddresses.get(position));
        });

        holder.llAddressItem.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager)
                    holder.llAddressItem.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", mListAddresses.get(position).getAddress());
            clipboard.setPrimaryClip(clip);
            Log.d("ADAPTER: ", COPY_MESSAGE);
            Toast.makeText(holder.llAddressItem.getContext(), holder.llAddressItem.getContext().getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListAddresses.size();
    }

    public void setIsNewAddress(boolean isNewAddress) {
        this.mIsNewAddress = isNewAddress;
    }


    class AddressesBitcoinViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel, tvAddress;
        LinearLayout llAddressItem;
        SwipeRevealLayout swipeLayout;
        View editLayout;


        AddressesBitcoinViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_address_label);
            tvAddress = itemView.findViewById(R.id.tv_address_address);
            llAddressItem = itemView.findViewById(R.id.rl_item_address);
            swipeLayout = itemView.findViewById(R.id.swipe_layout_item_address);
            editLayout = itemView.findViewById(R.id.edit_layout);
        }
    }


    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }
}