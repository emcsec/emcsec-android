package com.aspanta.emcsec.ui.fragment.historyFragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.aspanta.emcsec.ui.fragment.historyFragment.bitcoinHistoryFragment.BitcoinHistoryFragmentB;
import com.aspanta.emcsec.ui.fragment.historyFragment.emercoinHistoryFragment.EmercoinHistoryFragmentB;


public class ViewPagerAdapterHistory extends FragmentStatePagerAdapter {

    public ViewPagerAdapterHistory(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return EmercoinHistoryFragmentB.newInstance();
            case 1:
                return BitcoinHistoryFragmentB.newInstance();
            default:
                break;
    }
        return EmercoinHistoryFragmentB.newInstance();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
