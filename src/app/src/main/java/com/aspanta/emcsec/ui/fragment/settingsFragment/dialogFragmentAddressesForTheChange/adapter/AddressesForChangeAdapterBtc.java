package com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.DialogFragmentAddressesForTheChangeBtc;

import java.util.List;


public class AddressesForChangeAdapterBtc extends RecyclerView.Adapter<AddressesForChangeAdapterBtc.ChangeAddressesBtcViewHolder> {

    private List<BtcAddressForChange> mBtcAddresses;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private String selectedAddress;

    public AddressesForChangeAdapterBtc(List<BtcAddressForChange> mAddresses, String currentAddress) {
        this.mBtcAddresses = mAddresses;
        selectedAddress = currentAddress;
        for (int i = 0; i < mBtcAddresses.size(); i++) {
            if (mBtcAddresses.get(i).getAddress().equals(selectedAddress)) {
                selectedPosition = i;
                break;
            }
        }
    }

    @Override
    public ChangeAddressesBtcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_address, parent, false);
        return new ChangeAddressesBtcViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ChangeAddressesBtcViewHolder holder, int position) {

        final BtcAddressForChange btcAddress = mBtcAddresses.get(position);
        holder.mTvAddress.setText(btcAddress.getAddress());
        holder.mLlChangeAddress.setBackgroundColor(selectedPosition == position ? Color.parseColor("#FC8335") : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return mBtcAddresses.size();
    }

    class ChangeAddressesBtcViewHolder extends RecyclerView.ViewHolder {

        TextView mTvAddress;
        LinearLayout mLlChangeAddress;

        public ChangeAddressesBtcViewHolder(View itemView) {
            super(itemView);
            mTvAddress = itemView.findViewById(R.id.tv_address_for_change_item);
            mLlChangeAddress = itemView.findViewById(R.id.ll_change_address);

            mLlChangeAddress.setOnClickListener(view -> {

                if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                notifyItemChanged(selectedPosition);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
                DialogFragmentAddressesForTheChangeBtc.addressForChangeBtc = mBtcAddresses.get(selectedPosition).getAddress();
            });
        }
    }
}
