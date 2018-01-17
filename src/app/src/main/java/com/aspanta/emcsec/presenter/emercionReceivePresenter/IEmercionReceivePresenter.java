package com.aspanta.emcsec.presenter.emercionReceivePresenter;

import android.widget.EditText;
import android.widget.Spinner;

import com.aspanta.emcsec.db.room.EmcAddress;

import java.util.List;

public interface IEmercionReceivePresenter  {
    List<EmcAddress> getListAddressesForSpinner();
    void validateField(EditText text, Spinner sp);
    void generateQR(int pos, String amount);
    void copyToBuffer(int pos);
    void unsubscribe();
    void send(int pos);
}
