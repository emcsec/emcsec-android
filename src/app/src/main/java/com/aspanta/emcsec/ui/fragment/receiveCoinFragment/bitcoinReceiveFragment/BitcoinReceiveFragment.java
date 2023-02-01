package com.aspanta.emcsec.ui.fragment.receiveCoinFragment.bitcoinReceiveFragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.presenter.bitcionReceivePresenter.BitcionReceivePresenter;
import com.aspanta.emcsec.presenter.bitcionReceivePresenter.IBitcionReceivePresenter;
import com.aspanta.emcsec.tools.Config;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.bitcoinOperationsFragment.BitcoinOperationsFragment;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.adapter.BitcoinAdapterForSpinner;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;

public class BitcoinReceiveFragment extends Fragment implements IBaseFragment, IBitcoinReceiveFragment, AdapterView.OnItemSelectedListener {

    private ImageView mIvQrCode, mIvCopy, mIvSend;
    private Spinner mSPListAddresses;
    private EditText mEtAmount;
    private IBitcionReceivePresenter mPresenter;
    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc;
    private static String sAddress;
    private static String sAmount;

    public static BitcoinReceiveFragment newInstance(String address, String amount) {
        sAddress = address;
        sAmount = amount;
        return new BitcoinReceiveFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bitcoin_receive, container, false);
        init(v);

        String currentCurrency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceBtc.setText(SPHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
        String wholeRowBalanceBtc = "<b>~" + SPHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> BTC = <b>" + SPHelper.getInstance().getStringValue(BTC_EXCHANGE_RATE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceBtc.setText(Html.fromHtml(wholeRowBalanceBtc));

        mPresenter = new BitcionReceivePresenter(getContext(), this);

        mEtAmount.setText(sAmount);

        BitcoinAdapterForSpinner dataAdapter =
                new BitcoinAdapterForSpinner(getContext(),
                        R.layout.item_spinner, mPresenter.getListAddressesForSpinner());

        mSPListAddresses.setAdapter(dataAdapter);
        BtcAddress btcAddress;
        for (BtcAddress btcAddress1 : mPresenter.getListAddressesForSpinner()) {
            if (btcAddress1.getAddress().equals(sAddress)) {
                btcAddress = btcAddress1;
                mSPListAddresses.setSelection(dataAdapter.getPosition(btcAddress));
                break;
            }
        }

        mSPListAddresses.setOnItemSelectedListener(this);

        mIvCopy.setOnClickListener(view -> mPresenter.copyToBuffer(mSPListAddresses.getSelectedItemPosition()));

        mIvSend.setOnClickListener(view -> {
            Config.backFromQrOrSharing = true;
            mPresenter.send(mSPListAddresses.getSelectedItemPosition());
        });


        mPresenter.validateField(mEtAmount, mSPListAddresses);

        mIvQrCode.setScaleType(ImageView.ScaleType.FIT_XY);
        mIvQrCode.setAdjustViewBounds(true);

        return v;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mPresenter.generateQR(i, mEtAmount.getText().toString());

    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void setImage(Bitmap qr) {
        mIvQrCode.setImageBitmap(qr);
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).navigatorBackPressed(BitcoinOperationsFragment.newInstance());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public String getCurrentTag() {
        return getClass().getName();
    }

    private void init(View v) {
        mSPListAddresses = v.findViewById(R.id.sp_address_receive_btc);
        mIvQrCode = v.findViewById(R.id.iv_qr_receive_btc);
        mIvCopy = v.findViewById(R.id.iv_copy_receive_btc);
        mIvSend = v.findViewById(R.id.iv_send_receive_btc);
        mEtAmount = v.findViewById(R.id.et_amount_receive_btc);

        mTvBalanceBtc = v.findViewById(R.id.tv_balance_btc);
        mTvWholeRowBalanceBtc = v.findViewById(R.id.tv_whole_row_btc);
    }
}