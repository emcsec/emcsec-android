package com.aspanta.emcsec.ui.fragment.historyFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspanta.emcsec.R;
import com.aspanta.emcsec.ui.fragment.IBaseFragment;
import com.aspanta.emcsec.ui.fragment.historyFragment.adapter.ViewPagerAdapterHistory;


public class HistoryFragment extends Fragment implements IBaseFragment {

    final String TAG = this.getClass().getName();
    private ViewPager viewPager;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        viewPager = v.findViewById(R.id.viewpagerHistoryFragment);
        setupViewPager(viewPager);
        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapterHistory adapter =
                new ViewPagerAdapterHistory(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
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
