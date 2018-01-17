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
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.tools.Config;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.activities.QrReaderActivity;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.AddressBookBitcoinFragment;
import com.aspanta.emcsec.ui.fragment.bitcoinOperationsFragment.BitcoinOperationsFragment;
import com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment.EnterAnAddressBitcoinFragment;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;


public class SendBitcoinFragment extends Fragment implements IBaseFragment {

    final String TAG = this.getClass().getName();

    private TextView mTvBalanceBtc, mTvWholeRowBalanceBtc;
    private RelativeLayout mRlQrCodeBtc, mRlListOfRecipients, mRlSendAnAddressBtc;

    public static SendBitcoinFragment newInstance() {
        return new SendBitcoinFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_bitcoin, container, false);
        init(v);

        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceBtc.setText(SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY) + " BTC");
        String wholeRowBalanceBtc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> BTC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceBtc.setText(Html.fromHtml(wholeRowBalanceBtc));

        mRlQrCodeBtc.setOnClickListener(view -> {
            Config.backFromQrOrSharing = true;
            getActivity().startActivityForResult(new Intent(getActivity(), QrReaderActivity.class), 2);
        });

        mRlSendAnAddressBtc.setOnClickListener(c -> {
            ((MainActivity) getActivity())
                    .navigator(EnterAnAddressBitcoinFragment.newInstance(null, null, "branch"),
                            EnterAnAddressBitcoinFragment.newInstance(null, null, "branch").getCurrentTag());
        });

        mRlListOfRecipients.setOnClickListener(c -> {
            ((MainActivity) getActivity()).navigator(AddressBookBitcoinFragment.newInstance("branch"),
                    AddressBookBitcoinFragment.newInstance("branch").getCurrentTag());
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

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).navigatorBackPressed(BitcoinOperationsFragment.newInstance());
    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }
}