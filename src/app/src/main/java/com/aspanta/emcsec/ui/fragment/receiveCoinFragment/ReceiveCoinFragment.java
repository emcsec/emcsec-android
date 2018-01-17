package com.aspanta.emcsec.ui.fragment.receiveCoinFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.adapter.ViewPagerAdapterReceiveCoin;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.bitcoinReceiveFragment.BitcoinReceiveFragmentB;
import com.aspanta.emcsec.ui.fragment.receiveCoinFragment.emercoinReceiveFragment.EmercoinReceiveFragmentB;

import static com.aspanta.emcsec.tools.Config.ARG_PARAM_VIEW_PAGER_PAGE;


public class ReceiveCoinFragment extends Fragment implements IReceiveCoinFragment {

    private ViewPager mViewPager;
    private int mParamViewPager;

    public static ReceiveCoinFragment newInstance(int param1) {
        ReceiveCoinFragment fragment = new ReceiveCoinFragment();
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
        View v = inflater.inflate(R.layout.fragment_receive_coin, container, false);
        mViewPager = v.findViewById(R.id.vp_receive_coin);
        setupViewPager(mViewPager);
        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapterReceiveCoin adapter =
                new ViewPagerAdapterReceiveCoin(getActivity().getSupportFragmentManager());
        adapter.addFragment(EmercoinReceiveFragmentB.newInstance());
        adapter.addFragment(BitcoinReceiveFragmentB.newInstance());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mParamViewPager);
    }

    @Override
    public void onBackPressed() {




    }

    @Override
    public String getCurrentTag() {
        return this.getClass().getName();
    }
}