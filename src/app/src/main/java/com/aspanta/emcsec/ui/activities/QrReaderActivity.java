package com.aspanta.emcsec.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.aspanta.emcsec.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static com.aspanta.emcsec.tools.Config.BITCOIN;
import static com.aspanta.emcsec.tools.Config.EMERCOIN;
import static com.aspanta.emcsec.tools.Config.EXTRAS_ADDRESS;
import static com.aspanta.emcsec.tools.Config.EXTRAS_AMOUNT;
import static com.aspanta.emcsec.tools.Config.EXTRAS_TYPE;

public class QrReaderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        int currentApiVersion = Build.VERSION.SDK_INT;
        intent = new Intent(this, MainActivity.class);
        if (currentApiVersion >= Build.VERSION_CODES.M) {
//        if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }



    @Override
    public void onResume() {
        super.onResume();

        if (scannerView == null) {
            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);
        }
        scannerView.startCamera();
        scannerView.setResultHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String myResult = result.getText();
        String address = "";
        String amount = "";
        String resultAddress = "";
        String type = "";

        try {
            // split to address and amount
            if (myResult.contains("?") & myResult.contains("&")) {
                address = myResult.substring(0, myResult.indexOf('?'));
                amount = myResult.substring(myResult.indexOf('=') + 1, myResult.indexOf('&'));

            } else if (myResult.contains("?")) {
                address = myResult.substring(0, myResult.indexOf('?'));
                amount = myResult.substring(myResult.indexOf('=') + 1);

            } else {
                address = myResult;
            }
            if (address.contains(EMERCOIN)) {
                type = EMERCOIN;
                resultAddress = address.substring(address.indexOf(':') + 1);
            } else if (address.contains(BITCOIN)) {
                type = BITCOIN;
                resultAddress = address.substring(address.indexOf(':') + 1);
            } else if (address.startsWith("E") & address.length() == 34) {
                type = EMERCOIN;
                resultAddress = address;
            } else if ((address.startsWith("1") | address.startsWith("3")) &
                    address.length() >= 27 & address.length() <= 34) {
                type = BITCOIN;
                resultAddress = address;
            } else if (resultAddress.equals("")) {
                showAlertDialog();
                return;
            }

        } catch (RuntimeException e) {
            showAlertDialog();
            return;
        }

        intent.putExtra(EXTRAS_TYPE, type);
        intent.putExtra(EXTRAS_AMOUNT, amount);
        intent.putExtra(EXTRAS_ADDRESS, resultAddress);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error))
                .setMessage(getResources().getString(R.string.qr_incorrect))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), (DialogInterface dialog, int which) -> {
                    setResult(RESULT_CANCELED, intent);
                    dialog.dismiss();
                    finish();
                })
                .create()
                .show();
    }

}