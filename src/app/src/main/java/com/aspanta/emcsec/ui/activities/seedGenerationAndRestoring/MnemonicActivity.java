package com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.aspanta.emcsec.R;

import java.util.ArrayList;

import static com.aspanta.emcsec.tools.Config.INTENT_LIST_KEY;

public class MnemonicActivity extends AppCompatActivity {

    private Button mBtnNext;
    private TextView mTvWord;
    private TextView mTvWordCount;
    private TextView mTvStartOver;
    private int mCount;
    private ArrayList<String> mWordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic);

        mTvWord = findViewById(R.id.tv_word);
        mTvWordCount = findViewById(R.id.tv_word_of);
        mTvStartOver = findViewById(R.id.tv_start_over);
        mBtnNext = findViewById(R.id.btn_next_word);

        mCount = 0;
        mWordList = getIntent().getStringArrayListExtra(INTENT_LIST_KEY);
        setWord();

        mTvStartOver.setOnClickListener(view -> onBackPressed());
        mBtnNext.setOnClickListener(view -> nextWord());
    }


    private void nextWord() {
        if (mCount == 11) {
            Intent intent = new Intent(MnemonicActivity.this, MnemonicConfirmationActivity.class);
            intent.putStringArrayListExtra(INTENT_LIST_KEY, mWordList);
            startActivity(intent);
            finish();

        } else {
            mCount++;
            setWord();
            if (mCount == 11) mBtnNext.setText(getString(R.string.next));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setWord() {
        mTvWordCount.setText(getString(R.string.word) + (mCount + 1) + getString(R.string.of12));
        mTvWord.setText(mWordList.get(mCount));
    }
}

