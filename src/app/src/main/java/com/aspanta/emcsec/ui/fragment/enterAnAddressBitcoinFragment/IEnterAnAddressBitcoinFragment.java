package com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment;

import com.aspanta.emcsec.ui.fragment.IBaseFragment;

public interface IEnterAnAddressBitcoinFragment extends IBaseFragment {
    void showPleaseWaitDialog();

    void hidePleaseWaitDialog();

    void showSuccessDialog();

    void setBalance(String s);

    void setBalanceUsd(String s);

    void setDownloadProgress(int count);

    void setTotalProgress(int count);
}
