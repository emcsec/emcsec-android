package com.aspanta.emcsec.ui.fragment.dashboardFragment;


import com.aspanta.emcsec.ui.fragment.IBaseFragment;

public interface IDashboardFragment extends IBaseFragment {
    void setBitcoinBalance(String mBalance);

    void showErrorDialog(String mDetail);

    void showProgressBar(boolean show);

    void showPleaseWaitDialog();

    void hidePleaseWaitDialog();

    void setEmercoinBalance(String mBalance);

    void setBitcoinCourse(String bitcoinCourse);

    void setEmercoinCourse(String emercoinCourse);

    void setBitcoinBalanceUsd(String mStrBitcoinBalance);

    void setEmercoinBalanceUsd(String mStrEmercoinBalance);

    void setDownloadProgress(int count);

    void setTotalProgress(int count);
}

