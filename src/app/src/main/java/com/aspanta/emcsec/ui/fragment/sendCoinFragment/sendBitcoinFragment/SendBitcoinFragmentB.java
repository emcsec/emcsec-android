package com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendBitcoinFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.tools.Config;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.activities.QrReaderActivity;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.AddressBookBitcoinFragment;
import com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment.EnterAnAddressBitcoinFragment;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;

public class SendBitcoinFragmentB extends Fragment {

    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc;
    private RelativeLayout mRlQrCodeBtc, mRlListOfRecipients, mRlSendAnAddressBtc;

    public static SendBitcoinFragmentB newInstance() {
        return new SendBitcoinFragmentB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_bitcoin_b, container, false);
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

        mRlQrCodeBtc.setOnClickListener(view -> {
            Config.backFromQrOrSharing = true;
            getActivity().startActivityForResult(new Intent(getActivity(), QrReaderActivity.class), 2);
        });

        mRlSendAnAddressBtc.setOnClickListener(view -> {
            ((MainActivity) getActivity())
                    .navigator(EnterAnAddressBitcoinFragment.newInstance(null, null, ""),
                            EnterAnAddressBitcoinFragment.newInstance(null, null, "").getCurrentTag());
        });

        mRlListOfRecipients.setOnClickListener(c -> {
            ((MainActivity) getActivity()).navigator(AddressBookBitcoinFragment.newInstance(""),
                    AddressBookBitcoinFragment.newInstance("").getCurrentTag());
        });

        return v;
    }

    public void init(View view) {
        mRlQrCodeBtc = view.findViewById(R.id.rl_send_coins_dashboard_bitcoin);
        mRlListOfRecipients = view.findViewById(R.id.rl_list_of_recipients_bitcoin);
        mRlSendAnAddressBtc = view.findViewById(R.id.rl_my_send_btc_frag_enter_an_address);

        mTvBalanceBtc = view.findViewById(R.id.tv_balance_btc);
        mTvWholeRowBalanceBtc = view.findViewById(R.id.tv_whole_row_btc);
    }
}