package com.aspanta.emcsec.ui.fragment.addressBookFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.addressBook.BtcAddressBook;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.adapter.AddressBookBitcoinAdapter;
import com.aspanta.emcsec.ui.fragment.dialogFragmentAddAddressBook.DialogFragmentAddAddressBookBitcoin;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.SendCoinFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendBitcoinFragment.SendBitcoinFragment;

import java.util.ArrayList;
import java.util.List;

public class AddressBookBitcoinFragment extends Fragment implements IAddressBookBitcoinFragment {

    final String TAG = this.getClass().getName();

    private RecyclerView mRecyclerView;
    private ImageView mIvAdd;
    private TextView mTvNoAddresses;
    private List<BtcAddressBook> mListAddresses = new ArrayList<>();
    private AddressBookBitcoinAdapter mAddressBookBitcoinAdapter;
    public static String sFromFragment;

    public static AddressBookBitcoinFragment newInstance(String fromFragment) {
        sFromFragment = fromFragment;
        return new AddressBookBitcoinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAddresses = App.getDbInstance().btcAddressBookDao().getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bitcoin_address_book, container, false);
        init(v);

        setAddressesList(mListAddresses);
        mIvAdd.setOnClickListener(c -> DialogFragmentAddAddressBookBitcoin.newInstance(this)
                .show(getFragmentManager(), ""));

        return v;
    }


    public void init(View view) {
        mRecyclerView = view.findViewById(R.id.rv_bitcoin_address_book);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mTvNoAddresses = view.findViewById(R.id.tv_no_addresses);
        mIvAdd = view.findViewById(R.id.iv_add_address_book_bitcoin);
    }

    @Override
    public void onBackPressed() {
        if (sFromFragment.equals("branch")) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendBitcoinFragment.newInstance());
        } else {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendCoinFragment.newInstance(1));
        }
    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }

    @Override
    public void setAddressesList(List<BtcAddressBook> addressesList) {

        mTvNoAddresses.setVisibility(View.GONE);

        if (addressesList.isEmpty()) {
            mTvNoAddresses.setVisibility(View.VISIBLE);
        }

        mListAddresses = addressesList;
        mAddressBookBitcoinAdapter = new AddressBookBitcoinAdapter(addressesList, this);
        mRecyclerView.setAdapter(mAddressBookBitcoinAdapter);
    }

    @Override
    public void addNewAddress(BtcAddressBook address) {

    }
}