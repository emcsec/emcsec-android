package com.aspanta.emcsec.db.room.utxosUnconfirmed;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UTXOBitcoinFromChangeDao {

    @Query("SELECT * FROM utxo_bitcoin_from_change")
    List<UTXOBitcoinFromChange> getAll();

    @Insert
    void insertAll(UTXOBitcoinFromChange... utxoBitcoinFromChanges);

    @Insert
    void insertListUTXOBitcoinFromChange(List<UTXOBitcoinFromChange> utxoBitcoinFromChanges);

    @Delete
    void deleteAddress(UTXOBitcoinFromChange utxoBitcoinFromChange);

    @Query("DELETE FROM utxo_bitcoin_from_change")
    void deleteAll();
}
