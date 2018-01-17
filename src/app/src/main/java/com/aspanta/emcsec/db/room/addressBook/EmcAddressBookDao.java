package com.aspanta.emcsec.db.room.addressBook;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EmcAddressBookDao {

    @Query("SELECT * FROM emc_address_book")
    List<EmcAddressBook> getAll();

    @Query("SELECT * FROM emc_address_book WHERE uid IN (:userIds)")
    List<EmcAddressBook> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM emc_address_book WHERE label LIKE :first AND "
            + "address LIKE :last LIMIT 1")
    EmcAddressBook findByLabel(String first, String last);

    @Insert
    void insertAll(EmcAddressBook... addresses);

    @Insert
    void insertListEmcAdresses(List<EmcAddressBook> emcAddressList);

    @Delete
    void deleteAddress(EmcAddressBook address);

    @Query("DELETE FROM emc_address_book")
    void deleteAll();
}
