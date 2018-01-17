package com.aspanta.emcsec.ui.fragment.addressBookFragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.addressBook.EmcAddressBook;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.AddressBookEmercoinFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentDeleteAddressBook.DialogFragmentDeleteAddressBookEmercoin;
import com.aspanta.emcsec.ui.fragment.dialogFragmentEditContactAddressBook.DialogFragmentEditContactAddressBookEmercoin;
import com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment.EnterAnAddressEmercoinFragment;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;


public class AddressBookEmercoinAdapter extends RecyclerView.Adapter<AddressBookEmercoinAdapter.AddressBookEmercoinViewHolder> {

    private List<EmcAddressBook> mListAddresses;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    AddressBookEmercoinFragment addressBookEmercoinFragment;

    public AddressBookEmercoinAdapter(List<EmcAddressBook> mAddresses, AddressBookEmercoinFragment addressBookEmercoinFragment) {
        this.addressBookEmercoinFragment = addressBookEmercoinFragment;
        this.mListAddresses = mAddresses;
        binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public AddressBookEmercoinAdapter.AddressBookEmercoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_book, parent, false);

        return new AddressBookEmercoinViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(AddressBookEmercoinViewHolder holder, int position) {

        // new addressPojo model for add new contact
        final EmcAddressBook addressPojo = new EmcAddressBook
                (mListAddresses.get(position).getAddress(), mListAddresses.get(position).getLabel());

        // getting addressPojo model from existing AddressBook List
        final EmcAddressBook addressPojoForEdit = mListAddresses.get(position);

        holder.mTvLabel.setText(mListAddresses.get(position).getLabel());
        holder.mTvAddress.setText(mListAddresses.get(position).getAddress());

        holder.mIvEdit.setOnClickListener(c -> {
            DialogFragmentEditContactAddressBookEmercoin.newInstance(addressBookEmercoinFragment, addressPojoForEdit, position)
                    .show(addressBookEmercoinFragment.getFragmentManager(), "");
        });

        holder.mIvDelete.setOnClickListener(c -> {
            DialogFragmentDeleteAddressBookEmercoin.newInstance(addressBookEmercoinFragment, position)
                    .show(addressBookEmercoinFragment.getFragmentManager(), "");
        });

        holder.mRlAddressItem.setOnClickListener(c -> {

            if(AddressBookEmercoinFragment.sFromFragment.equals("branch")){
                EnterAnAddressEmercoinFragment fragment = EnterAnAddressEmercoinFragment.newInstance(mListAddresses.get(position).getAddress(), "", "branch");
                ((MainActivity)addressBookEmercoinFragment.getActivity()).navigator(fragment, fragment.getCurrentTag());
            } else {
                EnterAnAddressEmercoinFragment fragment = EnterAnAddressEmercoinFragment.newInstance(mListAddresses.get(position).getAddress(), "", "");
                ((MainActivity)addressBookEmercoinFragment.getActivity()).navigator(fragment, fragment.getCurrentTag());
            }
        });

        binderHelper.bind(holder.mSwipeLayout, String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return mListAddresses.size();
    }

    class AddressBookEmercoinViewHolder extends RecyclerView.ViewHolder {

        TextView mTvLabel, mTvAddress;
        RelativeLayout mRlAddressItem;
        SwipeRevealLayout mSwipeLayout;
        ImageView mIvEdit, mIvDelete;

        public AddressBookEmercoinViewHolder(View itemView) {
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
