package com.aspanta.emcsec.ui.fragment.emercoinAddressesFragment;


import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;

import java.util.List;

public interface IEmercoinAddressesFragment extends IBaseFragment {
    void setAddressesList(List<EmcAddress> addressesList);

    void showPleaseWaitDialog();

    void hidePleaseWaitDialog();

    List<EmcAddress> getAddressesList();

    void updateAddresses();
}
