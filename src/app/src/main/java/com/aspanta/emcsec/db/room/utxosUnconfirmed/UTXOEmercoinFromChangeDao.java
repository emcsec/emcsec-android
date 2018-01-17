package com.aspanta.emcsec.db.room.utxosUnconfirmed;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UTXOEmercoinFromChangeDao {

    @Query("SELECT * FROM utxo_emercoin_from_change")
    List<UTXOEmercoinFromChange> getAll();

    @Insert
    void insertAll(UTXOEmercoinFromChange... utxoEmercoinFromChanges);

    @Insert
    void insertListUTXOEmercoinFromChange(List<UTXOEmercoinFromChange> utxoEmercoinFromChanges);

    @Delete
    void deleteAddress(UTXOEmercoinFromChange utxoEmercoinFromChange);

    @Query("DELETE FROM utxo_emercoin_from_change")
    void deleteAll();
}
