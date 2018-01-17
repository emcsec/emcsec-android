package com.aspanta.emcsec.tools;

import com.matthewmitchell.peercoinj.core.NetworkParameters;
import com.matthewmitchell.peercoinj.params.Networks;

public class EmercoinNetworkPeerCoin extends NetworkParameters {

    private static EmercoinNetworkPeerCoin instance;

    EmercoinNetworkPeerCoin() {
        addressHeader = 0x21;
        p2shHeader = 5;
        dumpedPrivateKeyHeader = 183;
        packetMagic = 0xe6e8e9e5;
        acceptableAddressCodes = new int[]{addressHeader, p2shHeader};
    }

    public static synchronized NetworkParameters get() {
        if (instance == null) {
            instance = new EmercoinNetworkPeerCoin();
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
