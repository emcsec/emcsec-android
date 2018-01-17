package com.aspanta.emcsec.presenter.enterAnAddressEmercoinPresenter;


public interface IEnterAnAddressEmercoinPresenter {
    void sendEmercoin(String address, String amountToSend);

    void unsubscribe();

    void getEmcBalance();
}