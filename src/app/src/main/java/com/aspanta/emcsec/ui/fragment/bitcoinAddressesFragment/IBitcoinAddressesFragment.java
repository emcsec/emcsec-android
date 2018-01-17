package com.aspanta.emcsec.ui.fragment.bitcoinAddressesFragment;


import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;

import java.util.List;

public interface IBitcoinAddressesFragment extends IBaseFragment {
    void setAddressesList(List<BtcAddress> addresses);

    void showPleaseWaitDialog();

    void hidePleaseWaitDialog();

    List<BtcAddress> getAddressesList();

    void updateAddresses();
}