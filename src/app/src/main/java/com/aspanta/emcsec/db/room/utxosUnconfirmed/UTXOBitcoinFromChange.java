package com.aspanta.emcsec.db.room.utxosUnconfirmed;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "utxo_bitcoin_from_change")
public class UTXOBitcoinFromChange {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "tx_id")
    private String tx_id;

    @ColumnInfo(name = "index")
    private long index;

    @ColumnInfo(name = "value")
    private long value;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "time")
    private long timeToRemove;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTx_id() {
        return tx_id;
    }

    public void setTx_id(String tx_id) {
        this.tx_id = tx_id;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTimeToRemove() {
        return timeToRemove;
    }

    public void setTimeToRemove(long timeToRemove) {
        this.timeToRemove = timeToRemove;
    }

    public UTXOBitcoinFromChange(String tx_id, long index, long value, String address, long timeToRemove) {
        this.tx_id = tx_id;
        this.index = index;
        this.value = value;
        this.address = address;
        this.timeToRemove = timeToRemove;
    }

    @Override
    public String toString() {
        return "UTXOBitcoinFromChange{" +
                "uid=" + uid +
                ", tx_id='" + tx_id + '\'' +
                ", index=" + index +
                ", value=" + value +
                ", address='" + address + '\'' +
                ", timeToRemove=" + timeToRemove +
                '}' + "\n";
    }
}
