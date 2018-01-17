package com.aspanta.emcsec.db.room.historyPojos;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.aspanta.emcsec.model.supportPojos.Input;

import java.util.List;

@Entity(tableName = "emc_transaction")
public class EmcTransaction implements Comparable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "fee")
    private String fee;

    @ColumnInfo(name = "tx_id")
    private String tx_id;

    @ColumnInfo(name = "block")
    private String block;

    @Ignore
    private List<Input> mInputList;

    @Ignore
    private long totalAmountInOutPuts;

    public EmcTransaction(String address, String category, String amount, String fee, String tx_id, String block) {
        this.address = address;
        this.category = category;
        this.amount = amount;
        this.fee = fee;
        this.tx_id = tx_id;
        this.block = block;
    }

    public EmcTransaction(String date, String address, String category, String amount, String fee, String tx_id, String block, List<Input> inputList, long totalAmountInOutPuts) {
        this.date = date;
        this.address = address;
        this.category = category;
        this.amount = amount;
        this.fee = fee;
        this.tx_id = tx_id;
        this.block = block;
        this.mInputList = inputList;
        this.totalAmountInOutPuts = totalAmountInOutPuts;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTx_id() {
        return tx_id;
    }

    public void setTx_id(String tx_id) {
        this.tx_id = tx_id;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public List<Input> getInputList() {
        return mInputList;
    }

    public void setInputList(List<Input> inputList) {
        mInputList = inputList;
    }

    public long getTotalAmountInOutPuts() {
        return totalAmountInOutPuts;
    }

    public void setTotalAmountInOutPuts(long totalAmountInOutPuts) {
        this.totalAmountInOutPuts = totalAmountInOutPuts;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        EmcTransaction that = (EmcTransaction) o;
//
//        if (address != null ? !address.equals(that.address) : that.address != null) return false;
//        if (tx_id != null ? !tx_id.equals(that.tx_id) : that.tx_id != null) return false;
//        if (block != null ? !block.equals(that.block) : that.block != null) return false;
//
//        return true;
//    }

//    @Override
//    public int hashCode() {
//        int result = address != null ? address.hashCode() : 0;
//        result = 31 * result + (tx_id != null ? tx_id.hashCode() : 0);
//        result = 31 * result + (block != null ? block.hashCode() : 0);
//        return result;
//    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        EmcTransaction that = (EmcTransaction) o;
//
//        if (tx_id != null ? !tx_id.equals(that.tx_id) : that.tx_id != null) return false;
//        return block != null ? block.equals(that.block) : that.block == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = tx_id != null ? tx_id.hashCode() : 0;
//        result = 31 * result + (block != null ? block.hashCode() : 0);
//        return result;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmcTransaction that = (EmcTransaction) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (tx_id != null ? !tx_id.equals(that.tx_id) : that.tx_id != null) return false;
        return block != null ? block.equals(that.block) : that.block == null;

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (tx_id != null ? tx_id.hashCode() : 0);
        result = 31 * result + (block != null ? block.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EmcTransaction{" +
                "date='" + date + '\'' +
                ", address='" + address + '\'' +
                ", category='" + category + '\'' +
                ", amount='" + amount + '\'' +
                ", fee='" + fee + '\'' +
                ", tx_id='" + tx_id + '\'' +
                ", block='" + block + '\'' +
                '}' + "\n";
    }


    @Override
    public int compareTo(@NonNull Object o) {
        String date = ((EmcTransaction) o).getDate();

        return this.date.compareTo(date);
    }
}
