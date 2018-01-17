/*
 * Copyright 2012 Matt Corallo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aspanta.emcsec.tools;

import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.matthewmitchell.peercoinj.core.Coin;
import com.matthewmitchell.peercoinj.core.Sha256Hash;
import com.matthewmitchell.peercoinj.script.Script;

import java.util.Locale;

/**
 * A UTXOEmercoin message contains the information necessary to check a spending transaction.
 * It avoids having to store the entire parentTransaction just to get the hash and index.
 * Useful when working with free standing outputs.
 */
public class UTXOEmercoin implements Comparable {

    private int uid;

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
    public UTXOEmercoin(Sha256Hash hash,
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * The value which this Transaction output holds.
     */
    public Coin getValue() {
        return value;
    }

    public void setValue(Coin value) {
        this.value = value;
    }

    /**
     * The Script object which you can use to get address, script bytes or script type.
     */
    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    /**
     * The hash of the transaction which holds this output.
     */
    public Sha256Hash getHash() {
        return hash;
    }

    public void setHash(Sha256Hash hash) {
        this.hash = hash;
    }

    /**
     * The index of this output in the transaction which holds it.
     */
    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    /**
     * Gets the height of the block that created this output.
     */
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the flag of whether this was created by a coinbase tx.
     */
    public boolean isCoinbase() {
        return coinbase;
    }

    public void setCoinbase(boolean coinbase) {
        this.coinbase = coinbase;
    }

    /**
     * The address of this output, can be the empty string if none was provided at construction time or was deserialized
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Stored TxOut of %s (%s:%d)", value.toFriendlyString(), hash, index);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIndex(), getHash(), getHeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UTXOEmercoin other = (UTXOEmercoin) o;
        return getIndex() == other.getIndex() && getHash().equals(other.getHash());
    }


    @Override
    public int compareTo(@NonNull Object o) {
        int height = ((UTXOEmercoin) o).getHeight();

        return Integer.compare(this.height, height);
    }
}
