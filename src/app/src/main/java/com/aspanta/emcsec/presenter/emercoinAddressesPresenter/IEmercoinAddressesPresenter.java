package com.aspanta.emcsec.presenter.emercoinAddressesPresenter;


import com.aspanta.emcsec.db.room.EmcAddress;

import java.util.List;

public interface IEmercoinAddressesPresenter {
    void getAddressesList();

    void unsubscribe();

    void editAddress(EmcAddress address);

    void saveAddresses(List<EmcAddress> addresses);

    void showEditDialog(EmcAddress addressPojo);

}
