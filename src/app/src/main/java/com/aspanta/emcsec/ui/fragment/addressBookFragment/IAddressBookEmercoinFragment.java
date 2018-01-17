package com.aspanta.emcsec.ui.fragment.addressBookFragment;


import com.aspanta.emcsec.db.room.addressBook.EmcAddressBook;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;

import java.util.List;

public interface IAddressBookEmercoinFragment extends IBaseFragment {

    void setAddressesList(List<EmcAddressBook> addressesList);

    void addNewAddress(EmcAddressBook address);

}
