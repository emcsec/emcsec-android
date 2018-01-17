package com.aspanta.emcsec.presenter.bitcoinAddressPresenter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment.IBitcoinAddressesFragment;
import com.aspanta.emcsec.ui.fragment.dialogFragmentEditAddressBitcoin.DialogFragmentEditAddressBitcoin;

import java.util.List;


public class BitcoinAddressesPresenter implements IBitcoinAddressesPresenter {

    private IBitcoinAddressesFragment mAddressBitcoinFragment;
    private Context mContext;

    public BitcoinAddressesPresenter(Context context, IBitcoinAddressesFragment addressBitcoinFragment) {
        mContext = context;
        this.mAddressBitcoinFragment = addressBitcoinFragment;
    }

    @Override
    public void getAddressesList() {
        new AsyncTask<Void, Void, List<BtcAddress>>() {
            @Override
            protected List doInBackground(Void... params) {
                return App.getDbInstance().btcAddressDao().getAll();
            }

            @Override
            protected void onPostExecute(List<BtcAddress> addresses) {

                mAddressBitcoinFragment.setAddressesList(addresses);

            }
        }.execute();
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void saveAddresses(List<BtcAddress> addresses) {
        new AsyncTask<Object, Void, List<BtcAddress>>() {
            @Override
            protected List<BtcAddress> doInBackground(Object... params) {
                App.getDbInstance().btcAddressDao().deleteAll();
                App.getDbInstance().btcAddressDao().insertListBtcAdresses(addresses);
                return addresses;
            }
        }.execute();
    }

    @Override
    public void editAddress(BtcAddress address) {

        List<BtcAddress> addressList = mAddressBitcoinFragment.getAddressesList();

        for (BtcAddress btcAddress : addressList) {
            if (btcAddress.getAddress().equals(address.getAddress())) {
                btcAddress.setLabel(address.getLabel());

                saveAddresses(addressList);

                break;
            }
        }
        mAddressBitcoinFragment.updateAddresses();
    }

    @Override
    public void showEditDialog(BtcAddress addressPojo) {
        DialogFragmentEditAddressBitcoin.newInstance(this,
                addressPojo).show(((Fragment) mAddressBitcoinFragment).getActivity()
                .getSupportFragmentManager(), "");
    }
}