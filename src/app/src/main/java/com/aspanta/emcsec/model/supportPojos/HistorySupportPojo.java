package com.aspanta.emcsec.model.supportPojos;


public class HistorySupportPojo {

    private String address;

    private int height;

    private String tx_hash;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTx_hash() {
        return tx_hash;
    }

    public void setTx_hash(String tx_hash) {
        this.tx_hash = tx_hash;
    }

    public HistorySupportPojo(String address, int height, String tx_hash) {
        this.address = address;
        this.height = height;
        this.tx_hash = tx_hash;
    }

    @Override
    public String toString() {
        return "HistorySupportPojo{" +
                "address='" + address + '\'' +
                ", height='" + height + '\'' +
                ", tx_hash='" + tx_hash + '\'' +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistorySupportPojo that = (HistorySupportPojo) o;

        if (height != that.height) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return tx_hash != null ? tx_hash.equals(that.tx_hash) : that.tx_hash == null;

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + height;
        result = 31 * result + (tx_hash != null ? tx_hash.hashCode() : 0);
        return result;
    }
}
