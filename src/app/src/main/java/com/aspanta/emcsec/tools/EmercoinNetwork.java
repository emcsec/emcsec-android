package com.aspanta.emcsec.tools;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.Networks;

public class EmercoinNetwork extends AbstractBitcoinNetParams {

    private static EmercoinNetwork instance;

    EmercoinNetwork() {
        addressHeader = 0x21;
        p2shHeader = 5;
        dumpedPrivateKeyHeader = 128;
        packetMagic = 0xe6e8e9e5;
        bip32HeaderPub = 0x0488b21e; //The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderPriv = 0x0488ade4; //The 4 byte header that serializes in base58 to "xprv"
//        acceptableAddressCodes = new int[]{addressHeader, p2shHeader};
    }

    public static synchronized NetworkParameters get() {
        if (instance == null) {
            instance = new EmercoinNetwork();
        }
        Networks.register(instance);
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return "emcnet";
    }

    @Override
    public int getDumpedPrivateKeyHeader() {
        return dumpedPrivateKeyHeader;
    }
}