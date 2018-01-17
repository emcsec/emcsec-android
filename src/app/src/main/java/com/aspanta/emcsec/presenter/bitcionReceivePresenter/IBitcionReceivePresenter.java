package com.aspanta.emcsec.presenter.bitcionReceivePresenter;


import android.widget.EditText;
import android.widget.Spinner;

import com.aspanta.emcsec.db.room.BtcAddress;

import java.util.List;

public interface IBitcionReceivePresenter {
    List<BtcAddress> getListAddressesForSpinner();
    void validateField(EditText text, Spinner sp);
    void generateQR(int pos, String amount);
    void copyToBuffer(int pos);
    void unsubscribe();
    void send(int pos);
}

