package com.aspanta.emcsec.ui.activities.seedGenerationAndRestoring;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.aspanta.emcsec.R;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.aspanta.emcsec.tools.Config.INTENT_LIST_KEY;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;

public class SeedRestoringActivity extends AppCompatActivity {

    private EditText mEtWord1, mEtWord2, mEtWord3, mEtWord4, mEtWord5, mEtWord6, mEtWord7, mEtWord8,
            mEtWord9, mEtWord10, mEtWord11, mEtWord12;

    private Observable<Boolean> mWord1Observable, mWord2Observable, mWord3Observable,
            mWord4Observable, mWord5Observable, mWord6Observable, mWord7Observable, mWord8Observable,
            mWord9Observable, mWord10Observable, mWord11Observable, mWord12Observable,
            mFirst6WordObservable, mLast6WordObservable;
    private Disposable mDisposable;

    private Button mBtnRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_restoring);
        init();
        validateFields();
        mBtnRestore.setOnClickListener(view -> finishRestoring());
    }


    private ArrayList<String> getMnemonicList() {
        ArrayList<String> wordList = new ArrayList<>();
        wordList.add(mEtWord1.getText().toString().trim());
        wordList.add(mEtWord2.getText().toString().trim());
        wordList.add(mEtWord3.getText().toString().trim());
        wordList.add(mEtWord4.getText().toString().trim());
        wordList.add(mEtWord5.getText().toString().trim());
        wordList.add(mEtWord6.getText().toString().trim());
        wordList.add(mEtWord7.getText().toString().trim());
        wordList.add(mEtWord8.getText().toString().trim());
        wordList.add(mEtWord9.getText().toString().trim());
        wordList.add(mEtWord10.getText().toString().trim());
        wordList.add(mEtWord11.getText().toString().trim());
        wordList.add(mEtWord12.getText().toString().trim());

        return wordList;
    }

    private void finishRestoring() {

        ArrayList<String> mnemonicList = getMnemonicList();

        try {
            MnemonicCode mnemonicCode = new MnemonicCode();
            mnemonicCode.check(mnemonicList);

            Intent intent = new Intent(this, SeedRestoringSuccessfullyActivity.class);
            intent.putStringArrayListExtra(INTENT_LIST_KEY, mnemonicList);
            startActivity(intent);
            finish();

        } catch (IOException | MnemonicException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.wrong_seed_phrase))
                    .setCancelable(false)
                    .setPositiveButton(getString (R.string.ok), (DialogInterface dialog, int which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }


    public void validateFields() {
        mWord1Observable = textChanges(mEtWord1)
                .map(inputText -> inputText.length() != 0);
        mWord2Observable = textChanges(mEtWord2)
                .map(inputText -> inputText.length() != 0);
        mWord3Observable = textChanges(mEtWord3)
                .map(inputText -> inputText.length() != 0);
        mWord4Observable = textChanges(mEtWord4)
                .map(inputText -> inputText.length() != 0);
        mWord5Observable = textChanges(mEtWord5)
                .map(inputText -> inputText.length() != 0);
        mWord6Observable = textChanges(mEtWord6)
                .map(inputText -> inputText.length() != 0);
        mWord7Observable = textChanges(mEtWord7)
                .map(inputText -> inputText.length() != 0);
        mWord8Observable = textChanges(mEtWord8)
                .map(inputText -> inputText.length() != 0);
        mWord9Observable = textChanges(mEtWord9)
                .map(inputText -> inputText.length() != 0);
        mWord10Observable = textChanges(mEtWord10)
                .map(inputText -> inputText.length() != 0);
        mWord11Observable = textChanges(mEtWord11)
                .map(inputText -> inputText.length() != 0);
        mWord12Observable = textChanges(mEtWord12)
                .map(inputText -> inputText.length() != 0);


        mFirst6WordObservable = Observable
                .combineLatest(
                        mWord1Observable, mWord2Observable, mWord3Observable, mWord4Observable,
                        mWord5Observable, mWord6Observable,
                        (mWord1Boolean, mWord2Boolean, mWord3Boolean, mWord4Boolean, mWord5Boolean,
                         mWord6Boolean) ->
                                mWord1Boolean && mWord2Boolean && mWord3Boolean && mWord4Boolean
                                        && mWord5Boolean && mWord6Boolean);

        mLast6WordObservable = Observable
                .combineLatest(
                        mWord7Observable, mWord8Observable, mWord9Observable, mWord10Observable,
                        mWord11Observable, mWord12Observable,
                        (mWord7Boolean, mWord8Boolean, mWord9Boolean, mWord10Boolean, mWord11Boolean,
                         mWord12Boolean) ->
                                mWord7Boolean && mWord8Boolean && mWord9Boolean && mWord10Boolean
                                        && mWord11Boolean && mWord12Boolean);


        mDisposable = Observable
                .combineLatest(
                        mFirst6WordObservable, mLast6WordObservable,
                        (firstBoolean, lastBoolean) ->
                                firstBoolean && lastBoolean)
                .subscribe(b -> mBtnRestore.setEnabled(b));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            if (mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        }
    }

    public void init() {
        mBtnRestore = findViewById(R.id.btn_next);
        mEtWord1 = findViewById(R.id.et_word_1);
        mEtWord2 = findViewById(R.id.et_word_2);
        mEtWord3 = findViewById(R.id.et_word_3);
        mEtWord4 = findViewById(R.id.et_word_4);
        mEtWord5 = findViewById(R.id.et_word_5);
        mEtWord6 = findViewById(R.id.et_word_6);
        mEtWord7 = findViewById(R.id.et_word_7);
        mEtWord8 = findViewById(R.id.et_word_8);
        mEtWord9 = findViewById(R.id.et_word_9);
        mEtWord10 = findViewById(R.id.et_word_10);
        mEtWord11 = findViewById(R.id.et_word_11);
        mEtWord12 = findViewById(R.id.et_word_12);

        mEtWord1.addTextChangedListener(mEtWord1Watcher);
        mEtWord2.addTextChangedListener(mEtWord2Watcher);
        mEtWord3.addTextChangedListener(mEtWord3Watcher);
        mEtWord4.addTextChangedListener(mEtWord4Watcher);
        mEtWord5.addTextChangedListener(mEtWord5Watcher);
        mEtWord6.addTextChangedListener(mEtWord6Watcher);
        mEtWord7.addTextChangedListener(mEtWord7Watcher);
        mEtWord8.addTextChangedListener(mEtWord8Watcher);
        mEtWord9.addTextChangedListener(mEtWord9Watcher);
        mEtWord10.addTextChangedListener(mEtWord10Watcher);
        mEtWord11.addTextChangedListener(mEtWord11Watcher);
        mEtWord12.addTextChangedListener(mEtWord12Watcher);
    }

    private final TextWatcher mEtWord1Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord1.removeTextChangedListener(mEtWord1Watcher);
                mEtWord1.setText(s);
                mEtWord1.setSelection(mEtWord1.getText().length());
                mEtWord1.addTextChangedListener(mEtWord1Watcher);
            }
        }
    };

    private final TextWatcher mEtWord2Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord2.removeTextChangedListener(mEtWord2Watcher);
                mEtWord2.setText(s);
                mEtWord2.setSelection(mEtWord2.getText().length());
                mEtWord2.addTextChangedListener(mEtWord2Watcher);
            }
        }
    };

    private final TextWatcher mEtWord3Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord3.removeTextChangedListener(mEtWord3Watcher);
                mEtWord3.setText(s);
                mEtWord3.setSelection(mEtWord3.getText().length());
                mEtWord3.addTextChangedListener(mEtWord3Watcher);
            }
        }
    };

    private final TextWatcher mEtWord4Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord4.removeTextChangedListener(mEtWord4Watcher);
                mEtWord4.setText(s);
                mEtWord4.setSelection(mEtWord4.getText().length());
                mEtWord4.addTextChangedListener(mEtWord4Watcher);
            }
        }
    };

    private final TextWatcher mEtWord5Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord5.removeTextChangedListener(mEtWord5Watcher);
                mEtWord5.setText(s);
                mEtWord5.setSelection(mEtWord5.getText().length());
                mEtWord5.addTextChangedListener(mEtWord5Watcher);
            }
        }
    };

    private final TextWatcher mEtWord6Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord6.removeTextChangedListener(mEtWord6Watcher);
                mEtWord6.setText(s);
                mEtWord6.setSelection(mEtWord6.getText().length());
                mEtWord6.addTextChangedListener(mEtWord6Watcher);
            }
        }
    };

    private final TextWatcher mEtWord7Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord7.removeTextChangedListener(mEtWord7Watcher);
                mEtWord7.setText(s);
                mEtWord7.setSelection(mEtWord7.getText().length());
                mEtWord7.addTextChangedListener(mEtWord7Watcher);
            }
        }
    };

    private final TextWatcher mEtWord8Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord8.removeTextChangedListener(mEtWord8Watcher);
                mEtWord8.setText(s);
                mEtWord8.setSelection(mEtWord8.getText().length());
                mEtWord8.addTextChangedListener(mEtWord8Watcher);
            }
        }
    };

    private final TextWatcher mEtWord9Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord9.removeTextChangedListener(mEtWord9Watcher);
                mEtWord9.setText(s);
                mEtWord9.setSelection(mEtWord9.getText().length());
                mEtWord9.addTextChangedListener(mEtWord9Watcher);
            }
        }
    };

    private final TextWatcher mEtWord10Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord10.removeTextChangedListener(mEtWord10Watcher);
                mEtWord10.setText(s);
                mEtWord10.setSelection(mEtWord10.getText().length());
                mEtWord10.addTextChangedListener(mEtWord10Watcher);
            }
        }
    };

    private final TextWatcher mEtWord11Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord11.removeTextChangedListener(mEtWord11Watcher);
                mEtWord11.setText(s);
                mEtWord11.setSelection(mEtWord11.getText().length());
                mEtWord11.addTextChangedListener(mEtWord11Watcher);
            }
        }
    };

    private final TextWatcher mEtWord12Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (!s.equals(s.toLowerCase())) {
                s = s.toLowerCase();
                mEtWord12.removeTextChangedListener(mEtWord12Watcher);
                mEtWord12.setText(s);
                mEtWord12.setSelection(mEtWord12.getText().length());
                mEtWord12.addTextChangedListener(mEtWord12Watcher);
            }
        }
    };
}