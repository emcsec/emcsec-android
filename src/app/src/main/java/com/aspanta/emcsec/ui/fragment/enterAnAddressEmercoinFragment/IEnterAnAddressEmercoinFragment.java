package com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment;


import com.aspanta.emcsec.ui.fragment.IBaseFragment;

public interface IEnterAnAddressEmercoinFragment extends IBaseFragment {
    void showPleaseWaitDialog();

    void hidePleaseWaitDialog();

    void showSuccessDialog();

    void setBalanceUsd(String s);

    void setBalance(String balance);

    void setDownloadProgress(int count);

    void setTotalProgress(int count);
}