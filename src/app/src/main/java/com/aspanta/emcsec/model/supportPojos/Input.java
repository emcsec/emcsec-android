package com.aspanta.emcsec.model.supportPojos;


public class Input {

    String prev_tx_hash;
    long indexOutputFromPrevTx;

    public String getPrev_tx_hash() {
        return prev_tx_hash;
    }

    public void setPrev_tx_hash(String prev_tx_hash) {
        this.prev_tx_hash = prev_tx_hash;
    }

    public long getIndexOutputFromPrevTx() {
        return indexOutputFromPrevTx;
    }

    public void setIndexOutputFromPrevTx(long indexOutputFromPrevTx) {
        this.indexOutputFromPrevTx = indexOutputFromPrevTx;
    }

    public Input(String prev_tx_hash, long indexOutputFromPrevTx) {
        this.prev_tx_hash = prev_tx_hash;
        this.indexOutputFromPrevTx = indexOutputFromPrevTx;
    }

    @Override
    public String toString() {
        return "Input{" +
                "prev_tx_hash='" + prev_tx_hash + '\'' +
                ", indexOutputFromPrevTx=" + indexOutputFromPrevTx +
                '}' + "\n";
    }
}
