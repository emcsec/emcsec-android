package com.aspanta.emcsec.presenter.bitcionReceivePresenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.bitcoinReceiveFragment.IBitcoinReceiveFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;

import static com.aspanta.emcsec.tools.Config.REGEX_AMOUNT;
import static com.aspanta.emcsec.tools.Config.REGEX_WHOLE_AMOUNT;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;

public class BitcionReceivePresenter implements IBitcionReceivePresenter {

    private IBitcoinReceiveFragment mFragment;
    private MultiFormatWriter mMultiFormatWriter;
    private List<BtcAddress> mListAddresses;
    private Disposable mAmountDisposable;
    private Context mContext;

    public BitcionReceivePresenter(Context context, IBitcoinReceiveFragment fragment) {
        mContext = context;
        mFragment = fragment;
        mMultiFormatWriter = new MultiFormatWriter();
        mListAddresses = App.getDbInstance().btcAddressDao().getAll();
    }

    @Override
    public List<BtcAddress> getListAddressesForSpinner() {
        return mListAddresses;
    }

    @Override
    public void copyToBuffer(int pos) {

        ClipboardManager clipboard = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("", mListAddresses.get(pos).getAddress());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, mContext.getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void send(int pos) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mListAddresses.get(pos).getAddress());
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }

    @Override
    public void generateQR(int pos, String amount) {

        if (amount.contains(".")) {
            Double d = Double.parseDouble(amount);
            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(340);
            amount = df.format(d);
        }

        String textToQr = "bitcoin:" + mListAddresses.get(pos).getAddress();

        if (!amount.isEmpty()) {
            textToQr += "?amount=" + amount;
        }

        try {
            BitMatrix bitMatrix = mMultiFormatWriter.encode(textToQr, BarcodeFormat.QR_CODE, 208, 208);
            Bitmap bitmap = new BarcodeEncoder().createBitmap(bitMatrix);
            mFragment.setImage(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unsubscribe() {
        if (mAmountDisposable != null) {
            if (!mAmountDisposable.isDisposed())
                mAmountDisposable.dispose();
        }
    }

    @Override
    public void validateField(EditText mEtAmount, Spinner mSPListAddresses) {

        mAmountDisposable = textChanges(mEtAmount)
                .map(inputText -> amountValidation(inputText.toString()))
                .subscribe(isValid -> {
                    if (isValid) {
                        generateQR(mSPListAddresses.getSelectedItemPosition(), mEtAmount.getText().toString());
                        setDefaultUnderline(mEtAmount);
                    } else {
                        if (mEtAmount.getText().toString().isEmpty()) {
                            generateQR(mSPListAddresses.getSelectedItemPosition(), mEtAmount.getText().toString());
                            setDefaultUnderline(mEtAmount);
                        } else {
                            setErrorUnderline(mEtAmount);
                        }
                    }

                });
    }

    private boolean amountValidation(String amount) {

        if (amount.matches(REGEX_WHOLE_AMOUNT)) {
            return true;
        }

        if (amount.matches(REGEX_AMOUNT)) {
            String[] p = amount.split("\\.");
            if (p[0].length() < 10 && p[1].length() < 9) {
                if (Double.valueOf(amount) > 0.0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setErrorUnderline(EditText editText) {

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//
//            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.red));
//            editText.setBackgroundTintList(colorStateList);
//            editText.getBackground().mutate().setColorFilter
//                    (ContextCompat.getColor(mContext, R.color.red), PorterDuff.Mode.SRC_ATOP);
//        } else {
//            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.red));
//            ViewCompat.setBackgroundTintList(editText, colorStateList);
//        }
        editText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.edit_text_error_bg));
    }

    private void setDefaultUnderline(EditText editText) {
        editText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.edit_text_default_bg));
    }
}
