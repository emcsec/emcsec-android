package com.aspanta.emcsec.db.room.historyPojos;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EmcTransactionDao {

    @Query("SELECT * FROM emc_transaction")
    List<EmcTransaction> getAll();

    @Insert
    void insertAll(EmcTransaction... transactions);

    @Insert
    void insertListEmcAdresses(List<EmcTransaction> emcTransactionList);

    @Delete
    void deleteAddress(EmcTransaction transaction);

    @Query("DELETE FROM emc_transaction")
    void deleteAll();
}
