package com.aspanta.emcsec.ui.fragment.historyFragment.emercoinHistoryFragment;


import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;

import java.util.List;

public interface IEmercoinHistoryFragment {
    void setEmcTransactionsList(List<EmcTransaction> emcTransactionsList);

    void showPleaseWaitDialog();

    void hidePleaseWaitDialog();

    void setDownloadProgress(int count);

    void setTotalProgress(int count);
}
