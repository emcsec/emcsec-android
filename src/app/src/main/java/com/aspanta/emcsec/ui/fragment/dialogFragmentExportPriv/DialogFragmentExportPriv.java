package com.aspanta.emcsec.ui.fragment.dialogFragmentExportPriv;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.tools.EmercoinNetwork;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;

import static org.bitcoinj.core.Utils.HEX;

public class DialogFragmentExportPriv extends DialogFragment {

    private static final String TAG = DialogFragmentExportPriv.class.getSimpleName();

    private ImageView qr;
    private TextView tvPriv;
    private TextView tvAddress;
    private Button btnCancel;
    private MultiFormatWriter mMultiFormatWriter;


    public static DialogFragmentExportPriv newInstance() {
        return new DialogFragmentExportPriv();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMultiFormatWriter = new MultiFormatWriter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_export_priv_btc, container, false);
        setCancelable(false);
        init(view);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        Bundle bundle = getArguments();

        assert bundle != null;
        String address = bundle.getString("address");
        String coin = bundle.getString("coin");

        tvAddress.setText(address);
        String pr = SPHelper.getInstance().getStringValue(address);
        org.bitcoinj.core.ECKey ecKey = org.bitcoinj.core.ECKey.fromPrivate(HEX.decode(pr));

        try {
            BitMatrix bitMatrix = mMultiFormatWriter.encode(ecKey.getPrivateKeyAsWiF(EmercoinNetwork.get()), BarcodeFormat.QR_CODE, 208, 208);
            Bitmap bitmap = new BarcodeEncoder().createBitmap(bitMatrix);
            qr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        btnCancel.setOnClickListener(v -> dismissAllowingStateLoss());
        tvPriv.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("", ecKey.getPrivateKeyAsWiF(EmercoinNetwork.get()));
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), getContext().getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });
        tvPriv.setText(ecKey.getPrivateKeyAsWiF(EmercoinNetwork.get()));

        switch (Objects.requireNonNull(coin)) {
            case "emc":
                btnCancel.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimary));
                break;
            case "btc":
                btnCancel.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.orange));
                break;
            default:
                btnCancel.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimary));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void init(View view) {
        qr = view.findViewById(R.id.iv_qr_priv_btc);
        tvPriv = view.findViewById(R.id.tv_priv);
        tvAddress = view.findViewById(R.id.tv_address);
        btnCancel = view.findViewById(R.id.btn_cancel);
    }
}
