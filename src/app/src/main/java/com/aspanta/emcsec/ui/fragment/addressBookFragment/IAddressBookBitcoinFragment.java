package com.aspanta.emcsec.ui.fragment.addressBookFragment;


import com.aspanta.emcsec.db.room.addressBook.BtcAddressBook;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;

import java.util.List;

public interface IAddressBookBitcoinFragment extends IBaseFragment {

    void setAddressesList(List<BtcAddressBook> addressesList);

    void addNewAddress(BtcAddressBook address);

}
