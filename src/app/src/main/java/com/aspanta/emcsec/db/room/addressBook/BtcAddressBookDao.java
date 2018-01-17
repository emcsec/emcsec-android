package com.aspanta.emcsec.db.room.addressBook;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BtcAddressBookDao {

    @Query("SELECT * FROM btc_address_book")
    List<BtcAddressBook> getAll();

    @Query("SELECT * FROM btc_address_book WHERE uid IN (:userIds)")
    List<BtcAddressBook> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM btc_address_book WHERE label LIKE :first AND "
            + "address LIKE :last LIMIT 1")
    BtcAddressBook findByLabel(String first, String last);

    @Insert
    void insertAll(BtcAddressBook... addresses);

    @Insert
    void insertListBtcAdresses(List<BtcAddressBook> btcAddressList);

    @Delete
    void deleteAddress(BtcAddressBook address);

    @Query("DELETE FROM btc_address_book")
    void deleteAll();
}
