package com.aspanta.emcsec.ui.fragment.historyFragment.bitcoinHistoryFragment;

import com.aspanta.emcsec.db.room.historyPojos.BtcTransaction;

import java.util.List;

public interface IBitcoinHistoryFragment {
    void setBtcTransactionsList(List<BtcTransaction> btcTransactionsList);

    void showPleaseWaitDialog();

    void hidePleaseWaitDialog();

    void setDownloadProgress(int count);

    void setTotalProgress(int count);
}
