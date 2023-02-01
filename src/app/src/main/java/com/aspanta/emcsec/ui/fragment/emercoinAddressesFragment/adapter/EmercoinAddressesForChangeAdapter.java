package com.aspanta.emcsec.ui.fragment.emercoinAddressesFragment.adapter;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment.adapter.BitcoinAddressesAdapter;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.COPY_MESSAGE;


public class EmercoinAddressesForChangeAdapter extends RecyclerView
        .Adapter<EmercoinAddressesForChangeAdapter.AddressesEmercoinViewHolder> {

    private List<EmcAddressForChange> mListAddresses;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private EmercoinAddressesAdapter.OnAddressClickedListener listener;

    public EmercoinAddressesForChangeAdapter(List<EmcAddressForChange> mAddresses,
                                             EmercoinAddressesAdapter.OnAddressClickedListener listener) {
        this.mListAddresses = mAddresses;
        this.listener = listener;
        binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public AddressesEmercoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_addresses, parent, false);
        return new AddressesEmercoinViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddressesEmercoinViewHolder holder, int position) {

        holder.tvLabel.setText(holder.tvLabel.getContext().getString(R.string.address_for_the_change));
        holder.tvLabel.setTextColor(ContextCompat.getColor(holder.tvLabel.getContext(), R.color.navigation_drawer_bg));
        holder.tvAddress.setText(mListAddresses.get(position).getAddress());
        binderHelper.bind(holder.swipeLayout, mListAddresses.get(position).getAddress());

        holder.editAddress.setVisibility(View.GONE);

        holder.exportPriv.setOnClickListener(view -> {
            listener.exportPriv(mListAddresses.get(position).getAddress());
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

    class AddressesEmercoinViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel, tvAddress;
        RelativeLayout llAddressItem;
        SwipeRevealLayout swipeLayout;
        View editAddress, exportPriv;

        AddressesEmercoinViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_address_label);
            tvAddress = itemView.findViewById(R.id.tv_address_address);
            llAddressItem = itemView.findViewById(R.id.rl_item_address);
            swipeLayout = itemView.findViewById(R.id.swipe_layout_item_address);
            editAddress = itemView.findViewById(R.id.edit_address);
            exportPriv = itemView.findViewById(R.id.export_priv);
        }
    }

    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }
}