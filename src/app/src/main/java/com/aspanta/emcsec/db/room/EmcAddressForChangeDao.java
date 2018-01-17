package com.aspanta.emcsec.db.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EmcAddressForChangeDao {

    @Query("SELECT * FROM emc_addresses_for_change")
    List<EmcAddressForChange> getAll();

    @Query("SELECT * FROM emc_addresses_for_change WHERE uid IN (:userIds)")
    List<EmcAddressForChange> loadAllByIds(int[] userIds);

    @Insert
    void insertAll(EmcAddressForChange... addresses);

    @Insert
    void insertListEmcAdresses(List<EmcAddressForChange> emcAddressList);

    @Delete
    void deleteAddress(EmcAddressForChange address);

    @Query("DELETE FROM emc_addresses_for_change")
    void deleteAll();
}
