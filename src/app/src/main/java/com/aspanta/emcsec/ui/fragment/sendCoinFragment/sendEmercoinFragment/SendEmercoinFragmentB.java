package com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendEmercoinFragment;

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
import com.aspanta.emcsec.ui.fragment.addressBookFragment.AddressBookEmercoinFragment;
import com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment.EnterAnAddressEmercoinFragment;

import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_COURSE_KEY;

public class SendEmercoinFragmentB extends Fragment {

    final String TAG = this.getClass().getName();

    private TextView mTvBalanceEmc, mTvWholeRowBalanceEmc;
    private RelativeLayout mRlQrCodeEmc, mRlListOfRecipients, mRlSendAnAddressEmc;

    public static SendEmercoinFragmentB newInstance() {
        SendEmercoinFragmentB fragment = new SendEmercoinFragmentB();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_emercoin_b, container, false);
        init(v);

        String currentCurrency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        if (currentCurrency.equals("RUB")) {
            currentCurrency = "RUR";
        }

        mTvBalanceEmc.setText(SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_KEY) + " EMC");
        String wholeRowBalanceEmc = "<b>~" + SharedPreferencesHelper.getInstance().getStringValue(EMC_BALANCE_IN_USD_KEY) + " </b>" +
                currentCurrency + " (" + "<b>1 </b> EMC = <b>" + SharedPreferencesHelper.getInstance().getStringValue(EMC_COURSE_KEY) + " </b>" +
                currentCurrency + ")";

        mTvWholeRowBalanceEmc.setText(Html.fromHtml(wholeRowBalanceEmc));

        mRlListOfRecipients.setOnClickListener(c -> {
            ((MainActivity) getActivity()).navigator(AddressBookEmercoinFragment.newInstance(""),
                    AddressBookEmercoinFragment.newInstance("").getCurrentTag());
        });

        mRlSendAnAddressEmc.setOnClickListener(c -> {
            ((MainActivity) getActivity()).navigator(EnterAnAddressEmercoinFragment.newInstance(null, null, ""),
                    EnterAnAddressEmercoinFragment.newInstance(null, null, "").getCurrentTag());
        });

        mRlQrCodeEmc.setOnClickListener(c -> {
            Config.backFromQrOrSharing = true;
            getActivity().startActivityForResult(new Intent(getActivity(), QrReaderActivity.class), 2);
        });

        return v;
    }

    public void init(View view) {
        mRlQrCodeEmc = view.findViewById(R.id.rl_send_QR_Code_emercoin);
        mRlSendAnAddressEmc = view.findViewById(R.id.rl_send_emercoin_frag_enter_an_address);
        mRlListOfRecipients = view.findViewById(R.id.rl_list_of_recipients_emercoin);

        mTvBalanceEmc = view.findViewById(R.id.tv_balance_emc);
        mTvWholeRowBalanceEmc = view.findViewById(R.id.tv_whole_row_emc);
    }

    public String getCurrentTag() {
        return TAG;
    }

}