package com.aspanta.emcsec.tools;

import android.support.annotation.NonNull;

import com.google.common.base.Objects;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.script.Script;

import java.util.Locale;

public class UTXOBitcoin implements Comparable {

    private Coin value;
    private Script script;
    private Sha256Hash hash;
    private long index;
    private int height;
    private boolean coinbase;
    private String address;

    /**
     * Creates a stored transaction output.
     *
     * @param hash     The hash of the containing transaction.
     * @param index    The outpoint.
     * @param value    The value available.
     * @param height   The height this output was created in.
     * @param coinbase The coinbase flag.
     */
    public UTXOBitcoin(Sha256Hash hash,
                       long index,
                       Coin value,
                       int height,
                       boolean coinbase,
                       Script script,
                       String address) {
        this.hash = hash;
        this.index = index;
        this.value = value;
        this.height = height;
        this.script = script;
        this.coinbase = coinbase;
        this.address = address;
    }

    /**
     * The value which this Transaction output holds.
     */
    public Coin getValue() {
        return value;
    }

    /**
     * The Script object which you can use to get address, script bytes or script type.
     */
    public Script getScript() {
        return script;
    }

    /**
     * The hash of the transaction which holds this output.
     */
    public Sha256Hash getHash() {
        return hash;
    }

    /**
     * The index of this output in the transaction which holds it.
     */
    public long getIndex() {
        return index;
    }

    /**
     * Gets the height of the block that created this output.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the flag of whether this was created by a coinbase tx.
     */
    public boolean isCoinbase() {
        return coinbase;
    }

    /**
     * The address of this output, can be the empty string if none was provided at construction time or was deserialized
     */
    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Stored TxOut of %s (%s:%d)", value.toFriendlyString(), hash, index);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIndex(), getHash());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UTXOBitcoin other = (UTXOBitcoin) o;
        return getIndex() == other.getIndex() && getHash().equals(other.getHash());
    }

    @Override
    public int compareTo(@NonNull Object o) {
        long value = ((UTXOBitcoin) o).getValue().longValue();

        return Long.compare(this.value.longValue(), value);
    }
}
