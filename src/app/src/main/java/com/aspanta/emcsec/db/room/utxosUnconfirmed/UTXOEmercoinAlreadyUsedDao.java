package com.aspanta.emcsec.db.room.utxosUnconfirmed;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UTXOEmercoinAlreadyUsedDao {

    @Query("SELECT * FROM utxo_emercoin_already_used")
    List<UTXOEmercoinAlreadyUsed> getAll();

    @Insert
    void insertAll(UTXOEmercoinAlreadyUsed... utxoEmercoinAlreadyUseds);

    @Insert
    void insertListUTXOEmercoinAlready(List<UTXOEmercoinAlreadyUsed> utxoEmercoinAlreadyUseds);

    @Delete
    void deleteAddress(UTXOEmercoinAlreadyUsed utxoEmercoinAlreadyUsed);

    @Query("DELETE FROM utxo_emercoin_already_used")
    void deleteAll();
}
