package com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.tools.EmercoinNetworkPeerCoin;
import com.aspanta.emcsec.tools.MnemonicCodeCustom;
import com.aspanta.emcsec.ui.activities.MainActivity;

import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.spongycastle.util.encoders.Hex;

import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_BTC;
import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_EMC;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.INTENT_LIST_KEY;
import static com.aspanta.emcsec.tools.Config.SEED;
import static com.aspanta.emcsec.tools.Config.SEEKBAR_POSITION_KEY;
import static com.aspanta.emcsec.tools.Config.SEEKBAR_VALUE_KEY;
import static org.bitcoinj.core.Utils.HEX;


public class SeedRestoringSuccessfullyActivity extends AppCompatActivity {

    public static final String TAG = SeedRestoringSuccessfullyActivity.class.getSimpleName();

    private DeterministicKey child0; // key path m/0
    private DeterministicKey child1; // key path m/1
    private byte[] seed;
    private NetworkParameters btcParams;
    private com.matthewmitchell.peercoinj.core.NetworkParameters emcParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_restoring_successfilly);

        btcParams = MainNetParams.get();
        emcParams = EmercoinNetworkPeerCoin.get();

        Button mBtnDone = findViewById(R.id.btn_done);

        findViewById(R.id.btn_done).setOnClickListener(c -> {
            //don't swap methods. they should be in a such sequence
            mBtnDone.setEnabled(false);
            SPHelper.getInstance().putStringValue(CURRENT_CURRENCY, "USD");
            SPHelper.getInstance().putIntValue(SEEKBAR_POSITION_KEY, 1);
            SPHelper.getInstance().putStringValue(SEEKBAR_VALUE_KEY, "0.00148372");
            getAndStoreSeed();
            generateDeterministicKeysParents();
            App.getDbInstance().btcTransactionDao().deleteAll();
            App.getDbInstance().emcTransactionDao().deleteAll();
            startActivity(new Intent(this, MainActivity.class).putExtra("from", "seed"));
            finishAffinity();
        });
    }

    public void getAndStoreSeed() {
        Log.d(TAG, "getAndStoreSeed()");
        seed = MnemonicCodeCustom.toSeed(getIntent().getStringArrayListExtra(INTENT_LIST_KEY), "");
        String strSeed = HEX.encode(seed);
        SPHelper.getInstance().putStringValue(SEED, strSeed);
    }

    public void generateAndStoreBtcAddressesForChange(NetworkParameters networkParameters) {

        App.getDbInstance().btcAddressForChangeDao().deleteAll();

        for (int i = 0; i <= 4; i++) {
            DeterministicKey child = HDKeyDerivation.deriveChildKey(child1, i);
            byte[] child1hash160 = child.getIdentifier();

            LegacyAddress a = LegacyAddress.fromPubKeyHash(networkParameters, child1hash160);

            if (i == 0) {
                SPHelper.getInstance().putStringValue(CHANGE_ADDRESS_BTC, a.toString());
            }
            SPHelper.getInstance().putStringValue(a.toString(), child.getPrivateKeyAsHex());
            BtcAddressForChange btcAddress = new BtcAddressForChange(a.toString());
            App.getDbInstance().btcAddressForChangeDao().insertAll(btcAddress);
        }
    }

    public void generateAndStoreBtcAddresses(NetworkParameters networkParameters) {

        App.getDbInstance().btcAddressDao().deleteAll();

        for (int i = 0; i <= 19; i++) {
            DeterministicKey child = HDKeyDerivation.deriveChildKey(child0, i);
            byte[] child1hash160 = child.getIdentifier();

            LegacyAddress a = LegacyAddress.fromPubKeyHash(networkParameters, child1hash160);
            Log.d(TAG, "btc address: " + a.toString());

            SPHelper.getInstance().putStringValue(a.toString(), child.getPrivateKeyAsHex());
            BtcAddress btcAddress = new BtcAddress("", a.toString());
            App.getDbInstance().btcAddressDao().insertAll(btcAddress);
        }
    }

    public void generateAndStoreEmcAddressesForChange(com.matthewmitchell.peercoinj.core.NetworkParameters networkParameters) {

        App.getDbInstance().emcAddressForChangeDao().deleteAll();

        for (int i = 0; i <= 4; i++) {
            DeterministicKey child = HDKeyDerivation.deriveChildKey(child1, i);
            byte[] child1hash160 = child.getIdentifier();
            com.matthewmitchell.peercoinj.core.Address a = new com.matthewmitchell.peercoinj.core.Address(networkParameters, child1hash160);
            if (i == 0) {
                SPHelper.getInstance().putStringValue(CHANGE_ADDRESS_EMC, a.toString());
            }
            SPHelper.getInstance().putStringValue(a.toString(), child.getPrivateKeyAsHex());
            EmcAddressForChange emcAddress = new EmcAddressForChange(a.toString());
            App.getDbInstance().emcAddressForChangeDao().insertAll(emcAddress);
        }
    }

    public void generateAndStoreEmcAddresses(com.matthewmitchell.peercoinj.core.NetworkParameters networkParameters) {

        App.getDbInstance().emcAddressDao().deleteAll();

        for (int i = 0; i <= 19; i++) {
            DeterministicKey child = HDKeyDerivation.deriveChildKey(child0, i);
            byte[] child1hash160 = child.getIdentifier();

            Log.d(TAG, "generateAndStoreEmcAddresses: " + i + " " + Hex.toHexString(child1hash160));

            com.matthewmitchell.peercoinj.core.Address a = new com.matthewmitchell.peercoinj.core.Address(networkParameters, child1hash160);

            SPHelper.getInstance().putStringValue(a.toString(), child.getPrivateKeyAsHex());
            EmcAddress emcAddress = new EmcAddress("", a.toString());
            App.getDbInstance().emcAddressDao().insertAll(emcAddress);
        }
    }

    private void generateDeterministicKeysParents() {
        DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(seed);
        child0 = HDKeyDerivation.deriveChildKey(deterministicKey, 0);
        child1 = HDKeyDerivation.deriveChildKey(deterministicKey, 1);
        generateAndStoreBtcAddresses(btcParams);
        generateAndStoreBtcAddressesForChange(btcParams);
        generateAndStoreEmcAddresses(emcParams);
        generateAndStoreEmcAddressesForChange(emcParams);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}

