package com.aspanta.emcsec.db.room.historyPojos;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BtcTransactionDao {

    @Query("SELECT * FROM btc_transaction")
    List<BtcTransaction> getAll();

    @Insert
    void insertAll(BtcTransaction... transactions);

    @Insert
    void insertListBtcAdresses(List<BtcTransaction> btcTransactionList);

    @Delete
    void deleteAddress(BtcTransaction transaction);

    @Query("DELETE FROM btc_transaction")
    void deleteAll();
}
