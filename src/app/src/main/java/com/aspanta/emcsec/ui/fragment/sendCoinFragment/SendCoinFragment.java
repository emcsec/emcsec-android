package com.aspanta.emcsec.ui.fragment.sendCoinFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.adapter.ViewPagerAdapterSendCoin;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendBitcoinFragment.SendBitcoinFragmentB;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.sendEmercoinFragment.SendEmercoinFragmentB;

import static com.aspanta.emcsec.tools.Config.ARG_PARAM_VIEW_PAGER_PAGE;

public class SendCoinFragment extends Fragment implements ISendCoinFragment {

    final String TAG = this.getClass().getName();
    private ViewPager viewPager;

    private int mParamViewPager;

    public static SendCoinFragment newInstance(int param1) {
        SendCoinFragment fragment = new SendCoinFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_VIEW_PAGER_PAGE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamViewPager = getArguments().getInt(ARG_PARAM_VIEW_PAGER_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_coin, container, false);
        viewPager = v.findViewById(R.id.viewpagerSendsFragment);
        setupViewPager(viewPager);
        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapterSendCoin adapter =
                new ViewPagerAdapterSendCoin(getActivity().getSupportFragmentManager());
        adapter.addFragment(SendEmercoinFragmentB.newInstance());
        adapter.addFragment(SendBitcoinFragmentB.newInstance());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mParamViewPager);
    }

    @Override
    public void onBackPressed() {

//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.navigatorBackPressed(BitcoinOperationsHistoryFragment.newInstance());

    }

    @Override
    public String getCurrentTag() {
        return TAG;
    }
}
