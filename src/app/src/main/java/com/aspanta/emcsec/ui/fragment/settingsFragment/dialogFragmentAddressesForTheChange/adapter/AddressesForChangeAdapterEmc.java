package com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.ui.fragment.settingsFragment.dialogFragmentAddressesForTheChange.DialogFragmentAddressesForTheChangeEmc;

import java.util.List;


public class AddressesForChangeAdapterEmc extends RecyclerView.Adapter<AddressesForChangeAdapterEmc.ChangeAddressesEmcViewHolder> {

    private List<EmcAddressForChange> mEmcAddresses;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private String selectedAddress;

    public AddressesForChangeAdapterEmc(List<EmcAddressForChange> mAddresses, String currentAddress) {
        this.mEmcAddresses = mAddresses;
        selectedAddress = currentAddress;
        for (int i = 0; i < mEmcAddresses.size(); i++) {
            if (mEmcAddresses.get(i).getAddress().equals(selectedAddress)) {
                selectedPosition = i;
                break;
            }
        }
    }

    @Override
    public ChangeAddressesEmcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_address, parent, false);
        return new ChangeAddressesEmcViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ChangeAddressesEmcViewHolder holder, int position) {

        final EmcAddressForChange btcAddress = mEmcAddresses.get(position);
        holder.mTvAddress.setText(btcAddress.getAddress());
        holder.mLlChangeAddress.setBackgroundColor(selectedPosition == position ? Color.parseColor("#9c72b1") : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return mEmcAddresses.size();
    }

    class ChangeAddressesEmcViewHolder extends RecyclerView.ViewHolder {

        TextView mTvAddress;
        LinearLayout mLlChangeAddress;

        public ChangeAddressesEmcViewHolder(View itemView) {
            super(itemView);
            mLlChangeAddress = itemView.findViewById(R.id.ll_change_address);
            mTvAddress = itemView.findViewById(R.id.tv_address_for_change_item);

            mLlChangeAddress.setOnClickListener(view -> {

                if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                notifyItemChanged(selectedPosition);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
                DialogFragmentAddressesForTheChangeEmc.addressForChangeEmc = mEmcAddresses.get(selectedPosition).getAddress();
            });
        }
    }
}
