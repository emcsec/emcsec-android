package com.aspanta.emcsec.db.room.addressBook;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "emc_address_book")
public class EmcAddressBook {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "label")
    private String label;

    @ColumnInfo(name = "address")
    private String address;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public EmcAddressBook(String label, String address) {
        this.label = label;
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmcAddressBook that = (EmcAddressBook) o;

        return uid == that.uid;

    }

    @Override
    public int hashCode() {
        return uid;
    }

    @Override
    public String toString() {
        return "EmcAddressBook{" +
                "uid=" + uid +
                ", label='" + label + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
