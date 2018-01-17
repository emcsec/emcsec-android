package com.aspanta.emcsec.presenter.enterAnAddressBitcoinPresenter;



public interface IEnterAnAddressBitcoinPresenter   {
    //    void sendBitcoin(BitcoinTransactionPojo trasactionPojo);
    void unsubscribe();
    void sendBitcoin(String address, String amountToSend, String feePerKb);
    void getBtcBalance();
}
