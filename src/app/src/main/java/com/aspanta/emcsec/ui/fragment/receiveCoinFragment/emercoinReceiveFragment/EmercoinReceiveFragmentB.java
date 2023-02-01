package com.aspanta.emcsec.ui.fragment.receiveCoinFragment.emercoinReceiveFragment;


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
import com.aspanta.emcsec.presenter.emercionReceivePresenter.EmercionReceivePresenter;
import com.aspanta.emcsec.presenter.emercionReceivePresenter.IEmercionReceivePresenter;
import com.aspanta.emcsec.tools.Config;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.adapter.EmercoinAdapterForSpinner;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;

public class EmercoinReceiveFragmentB extends Fragment implements IEmercoinReceiveFragment, AdapterView.OnItemSelectedListener {

    private ImageView mIvQrCode, mIvCopy, mIvSend;
    private Spinner mSPListAddresses;
    private EditText mEtAmount;
    private IEmercionReceivePresenter mPresenter;
    private TextView mTvBalanceEmc, mTvWholeRowBalanceEmc;

    public static EmercoinReceiveFragmentB newInstance() {
        return new EmercoinReceiveFragmentB();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emercoin_receive_b, container, false);
        init(v);

        String currentCurrency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceEmc.setText(SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        String wholeRowBalanceEmc = "<b>~" + SPHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        mPresenter = new EmercionReceivePresenter(getContext(), this);

        EmercoinAdapterForSpinner dataAdapter =
                new EmercoinAdapterForSpinner(getContext(),
                        R.layout.item_spinner, mPresenter.getListAddressesForSpinner());

        mSPListAddresses.setAdapter(dataAdapter);
        mSPListAddresses.setOnItemSelectedListener(this);

        mIvCopy.setOnClickListener(view -> mPresenter.copyToBuffer(mSPListAddresses.getSelectedItemPosition()));

        mIvSend.setOnClickListener(view -> {
            Config.backFromQrOrSharing = true;
            mPresenter.send(mSPListAddresses.getSelectedItemPosition());
        });

        mPresenter.validateField(mEtAmount, mSPListAddresses);

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

    public void onBackPressed() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    public String getCurrentTag() {
        return getClass().getName();
    }

    private void init(View v) {
        mSPListAddresses = v.findViewById(R.id.sp_address_receive_emc);
        mIvQrCode = v.findViewById(R.id.iv_qr_receive_emc);
        mIvCopy = v.findViewById(R.id.iv_copy_receive_emc);
        mIvSend = v.findViewById(R.id.iv_send_receive_emc);
        mEtAmount = v.findViewById(R.id.et_amount_receive_emc);

        mTvBalanceEmc = v.findViewById(R.id.tv_balance_emc);
        mTvWholeRowBalanceEmc = v.findViewById(R.id.tv_whole_row_emc);
    }
}