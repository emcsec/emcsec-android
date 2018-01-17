package com.aspanta.emcsec.db.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BtcAddressDao {

    @Query("SELECT * FROM btc_addresses")
    List<BtcAddress> getAll();

    @Query("SELECT * FROM btc_addresses WHERE uid IN (:userIds)")
    List<BtcAddress> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM btc_addresses WHERE label LIKE :first AND "
            + "address LIKE :last LIMIT 1")
    BtcAddress findByLabel(String first, String last);

    @Insert
    void insertAll(BtcAddress... addresses);

    @Insert
    void insertListBtcAdresses(List<BtcAddress> btcAddressList);

    @Delete
    void deleteAddress(BtcAddress address);

    @Query("DELETE FROM btc_addresses")
    void deleteAll();
}
