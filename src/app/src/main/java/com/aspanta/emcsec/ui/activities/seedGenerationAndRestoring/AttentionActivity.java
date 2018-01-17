package com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.tools.MnemonicCodeCustom;

import org.bitcoinj.crypto.MnemonicException;

import java.security.SecureRandom;
import java.util.ArrayList;

import static com.aspanta.emcsec.tools.Config.INTENT_LIST_KEY;

public class AttentionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        findViewById(R.id.btn_start).setOnClickListener(c -> {
                    Intent intent = new Intent(this, MnemonicActivity.class);
                    intent.putStringArrayListExtra(INTENT_LIST_KEY, generateMnemonic());
                    startActivity(intent);
                }
        );
    }

    private ArrayList<String> generateMnemonic() {
        ArrayList<String> mWordList = new ArrayList<>();
//        for (int i = 0; i < 12; i++) {
//            mWordList.add(i, "a");
//        }
        byte bytes[] = new byte[16];
        new SecureRandom().nextBytes(bytes);

        try {
            mWordList = (ArrayList<String>) MnemonicCodeCustom.INSTANCE.toMnemonic(bytes);
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        }
        return mWordList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}