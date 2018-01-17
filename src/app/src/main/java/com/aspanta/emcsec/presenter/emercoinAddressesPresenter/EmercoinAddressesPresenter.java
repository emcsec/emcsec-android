package com.aspanta.emcsec.presenter.emercoinAddressesPresenter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.ui.fragment.dialogFragmentEditAddressEmercoin.DialogFragmentEditAddressEmercoin;
import com.aspanta.emcsec.ui.fragment.emercoinAddressesFragment.IEmercoinAddressesFragment;

import java.util.List;


public class EmercoinAddressesPresenter implements IEmercoinAddressesPresenter {

    private IEmercoinAddressesFragment mAddressEmercoinFragment;
    private Context mContext;

    public EmercoinAddressesPresenter(Context context, IEmercoinAddressesFragment addressEmercoinFragment) {
        mContext = context;
        this.mAddressEmercoinFragment = addressEmercoinFragment;
    }

    @Override
    public void getAddressesList() {
        new AsyncTask<Void, Void, List<EmcAddress>>() {
            @Override
            protected List doInBackground(Void... params) {
                return App.getDbInstance().emcAddressDao().getAll();
            }
            @Override
            protected void onPostExecute(List<EmcAddress> addresses) {

                mAddressEmercoinFragment.setAddressesList(addresses);

            }
        }.execute();
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void editAddress(EmcAddress address) {

        List<EmcAddress> addressList = mAddressEmercoinFragment.getAddressesList();

        for (EmcAddress emcAddress : addressList) {
            if (emcAddress.getAddress().equals(address.getAddress())) {
                emcAddress.setLabel(address.getLabel());

                saveAddresses(addressList);

                break;
            }
        }
        mAddressEmercoinFragment.updateAddresses();
    }

    @Override
    public void saveAddresses(List<EmcAddress> addresses) {
        new AsyncTask<Object, Void, List<EmcAddress>>() {
            @Override
            protected List<EmcAddress> doInBackground(Object... params) {
                App.getDbInstance().emcAddressDao().deleteAll();
                App.getDbInstance().emcAddressDao().insertListEmcAdresses(addresses);
                return addresses;
            }
        }.execute();
    }

    @Override
    public void showEditDialog(EmcAddress addressPojo) {
        DialogFragmentEditAddressEmercoin.newInstance(this,
                addressPojo).show(((Fragment)mAddressEmercoinFragment).getActivity()
                .getSupportFragmentManager(),"");
    }
}
