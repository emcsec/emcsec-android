package com.aspanta.emcsec.presenter.bitcoinAddressPresenter;


import com.aspanta.emcsec.db.room.BtcAddress;

import java.util.List;

public interface IBitcoinAddressesPresenter {
    void getAddressesList();

    void unsubscribe();

    void saveAddresses(List<BtcAddress> addresses);

    void editAddress(BtcAddress addressPojo);

    void showEditDialog(BtcAddress addressPojo);
}

