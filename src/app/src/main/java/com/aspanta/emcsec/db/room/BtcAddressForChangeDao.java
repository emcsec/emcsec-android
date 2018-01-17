package com.aspanta.emcsec.db.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BtcAddressForChangeDao {

    @Query("SELECT * FROM btc_addresses_for_change")
    List<BtcAddressForChange> getAll();

    @Query("SELECT * FROM btc_addresses_for_change WHERE uid IN (:userIds)")
    List<BtcAddressForChange> loadAllByIds(int[] userIds);

    @Insert
    void insertAll(BtcAddressForChange... addresses);

    @Insert
    void insertListBtcAdresses(List<BtcAddressForChange> btcAddressList);

    @Delete
    void deleteAddress(BtcAddressForChange address);

    @Query("DELETE FROM btc_addresses_for_change")
    void deleteAll();
}
