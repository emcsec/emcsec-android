package com.aspanta.emcsec.ui.fragment.addressBookFragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.addressBook.BtcAddressBook;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.AddressBookBitcoinFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentDeleteAddressBook.DialogFragmentDeleteAddressBookBitcoin;
import com.aspanta.emcsec.ui.fragment.dialogFragmentEditContactAddressBook.DialogFragmentEditContactAddressBookBitcoin;
import com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment.EnterAnAddressBitcoinFragment;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;


public class AddressBookBitcoinAdapter extends RecyclerView.Adapter<AddressBookBitcoinAdapter.AddressBookBitcoinViewHolder> {

    private List<BtcAddressBook> mListAddresses;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    AddressBookBitcoinFragment addressBookBitcoinFragment;

    public AddressBookBitcoinAdapter(List<BtcAddressBook> mAddresses, AddressBookBitcoinFragment addressBookBitcoinFragment) {
        this.addressBookBitcoinFragment = addressBookBitcoinFragment;
        this.mListAddresses = mAddresses;
        binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public AddressBookBitcoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_book, parent, false);

        return new AddressBookBitcoinViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(AddressBookBitcoinViewHolder holder, int position) {

        // new addressPojo model for add new contact
        final BtcAddressBook btcAddressBook = new BtcAddressBook
                (mListAddresses.get(position).getAddress(), mListAddresses.get(position).getLabel());

        // getting addressPojo model from existing AddressBook List
        final BtcAddressBook addressPojoForEdit = mListAddresses.get(position);

        holder.mTvLabel.setText(mListAddresses.get(position).getLabel());
        holder.mTvAddress.setText(mListAddresses.get(position).getAddress());

        holder.mIvEdit.setOnClickListener(c -> {
            DialogFragmentEditContactAddressBookBitcoin.newInstance(addressBookBitcoinFragment, addressPojoForEdit, position)
                    .show(addressBookBitcoinFragment.getFragmentManager(), "");
        });

        holder.mIvDelete.setOnClickListener(c -> {
            DialogFragmentDeleteAddressBookBitcoin.newInstance(addressBookBitcoinFragment, position)
                    .show(addressBookBitcoinFragment.getFragmentManager(), "");
        });

        holder.mRlAddressItem.setOnClickListener(c -> {
            if (AddressBookBitcoinFragment.sFromFragment.equals("branch")) {
                EnterAnAddressBitcoinFragment fragment = EnterAnAddressBitcoinFragment.newInstance(mListAddresses.get(position).getAddress(), "", "branch");
                ((MainActivity) addressBookBitcoinFragment.getActivity()).navigator(fragment, fragment.getCurrentTag());
            } else {
                EnterAnAddressBitcoinFragment fragment = EnterAnAddressBitcoinFragment.newInstance(mListAddresses.get(position).getAddress(), "", "");
                ((MainActivity) addressBookBitcoinFragment.getActivity()).navigator(fragment, fragment.getCurrentTag());
            }
        });

        binderHelper.bind(holder.mSwipeLayout, String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return mListAddresses.size();
    }

    class AddressBookBitcoinViewHolder extends RecyclerView.ViewHolder {

        TextView mTvLabel, mTvAddress;
        RelativeLayout mRlAddressItem;
        SwipeRevealLayout mSwipeLayout;
        ImageView mIvEdit, mIvDelete;

        public AddressBookBitcoinViewHolder(View itemView) {
            super(itemView);
            mTvLabel = itemView.findViewById(R.id.tv_address_label);
            mTvAddress = itemView.findViewById(R.id.tv_address_address);
            mRlAddressItem = itemView.findViewById(R.id.rl_item_address);
            mSwipeLayout = itemView.findViewById(R.id.swipe_layout_item_address_book);
            mIvEdit = itemView.findViewById(R.id.iv_edit_item_address_book);
            mIvDelete = itemView.findViewById(R.id.iv_delete_item_address_book);
        }
    }
}
