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
import com.aspanta.emcsec.db.room.addressBook.EmcAddressBook;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.adapter.AddressBookEmercoinAdapter;
import com.aspanta.emcsec.ui.fragment.dialogFragmentAddAddressBook.DialogFragmentAddAddressBookEmercoin;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.SendCoinFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendEmercoinFragment.SendEmercoinFragment;

import java.util.ArrayList;
import java.util.List;

public class AddressBookEmercoinFragment extends Fragment implements IAddressBookEmercoinFragment {

    final String TAG = this.getClass().getName();

    private RecyclerView mRecyclerView;
    private ImageView mIvAdd;
    private TextView mTvNoAddresses;
    private List<EmcAddressBook> mListAddresses = new ArrayList<>();
    private AddressBookEmercoinAdapter mAddressBookEmercoinAdapter;
    public static String sFromFragment;

    public static AddressBookEmercoinFragment newInstance(String fromFragmant) {
        sFromFragment = fromFragmant;
        return new AddressBookEmercoinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAddresses = App.getDbInstance().emcAddressBookDao().getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emercoin_address_book, container, false);
        init(v);

        setAddressesList(mListAddresses);
        mIvAdd.setOnClickListener(c -> DialogFragmentAddAddressBookEmercoin.newInstance(this)
                .show(getFragmentManager(), ""));

        return v;
    }


    public void init(View view) {
        mRecyclerView = view.findViewById(R.id.rv_emercoin_address_book);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mTvNoAddresses = view.findViewById(R.id.tv_no_addresses);
        mIvAdd = view.findViewById(R.id.iv_add_address_book_emercoin);
    }

    @Override
    public void onBackPressed() {

        if (sFromFragment.equals("branch")){
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendEmercoinFragment.newInstance());
        } else {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.navigatorBackPressed(SendCoinFragment.newInstance(0));
        }
    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }

    @Override
    public void setAddressesList(List<EmcAddressBook> addressesList) {

        mTvNoAddresses.setVisibility(View.GONE);

        if (addressesList.isEmpty()) {
            mTvNoAddresses.setVisibility(View.VISIBLE);
        }
        mListAddresses = addressesList;
        mAddressBookEmercoinAdapter = new AddressBookEmercoinAdapter(addressesList, this);
        mRecyclerView.setAdapter(mAddressBookEmercoinAdapter);
    }

    @Override
    public void addNewAddress(EmcAddressBook address) {

    }
}