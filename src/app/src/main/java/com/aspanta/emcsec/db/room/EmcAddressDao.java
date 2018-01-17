package com.aspanta.emcsec.db.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EmcAddressDao {

    @Query("SELECT * FROM emc_addresses")
    List<EmcAddress> getAll();

    @Query("SELECT * FROM emc_addresses WHERE uid IN (:userIds)")
    List<EmcAddress> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM emc_addresses WHERE label LIKE :first AND "
            + "address LIKE :last LIMIT 1")
    EmcAddress findByLabel(String first, String last);

    @Insert
    void insertAll(EmcAddress... addresses);

    @Insert
    void insertListEmcAdresses(List<EmcAddress> emcAddressList);

    @Delete
    void deleteAddress(EmcAddress address);

    @Query("DELETE FROM emc_addresses")
    void deleteAll();
}
