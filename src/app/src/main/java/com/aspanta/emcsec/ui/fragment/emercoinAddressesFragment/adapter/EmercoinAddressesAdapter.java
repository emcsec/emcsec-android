package com.aspanta.emcsec.ui.fragment.emercoinAddressesFragment.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.presenter.emercoinAddressesPresenter.IEmercoinAddressesPresenter;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;


public class EmercoinAddressesAdapter extends RecyclerView.Adapter<EmercoinAddressesAdapter.AddressesEmercoinViewHolder> {

    private List<EmcAddress> mListAddresses;
    private IEmercoinAddressesPresenter mEmercoinAddressesPresenter;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private boolean mIsNewAddress;

    public EmercoinAddressesAdapter(List<EmcAddress> mAddresses,
                                    IEmercoinAddressesPresenter mEmercoinAddressesPresenter) {
        this.mEmercoinAddressesPresenter = mEmercoinAddressesPresenter;
        this.mListAddresses = mAddresses;
        binderHelper.setOpenOnlyOne(true);

    }

    @Override
    public EmercoinAddressesAdapter.AddressesEmercoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_addresses, parent, false);
        return new AddressesEmercoinViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(AddressesEmercoinViewHolder holder, int position) {
        if(mIsNewAddress){
            if (position == 0)
                holder.llAddressItem.setBackgroundColor(ContextCompat.getColor
                        (holder.editLayout.getContext(), R.color.colorItemNewAddressEmercoin));
        }
        holder.tvLabel.setText(mListAddresses.get(position).getLabel());
        holder.tvAddress.setText(mListAddresses.get(position).getAddress());
        binderHelper.bind(holder.swipeLayout, mListAddresses.get(position).getAddress());
        holder.editLayout.setOnClickListener(view -> {
            mEmercoinAddressesPresenter.showEditDialog(mListAddresses.get(position));
        });

        holder.llAddressItem.setOnClickListener(view -> {

            ClipboardManager clipboard = (ClipboardManager)
                    holder.llAddressItem.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", mListAddresses.get(position).getAddress());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(holder.llAddressItem.getContext(), holder.llAddressItem.getContext().getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return mListAddresses.size();
    }

    class AddressesEmercoinViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel, tvAddress;
        LinearLayout llAddressItem;
        SwipeRevealLayout swipeLayout;
        View editLayout;


        AddressesEmercoinViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_address_label);
            tvAddress = itemView.findViewById(R.id.tv_address_address);
            llAddressItem = itemView.findViewById(R.id.rl_item_address);
            swipeLayout = itemView.findViewById(R.id.swipe_layout_item_address);
            editLayout = itemView.findViewById(R.id.edit_layout);
        }
    }
}