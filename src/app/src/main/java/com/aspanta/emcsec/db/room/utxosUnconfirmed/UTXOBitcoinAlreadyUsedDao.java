package com.aspanta.emcsec.db.room.utxosUnconfirmed;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UTXOBitcoinAlreadyUsedDao {

    @Query("SELECT * FROM utxo_bitcoin_already_used")
    List<UTXOBitcoinAlreadyUsed> getAll();

    @Insert
    void insertAll(UTXOBitcoinAlreadyUsed... utxoBitcoinAlreadyUseds);

    @Insert
    void insertListUTXOBitcoinAlready(List<UTXOBitcoinAlreadyUsed> utxoBitcoinAlreadyUseds);

    @Delete
    void deleteAddress(UTXOBitcoinAlreadyUsed utxoBitcoinAlreadyUsed);

    @Query("DELETE FROM utxo_bitcoin_already_used")
    void deleteAll();
}
