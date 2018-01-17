package com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.EmcAddress;

import org.bitcoinj.crypto.DeterministicKey;

import java.util.List;

import static com.aspanta.emcsec.tools.Config.SEED;


public class AddressesBtcEmcActivity extends AppCompatActivity {

    private DeterministicKey child0; // key path m/0

    private TextView mTvBtc;
    private TextView mTvEmc;
    private Button mBtnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses_btc_emc);

        init();
        loadBtcAddresses();
        loadEmcAddresses();
        mBtnLogout.setOnClickListener(c -> {
            SharedPreferencesHelper.getInstance().putStringValue(SEED, "");
            startActivity(new Intent(this, GenerateRestoreSeedActivity.class));
        });
    }

    public void init() {
        mTvBtc = findViewById(R.id.tv_btc);
        mTvEmc = findViewById(R.id.tv_emc);
        mBtnLogout = findViewById(R.id.btn_logout);
    }

    private void loadBtcAddresses() {
        new AsyncTask<Void, Void, List<BtcAddress>>() {
            @Override
            protected List doInBackground(Void... params) {
                return App.getDbInstance().btcAddressDao().getAll();
            }

            @Override
            protected void onPostExecute(List<BtcAddress> addresses) {

                String btcAddresses = "";

                for (int i = 0; i < addresses.size(); i++) {
                    btcAddresses += addresses.get(i).getAddress() + "\n";
                }
                mTvBtc.setText(btcAddresses);
            }
        }.execute();
    }

    private void loadEmcAddresses() {
        new AsyncTask<Void, Void, List<EmcAddress>>() {
            @Override
            protected List doInBackground(Void... params) {
                return App.getDbInstance().emcAddressDao().getAll();
            }

            @Override
            protected void onPostExecute(List<EmcAddress> addresses) {

                String emcAddresses = "";

                for (int i = 0; i < addresses.size(); i++) {
                    emcAddresses += addresses.get(i).getAddress() + "\n";
                }
                mTvEmc.setText(emcAddresses);
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
