package com.aspanta.emcsec.presenter.historyPresenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.db.room.historyPojos.BtcTransaction;
import com.aspanta.emcsec.model.apiTCP.TcpClientBtc;
import com.aspanta.emcsec.model.supportPojos.HistorySupportPojo;
import com.aspanta.emcsec.model.supportPojos.Input;
import com.aspanta.emcsec.ui.activities.SplashActivity;
import com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment;
import com.aspanta.emcsec.ui.fragment.historyFragment.bitcoinHistoryFragment.IBitcoinHistoryFragment;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.showAlertDialog;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;
import static org.bitcoinj.core.Utils.HEX;

public class HistoryBitcoinPresenter implements IHistoryPresenter {

    //fields for getting balance
    private String balanceBtc;
    private int countBtc;
    private int satoshiSumBtc;
    private String currency;
    private ConnectTaskBtcGetBalance mConnectTaskBtcGetBalance;

    private NetworkParameters btcParams = MainNetParams.get();
    private Context mContext;
    private TcpClientBtc mTcpClientBtc;
    private TcpClientBtc mTcpClientBtcGetBalance;
    private IBitcoinHistoryFragment mBitcoinHistoryFragment;

    private List<BtcAddress> mListBtcAddresses = App.getDbInstance().btcAddressDao().getAll();
    private List<BtcAddressForChange> mListBtcAddressesForChange = App.getDbInstance().btcAddressForChangeDao().getAll();
    private List<BtcTransaction> btcTransactionsWithoutFee;
    private List<HistorySupportPojo> mListSupportPojos;
    private List<Input> inputsList = new ArrayList<>();
    private List<String> listStringAddressesForChange;
    private Set<BtcTransaction> mBtcTransactionSet;
    private List<BtcTransaction> mBtcTransactionList;
    private List<String> listStringAddresses;
    private List<String> listStringAllAddresses;

    private int positionGetHistory = 0;
    private int positionGetHeader = 0;
    private int mPositionOfSupportPojo = 0;
    private int mPositionBtcTransaction = 0;
    private int inputsPos = 0;
    private long inputValue = 0;
    private boolean btcTxWithoutFeeAlsoAdded = false;
    private ConnectTaskBtc connectTaskBtc;
    private DashboardFragment mDashboardFragment;
    private int downloadProgress = 0;
    private int totalProgress = 25;

    private boolean doneTotalProgressTransactionGet;
    private boolean doneTotalProgressGetHeader;

    public HistoryBitcoinPresenter(Context context, IBitcoinHistoryFragment bitcoinHistoryFragment, DashboardFragment dashboardFragment) {
        mBitcoinHistoryFragment = bitcoinHistoryFragment;
        mDashboardFragment = dashboardFragment;
        mContext = context;
    }

    @Override
    public void getTransactionHistory() {

        downloadProgress = 0;
        totalProgress = 25;

        downloadProgressDashboard = 0;
        totalProgressDashboard += 25;

        doneTotalProgressTransactionGet = false;
        doneTotalProgressGetHeader = false;

        positionGetHistory = 0;

        mBtcTransactionList = new ArrayList<>();
        mListSupportPojos = new ArrayList<>();
        listStringAddressesForChange = new ArrayList<>();
        mListSupportPojos = new ArrayList<>();
        btcTransactionsWithoutFee = new ArrayList<>();

        for (BtcAddressForChange btcAddressForChange : mListBtcAddressesForChange) {
            listStringAddressesForChange.add(btcAddressForChange.getAddress());
        }

        mListSupportPojos = new ArrayList<>();
        listStringAddresses = new ArrayList<>();
        for (BtcAddress btcAddress : mListBtcAddresses) {
            listStringAddresses.add(btcAddress.getAddress());
        }

        listStringAllAddresses = new ArrayList<>();
        listStringAllAddresses.addAll(listStringAddresses);
        listStringAllAddresses.addAll(listStringAddressesForChange);

        mBtcTransactionSet = new HashSet<>();
        if (mBitcoinHistoryFragment != null) {
            mBitcoinHistoryFragment.showPleaseWaitDialog();
            mBitcoinHistoryFragment.setDownloadProgress(downloadProgress);
            mBitcoinHistoryFragment.setTotalProgress(totalProgress);
        }
        if (mDashboardFragment != null) {
            mDashboardFragment.showPleaseWaitDialogForBitcoinHistoryPresenter();
            mDashboardFragment.setDownloadProgress(downloadProgressDashboard);
            mDashboardFragment.setTotalProgress(totalProgressDashboard);
        }
        connectTaskBtc = new ConnectTaskBtc();
        connectTaskBtc.execute("");
        if (mBitcoinHistoryFragment != null) {
            getBtcBalance();
        }
    }

    private void getHistory() {

        if (positionGetHistory < 25) {
            downloadProgress++;
            downloadProgressDashboard++;
            connectTaskBtc.doProgress();

            String address = listStringAllAddresses.get(positionGetHistory);
            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 4,\"method\":\"blockchain.address.get_history\",\"params\":[\"" + address + "\"]}";
            mTcpClientBtc.sendMessage(jsonRequest);
        }
    }

    private void transactionGet() {

        if (!doneTotalProgressTransactionGet) {
            totalProgress += mListSupportPojos.size();
            totalProgressDashboard += mListSupportPojos.size();
            doneTotalProgressTransactionGet = !doneTotalProgressTransactionGet;
        }
        downloadProgress++;
        downloadProgressDashboard++;
        connectTaskBtc.doProgress();

        String tx_hash = mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash();
        String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 5,\"method\":\"blockchain.transaction.get\",\"params\":[\"" + tx_hash + "\"]}";
        mTcpClientBtc.sendMessage(jsonRequest);
    }

    private void getHeader() {

        if (!doneTotalProgressGetHeader) {
            totalProgress += mBtcTransactionList.size();
            totalProgressDashboard += mBtcTransactionList.size();
            doneTotalProgressGetHeader = !doneTotalProgressGetHeader;
        }
        downloadProgress++;
        downloadProgressDashboard++;
        connectTaskBtc.doProgress();

        String height = mBtcTransactionList.get(positionGetHeader).getBlock();
        String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 6,\"method\":\"blockchain.block.get_header\",\"params\":[\"" + height + "\"]}";
        mTcpClientBtc.sendMessage(jsonRequest);
    }

    private void transactionGetPrevious() {

        if (mPositionBtcTransaction < mBtcTransactionSet.size()) {

            inputsList = btcTransactionsWithoutFee.get(mPositionBtcTransaction).getInputList();
            totalProgress += inputsList.size();
            totalProgressDashboard += inputsList.size();
            getInputsValue();
        }
    }

    private void getInputsValue() {

        if (inputsPos < inputsList.size()) {

            downloadProgress++;
            downloadProgressDashboard++;

            connectTaskBtc.doProgress();

            String prevTxHash = inputsList.get(inputsPos).getPrev_tx_hash();
            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 7,\"method\":\"blockchain.transaction.get\",\"params\":[\"" + prevTxHash + "\"]}";
            mTcpClientBtc.sendMessage(jsonRequest);
        } else {
            inputsPos = 0;
        }
    }

    private class ConnectTaskBtc extends AsyncTask<String, String, TcpClientBtc> {

        @Override
        protected TcpClientBtc doInBackground(String... message) {

            mTcpClientBtc = new TcpClientBtc(response -> {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 3) {
                        getHistory();
                    } else if (jsonObject.getInt("id") == 4) {

                        Log.d("JSON GET HISTORY", jsonObject.toString());
                        JSONArray getHistoryJSONArray = jsonObject.getJSONArray("result");
                        Log.d("getHistoryJSONArray", getHistoryJSONArray.toString());

                        int lengthOfHistoryArray = getHistoryJSONArray.length();

                        if (lengthOfHistoryArray <= 0) {
                            positionGetHistory++;
                            getHistory();
                        } else {
                            for (int i = 0; i < lengthOfHistoryArray; i++) {

                                JSONObject jsonObject1 = getHistoryJSONArray.getJSONObject(i);

                                HistorySupportPojo historySupportPojo = new HistorySupportPojo(
                                        listStringAllAddresses.get(positionGetHistory),
                                        jsonObject1.getInt("height"),
                                        jsonObject1.getString("tx_hash"));

                                mListSupportPojos.add(historySupportPojo);
                            }
                            positionGetHistory++;
                            getHistory();
                        }

                        if (positionGetHistory == 25) {
                            Log.d("mListSupportPojos", mListSupportPojos.toString());
                            Log.d("mListSupportPojosSize", mListSupportPojos.size() + "");
                            if (mListSupportPojos.isEmpty()) {
                                publishProgress("");
                            } else {
                                transactionGet();
                            }
                        }

                    } else if (jsonObject.getInt("id") == 5) {
                        Log.d("JSON transactionGet", jsonObject.toString());

                        String error = "";
                        try {
                            error = jsonObject.getString("error");
                            publishProgress("error", error);
                        } catch (JSONException jse) {
                            Log.d("response without error", "response without error");
                        }

                        String rawTransaction = jsonObject.getString("result");
                        Log.d("rawTransaction", rawTransaction);

                        byte[] raw = HEX.decode(rawTransaction);
                        Transaction transaction = new Transaction(MainNetParams.get(), raw);

                        List<Input> inputList = new ArrayList<>();
                        for (TransactionInput transactionInput : transaction.getInputs()) {
                            inputList.add(new Input(transactionInput.getOutpoint().getHash().toString(), transactionInput.getOutpoint().getIndex()));
                        }

                        long totalOutPutAmount = 0;
                        for (TransactionOutput transactionOutput : transaction.getOutputs()) {
                            totalOutPutAmount += transactionOutput.getValue().longValue();
                        }

                        String transactionType = "";
                        Address addressFromFirstInput = new Address(btcParams,
                                Utils.sha256hash160(transaction.getInputs().get(0).getScriptSig().getPubKey()));

                        if (!listStringAddresses.contains(addressFromFirstInput.toString()) & !listStringAddressesForChange.contains(addressFromFirstInput.toString())) {
                            transactionType = "Receive";
                            Log.d("transactionType", "Receive");
                            for (TransactionOutput transactionOutput : transaction.getOutputs()) {

                                Address address = transactionOutput.getAddressFromP2PKHScript(btcParams);
                                String addressString;
                                try {
                                    addressString = address.toString();
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                    address = transactionOutput.getAddressFromP2SH(btcParams);
                                    addressString = address.toString();
                                }

                                Log.d("address in Outputs", addressString);

                                if (listStringAddresses.contains(addressString)) {
                                    long satoshis = transactionOutput.getValue().longValue();
                                    Log.d("TransactionInfo", addressString + "  " + satoshis);
                                    BtcTransaction btcTransaction = new BtcTransaction(
                                            "",
                                            addressString,
                                            transactionType,
                                            String.valueOf(satoshis),
                                            "None",
                                            mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash(),
                                            String.valueOf(mListSupportPojos.get(mPositionOfSupportPojo).getHeight()),
                                            inputList,
                                            totalOutPutAmount);

                                    Log.d("btcTransaction", btcTransaction.toString());
                                    if (!mBtcTransactionSet.contains(btcTransaction)) {
                                        mBtcTransactionSet.add(btcTransaction);
                                    }
                                }
                            }
                        } else {
                            Log.d("transactionType", "Send");
                            for (TransactionOutput transactionOutput : transaction.getOutputs()) {
                                Address addressfromOutputs = transactionOutput.getAddressFromP2PKHScript(btcParams);
                                String addressString;
                                try {
                                    addressString = addressfromOutputs.toString();
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                    addressfromOutputs = transactionOutput.getAddressFromP2SH(btcParams);
                                    addressString = addressfromOutputs.toString();
                                }
                                if (listStringAddresses.contains(addressString)) {
                                    transactionType = "Self";
                                    long satoshis = transactionOutput.getValue().longValue();
                                    BtcTransaction btcTransaction = new BtcTransaction(
                                            "",
                                            addressString,
                                            transactionType,
                                            String.valueOf(satoshis),
                                            "",
                                            mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash(),
                                            String.valueOf(mListSupportPojos.get(mPositionOfSupportPojo).getHeight()),
                                            inputList,
                                            totalOutPutAmount);
                                    if (!mBtcTransactionSet.contains(btcTransaction)) {
                                        mBtcTransactionSet.add(btcTransaction);
                                    }
                                } else {
                                    if (!listStringAddressesForChange.contains(addressString)) {
                                        transactionType = "Send";
                                        long satoshis = transactionOutput.getValue().longValue();
                                        BtcTransaction btcTransaction = new BtcTransaction(
                                                "",
                                                addressString,
                                                transactionType,
                                                "-" + satoshis,
                                                "",
                                                mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash(),
                                                String.valueOf(mListSupportPojos.get(mPositionOfSupportPojo).getHeight()),
                                                inputList,
                                                totalOutPutAmount);
                                        if (!mBtcTransactionSet.contains(btcTransaction)) {
                                            mBtcTransactionSet.add(btcTransaction);
                                        }
                                    }
                                }
                            }
                        }
                        mPositionOfSupportPojo++;
                        if (mPositionOfSupportPojo == mListSupportPojos.size()) {
                            mBtcTransactionList.addAll(mBtcTransactionSet);
                            getHeader();
                        } else {
                            transactionGet();
                        }

                    } else if (jsonObject.getInt("id") == 6) {

                        Log.d("HEADER RESPONSE", "HEADER RESPONSE");

                        JSONObject result = jsonObject.getJSONObject("result");
                        String time = result.getString("timestamp");
                        BtcTransaction btcTransaction = mBtcTransactionList.get(positionGetHeader++);
                        btcTransaction.setDate(time);

                        if (positionGetHeader == mBtcTransactionList.size()) {
                            mBtcTransactionSet.clear();
                            mBtcTransactionSet.addAll(mBtcTransactionList);

                            if (!btcTxWithoutFeeAlsoAdded) {
                                btcTransactionsWithoutFee = new ArrayList<>();
                                for (BtcTransaction btcTransaction1 : mBtcTransactionSet) {
                                    if (btcTransaction1.getFee().equals("")) {
                                        btcTransactionsWithoutFee.add(btcTransaction1);
                                    }
                                }
                                btcTxWithoutFeeAlsoAdded = true;
                            }

                            if (btcTransactionsWithoutFee.isEmpty()) {
                                publishProgress("");
                            } else {
                                transactionGetPrevious();
                            }
                        } else {
                            getHeader();
                        }
                    } else if (jsonObject.getInt("id") == 7) {

                        String rawTransaction = jsonObject.getString("result");
                        byte[] raw = HEX.decode(rawTransaction);
                        Transaction transactionPrev = new Transaction(btcParams, raw);
                        int utxoIndex = (int) inputsList.get(inputsPos).getIndexOutputFromPrevTx();
                        Log.d("utxoIndex", utxoIndex + "");
                        inputValue += transactionPrev
                                .getOutputs()
                                .get(utxoIndex)
                                .getValue()
                                .longValue();
                        inputsPos++;
                        if (inputsPos == inputsList.size()) {
                            long totalAmountFromOutputs = btcTransactionsWithoutFee.get(mPositionBtcTransaction).getTotalAmountInOutPuts();
                            long fee = inputValue - totalAmountFromOutputs;
                            btcTransactionsWithoutFee.get(mPositionBtcTransaction).setFee(String.valueOf(fee));
                            inputValue = 0;
                            inputsPos = 0;
                            mPositionBtcTransaction++;
                            if (mPositionBtcTransaction == btcTransactionsWithoutFee.size()) {
                                mBtcTransactionSet.addAll(btcTransactionsWithoutFee);
                                publishProgress("");
                            } else {
                                transactionGetPrevious();
                            }
                        } else {
                            getInputsValue();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mBitcoinHistoryFragment != null) {
                        mBitcoinHistoryFragment.hidePleaseWaitDialog();
                    }
                    if (mDashboardFragment != null) {
                        Log.d("DashboardFragmentHidePleaseWaitDialog", "Hide History Bitcoin Presenter");
                        mDashboardFragment.hidePleaseWaitDialogForBitcoinHistoryPresenter();
                    }
                }
            });
            mTcpClientBtc.run();
            return mTcpClientBtc;
        }

        void doProgress() {
            publishProgress("progress");
        }

        @Override
        protected void onPostExecute(TcpClientBtc mTcpClientBtc) {
            super.onPostExecute(mTcpClientBtc);
            Log.d("onPostExecute", "onPostExecute started");

            if (!mTcpClientBtc.error.isEmpty()) {
                Log.d("error for dialog", mTcpClientBtc.error);
                showAlertDialog(mContext, mContext.getString(R.string.could_not_connect_for) + "Bitcoin");
//                SplashActivity.mBtcTransactionsListForSplashActivity = new ArrayList<>();
            }

            if (mDashboardFragment != null) {
                Log.d("DashboardFragmentHidePleaseWaitDialog", "Hide History Bitcoin Presenter");
                mDashboardFragment.hidePleaseWaitDialogForBitcoinHistoryPresenter();
            }
            if (mBitcoinHistoryFragment != null) {
                Log.d("hidePleaseWaitDialog", "Hide History Bitcoin Presenter");
                mBitcoinHistoryFragment.hidePleaseWaitDialog();
            }

            if (mTcpClientBtc != null) {
                mTcpClientBtc.stopClient();
            }

            positionGetHistory = 0;
            positionGetHeader = 0;
            mPositionOfSupportPojo = 0;
            mPositionBtcTransaction = 0;
            btcTxWithoutFeeAlsoAdded = false;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("SET BTC TRANSACTION SIZE", mBtcTransactionSet.size() + "");
            Log.d("SET BTC TRANSACTION INFO", mBtcTransactionSet.toString());

            if (values[0].contains("error")) {
                showAlertDialog(mContext, values[1]);
                if (mBitcoinHistoryFragment != null) {
                    mBitcoinHistoryFragment.hidePleaseWaitDialog();
                }
                if (mDashboardFragment != null) {
                    Log.d("DashboardFragmentHidePleaseWaitDialog", "Hide History Bitcoin Presenter");
                    mDashboardFragment.hidePleaseWaitDialogForBitcoinHistoryPresenter();
                }
                mTcpClientBtc.stopClient();
                positionGetHistory = 0;
                positionGetHeader = 0;
                mPositionOfSupportPojo = 0;
                mPositionBtcTransaction = 0;
                btcTxWithoutFeeAlsoAdded = false;
                this.cancel(true);
                return;
            } else if (values[0].contains("progress")) {
                //progress updating
                if (mBitcoinHistoryFragment != null) {
                    mBitcoinHistoryFragment.setDownloadProgress(downloadProgress);
                    mBitcoinHistoryFragment.setTotalProgress(totalProgress);
                } else if (mDashboardFragment != null) {
                    mDashboardFragment.setDownloadProgress(downloadProgressDashboard);
                    mDashboardFragment.setTotalProgress(totalProgressDashboard);
                } else {
                    try {
                        ((SplashActivity) mContext).setDownloadProgress(downloadProgressDashboard);
                        ((SplashActivity) mContext).setTotalProgress(totalProgressDashboard);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!values[0].contains("progress")) {
                mTcpClientBtc.stopClient();
                positionGetHistory = 0;
                positionGetHeader = 0;
                mPositionOfSupportPojo = 0;
                mPositionBtcTransaction = 0;
                btcTxWithoutFeeAlsoAdded = false;

                sortTransactionList();
                SplashActivity.mBtcTransactionsListForSplashActivity = mBtcTransactionList;
                if (!mBtcTransactionSet.isEmpty()) {

                    if (mBitcoinHistoryFragment != null) {
                        mBitcoinHistoryFragment.setBtcTransactionsList(mBtcTransactionList);
                    }

                    App.getDbInstance().btcTransactionDao().deleteAll();
                    App.getDbInstance().btcTransactionDao().insertListBtcAdresses(mBtcTransactionList);
                }
                if (mBitcoinHistoryFragment != null) {
                    mBitcoinHistoryFragment.hidePleaseWaitDialog();
                }
                if (mDashboardFragment != null) {
                    Log.d("DashboardFragmentHidePleaseWaitDialog", "Hide History Bitcoin Presenter");
                    mDashboardFragment.hidePleaseWaitDialogForBitcoinHistoryPresenter();
                }
                this.cancel(true);
            }
        }
    }

    private void sortTransactionList() {

        List<BtcTransaction> btcTransactionListPending = new ArrayList<>();
        List<BtcTransaction> btcTransactionListFinished = new ArrayList<>();

        for (BtcTransaction btcTransaction : mBtcTransactionSet) {
            if (btcTransaction.getBlock().equals("0") | btcTransaction.getBlock().equals("-1")) {
                btcTransactionListPending.add(btcTransaction);
            } else {
                btcTransactionListFinished.add(btcTransaction);
            }
        }

        Collections.sort(btcTransactionListPending);
        Collections.reverse(btcTransactionListPending);

        Collections.sort(btcTransactionListFinished);
        Collections.reverse(btcTransactionListFinished);

        mBtcTransactionList.clear();
        mBtcTransactionList.addAll(btcTransactionListPending);
        mBtcTransactionList.addAll(btcTransactionListFinished);

        mBtcTransactionSet.clear();
        mBtcTransactionSet.addAll(btcTransactionListPending);
        mBtcTransactionSet.addAll(btcTransactionListFinished);
    }

    private void getBtcBalance() {

        balanceBtc = null;
        countBtc = 0;
        satoshiSumBtc = 0;

        totalProgress += 25;

        mConnectTaskBtcGetBalance = new ConnectTaskBtcGetBalance();
        mConnectTaskBtcGetBalance.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    private class ConnectTaskBtcGetBalance extends AsyncTask<String, String, TcpClientBtc> {

        @Override
        protected TcpClientBtc doInBackground(String... message) {

            mTcpClientBtcGetBalance = new TcpClientBtc(response -> {
                //response received from server
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 3) {
                        get20Btc();
                    } else if (jsonObject.getInt("id") == 4) {

                        Log.d("jsonObject", jsonObject.toString());
                        JSONObject balanceResult = new JSONObject(jsonObject.getString("result"));
                        Log.d("balanceResultJSON", balanceResult.toString());

                        String balanceConfirmed = balanceResult.getString("confirmed");
                        String balanceUnconfirmed = balanceResult.getString("unconfirmed");

                        Log.d("balanceUnconfirmed", balanceUnconfirmed);

                        if (balanceUnconfirmed.contains("-") | listStringAddressesForChange.contains(listStringAllAddresses.get(countBtc))) {
                            int totalValue = Integer.valueOf(balanceConfirmed) + Integer.valueOf(balanceUnconfirmed);
                            satoshiSumBtc += totalValue;
                        } else {
                            satoshiSumBtc += Integer.valueOf(balanceConfirmed);
                        }

                        countBtc++;
                        if (countBtc < 25) {
                            get20Btc();
                        }
                        if (countBtc == 25) {
                            publishProgress("");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    publishProgress("");
                }
            });
            mTcpClientBtcGetBalance.run();

            return mTcpClientBtcGetBalance;
        }

        @Override
        protected void onPostExecute(TcpClientBtc mTcpClientBtcGetBalance) {
            super.onPostExecute(mTcpClientBtcGetBalance);
            if (!mTcpClientBtcGetBalance.error.isEmpty()) {
                showAlertDialog(mContext, mContext.getString(R.string.could_not_connect_for) + "Bitcoin");
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("onProgressUpdate", "start");

            BigDecimal balanceBtcBigDecimal = new BigDecimal(satoshiSumBtc).divide(new BigDecimal(100000000));
            balanceBtc = String.valueOf(balanceBtcBigDecimal);
            SharedPreferencesHelper.getInstance().putStringValue(BTC_BALANCE_KEY, balanceBtc);

            String courseBtc = SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY);
            BigDecimal courseBigDecimal = new BigDecimal(courseBtc);

            BigDecimal balanceBtcInUsdBigDecimal = courseBigDecimal.multiply(balanceBtcBigDecimal);
            SharedPreferencesHelper.getInstance().putStringValue(BTC_BALANCE_IN_USD_KEY,
                    String.valueOf(balanceBtcInUsdBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)));

            Log.d("BALANCE BTC", balanceBtc);
            mTcpClientBtcGetBalance.stopClient();
        }
    }

    private void get20Btc() {

        String btcAddress = listStringAllAddresses.get(countBtc);
        if (mTcpClientBtcGetBalance != null) {
            downloadProgress++;
            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 4,\"method\":\"blockchain.address.get_balance\",\"params\":[\"" + btcAddress + "\"]}";
            mTcpClientBtcGetBalance.sendMessage(jsonRequest);
        }
    }
}

