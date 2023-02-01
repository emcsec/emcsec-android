package com.aspanta.emcsec.presenter.historyPresenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;
import com.aspanta.emcsec.model.apiTCP.TcpClientEmc;
import com.aspanta.emcsec.model.supportPojos.HistorySupportPojo;
import com.aspanta.emcsec.model.supportPojos.Input;
import com.aspanta.emcsec.tools.EmercoinNetwork;
import com.aspanta.emcsec.tools.EmercoinTransaction;
import com.aspanta.emcsec.tools.JsonRpcHelper;
import com.aspanta.emcsec.ui.activities.SplashActivity;
import com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment;
import com.aspanta.emcsec.ui.fragment.historyFragment.emercoinHistoryFragment.IEmercoinHistoryFragment;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.METHOD_GET_BALANCE;
import static com.aspanta.emcsec.tools.Config.METHOD_GET_HISTORY;
import static com.aspanta.emcsec.tools.Config.METHOD_TRANSACTION_GET;
import static com.aspanta.emcsec.ui.activities.MainActivity.showAlertDialog;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;
import static org.bitcoinj.core.Utils.HEX;

public class HistoryEmercoinPresenter implements IHistoryPresenter {

    private TcpClientEmc mTcpClientEmc;
    private TcpClientEmc mTcpClientEmcGetBalance;

    private List<EmcAddress> mListEmcAddresses = App.getDbInstance().emcAddressDao().getAll();
    private List<EmcAddressForChange> mListEmcAddressesForChange = App.getDbInstance().emcAddressForChangeDao().getAll();
    private List<HistorySupportPojo> mListSupportPojos = new ArrayList<>();

    private List<Input> inputsList = new ArrayList<>();
    private List<EmcTransaction> emcTransactionsWithoutFee = new ArrayList<>();

    private int positionGetHistory = 0;
    private int mPositionOfSupportPojo = 0;
    private int mPositionEmcTransaction = 0;
    private int inputsPos = 0;
    private long inputValue = 0;

    private int height;
    private IEmercoinHistoryFragment mEmercoinHistoryFragment;
    private Set<EmcTransaction> mEmcTransactionSet;
    private Context mContext;
    private List<String> listStringAddresses;
    private List<String> listStringAddressesForChange;
    private List<String> listStringAllAddresses;
    private boolean emcTxWithoutFeeAlsoAdded = false;
    private ConnectTaskEmc connectTaskEmc;
    private DashboardFragment mDashboardFragment;
    private int downloadProgress = 0;
    private int totalProgress = 25;

    private boolean doneTotalProgressTransactionGet;

    //fields for getting balance
    private String balanceEmc;
    private int countEmc;
    private long satoshiSumEmc;
    private String currency;
    private ConnectTaskEmcGetBalance mConnectTaskEmcGetBalance;

    public HistoryEmercoinPresenter(Context context, IEmercoinHistoryFragment emercoinHistoryFragment, DashboardFragment dashboardFragment) {
        mEmercoinHistoryFragment = emercoinHistoryFragment;
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
        positionGetHistory = 0;

        mListSupportPojos = new ArrayList<>();
        listStringAddressesForChange = new ArrayList<>();
        for (EmcAddressForChange emcAddressForChange : mListEmcAddressesForChange) {
            listStringAddressesForChange.add(emcAddressForChange.getAddress());
        }

        listStringAddresses = new ArrayList<>();
        for (EmcAddress emcAddress : mListEmcAddresses) {
            listStringAddresses.add(emcAddress.getAddress());
        }

        Log.d("listStringAddresses", listStringAddresses.toString());

        listStringAllAddresses = new ArrayList<>();
        listStringAllAddresses.addAll(listStringAddresses);
        listStringAllAddresses.addAll(listStringAddressesForChange);

        mEmcTransactionSet = new HashSet<>();

        if (mEmercoinHistoryFragment != null) {
            mEmercoinHistoryFragment.showPleaseWaitDialog();
            mEmercoinHistoryFragment.setDownloadProgress(downloadProgress);
            mEmercoinHistoryFragment.setTotalProgress(totalProgress);
        }
        if (mDashboardFragment != null) {
            mDashboardFragment.showPleaseWaitDialogForEmercoinHistoryPresenter();
            mDashboardFragment.setDownloadProgress(downloadProgressDashboard);
            mDashboardFragment.setTotalProgress(totalProgressDashboard);
        }

        connectTaskEmc = new ConnectTaskEmc();
        connectTaskEmc.execute("");
        if (mEmercoinHistoryFragment != null) {
            getEmcBalance();
        }
    }

    private void getHistory() {

        Log.d("positionGetHistory", positionGetHistory + "");

        if (positionGetHistory < 25) {
            downloadProgress++;
            downloadProgressDashboard++;
            connectTaskEmc.doProgress();

            String address = listStringAllAddresses.get(positionGetHistory);
            String jsonRequest = JsonRpcHelper.createRpcRequest(2, METHOD_GET_HISTORY, address);
//            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 2,\"method\":\"blockchain.address.get_history\",\"params\":[\"" + address + "\"]}";
            mTcpClientEmc.sendMessage(jsonRequest);
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
        connectTaskEmc.doProgress();

        String tx_hash = mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash();
        String jsonRequest = JsonRpcHelper.createRpcRequest(5, METHOD_TRANSACTION_GET, tx_hash);
//        String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 5,\"method\":\"blockchain.transaction.get\",\"params\":[\"" + tx_hash + "\"]}";
        mTcpClientEmc.sendMessage(jsonRequest);
    }

    private void transactionGetPrevious() {

        if (mPositionEmcTransaction < mEmcTransactionSet.size()) {

            inputsList = emcTransactionsWithoutFee.get(mPositionEmcTransaction).getInputList();
            totalProgress += inputsList.size();
            totalProgressDashboard += inputsList.size();
            getInputsValue();
        }
    }

    private void getInputsValue() {

        if (inputsPos < inputsList.size()) {

            downloadProgress++;
            downloadProgressDashboard++;

            connectTaskEmc.doProgress();

            String prevTxHash = inputsList.get(inputsPos).getPrev_tx_hash();
            String jsonRequest = JsonRpcHelper.createRpcRequest(6, METHOD_TRANSACTION_GET, prevTxHash);
//            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 6,\"method\":\"blockchain.transaction.get\",\"params\":[\"" + prevTxHash + "\"]}";
            mTcpClientEmc.sendMessage(jsonRequest);
        } else {
            inputsPos = 0;
        }
    }

    private class ConnectTaskEmc extends AsyncTask<String, String, TcpClientEmc> {

        @Override
        protected TcpClientEmc doInBackground(String... message) {

            mTcpClientEmc = new TcpClientEmc(response -> {
                //response received from server
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 1) {
                        getHistory();
                    } else if (jsonObject.getInt("id") == 2) {

                        Log.d("JSON GET HISTORY", jsonObject.toString());
                        JSONArray getHistoryJSONArray = jsonObject.getJSONArray("result");
                        Log.d("getHistoryJSONArray", getHistoryJSONArray.toString());

                        int lengthOfHistoryArray = getHistoryJSONArray.length();

                        if (lengthOfHistoryArray == 0) {
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
                        EmercoinTransaction transaction = new EmercoinTransaction(EmercoinNetwork.get(), raw);

                        List<Input> inputList = new ArrayList<>();
                        for (TransactionInput transactionInput : transaction.getInputs()) {
                            inputList.add(new Input(transactionInput.getOutpoint().getHash().toString(), transactionInput.getOutpoint().getIndex()));
                        }

                        long totalOutPutAmount = 0;
                        for (TransactionOutput transactionOutput : transaction.getOutputs()) {
                            totalOutPutAmount += transactionOutput.getValue().longValue();
                        }


                        TransactionInput input = transaction.getInputs().get(0);
                        String transactionType = "";

                        LegacyAddress addressFromFirstInput = LegacyAddress
                                .fromPubKeyHash(EmercoinNetwork.get(), Utils.sha256hash160(input.getScriptSig().getPubKey()));

                        Log.d("addressFromFirstInput", addressFromFirstInput.toString());

                        if (!listStringAddresses.contains(addressFromFirstInput.toString()) & !listStringAddressesForChange.contains(addressFromFirstInput.toString())) {
                            transactionType = "Receive";
                            Log.d("transactionType", "Receive");
                            for (TransactionOutput transactionOutput : transaction.getOutputs()) {

                                Address address = transactionOutput.getAddressFromP2PKHScript(EmercoinNetwork.get());
                                String addressString;
                                try {
                                    addressString = address.toString();
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                    try {
                                        address = transactionOutput.getAddressFromP2SH(EmercoinNetwork.get());
                                        addressString = address.toString();
                                    } catch (NullPointerException npe2) {
                                        npe.printStackTrace();
                                        addressString = "address for NVS";
                                        Log.d("address in Outputs", addressString);
                                        break;
                                    }
                                }

                                Log.d("address in Outputs", addressString);

                                if (listStringAddresses.contains(addressString)) {
                                    long satoshis = transactionOutput.getValue().longValue();
                                    Log.d("TransactionInfo", addressString + "  " + satoshis);
                                    EmcTransaction emcTransaction = new EmcTransaction(
                                            String.valueOf(transaction.getTime()),
                                            addressString,
                                            transactionType,
                                            String.valueOf(satoshis),
                                            "None",
                                            mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash(),
                                            String.valueOf(mListSupportPojos.get(mPositionOfSupportPojo).getHeight()),
                                            inputList,
                                            totalOutPutAmount);

                                    Log.d("emcTransaction", emcTransaction.toString());
                                    if (!mEmcTransactionSet.contains(emcTransaction)) {
                                        mEmcTransactionSet.add(emcTransaction);
                                    }
                                }
                            }
                        } else {
                            Log.d("transactionType", "Send");
                            for (TransactionOutput transactionOutput : transaction.getOutputs()) {
                                Address addressfromOutputs = transactionOutput.getAddressFromP2PKHScript(EmercoinNetwork.get());
                                String addressString;
                                try {
                                    addressString = addressfromOutputs.toString();
                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();

                                    try {
                                        addressfromOutputs = transactionOutput.getAddressFromP2SH(EmercoinNetwork.get());
                                        addressString = addressfromOutputs.toString();
                                    } catch (NullPointerException npe2) {
                                        npe.printStackTrace();
                                        addressString = "address for NVS";
                                        Log.d("address in Outputs", addressString);
                                        break;
                                    }
                                }
                                if (listStringAddresses.contains(addressString)) {
                                    transactionType = "Self";
                                    long satoshis = transactionOutput.getValue().longValue();
                                    EmcTransaction emcTransaction = new EmcTransaction(
                                            String.valueOf(transaction.getTime()),
                                            addressString,
                                            transactionType,
                                            String.valueOf(satoshis),
                                            "",
                                            mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash(),
                                            String.valueOf(mListSupportPojos.get(mPositionOfSupportPojo).getHeight()),
                                            inputList,
                                            totalOutPutAmount);
                                    if (!mEmcTransactionSet.contains(emcTransaction)) {
                                        mEmcTransactionSet.add(emcTransaction);
                                    }
                                } else {
                                    if (!listStringAddressesForChange.contains(addressString)) {
                                        transactionType = "Send";
                                        long satoshis = transactionOutput.getValue().longValue();
                                        EmcTransaction emcTransaction = new EmcTransaction(
                                                String.valueOf(transaction.getTime()),
                                                addressString,
                                                transactionType,
                                                "-" + satoshis,
                                                "",
                                                mListSupportPojos.get(mPositionOfSupportPojo).getTx_hash(),
                                                String.valueOf(mListSupportPojos.get(mPositionOfSupportPojo).getHeight()),
                                                inputList,
                                                totalOutPutAmount);
                                        if (!mEmcTransactionSet.contains(emcTransaction)) {
                                            mEmcTransactionSet.add(emcTransaction);
                                        }
                                    }
                                }
                            }
                        }

                        mPositionOfSupportPojo++;
                        if (mPositionOfSupportPojo == mListSupportPojos.size()) {
                            if (!emcTxWithoutFeeAlsoAdded) {
                                emcTransactionsWithoutFee = new ArrayList<>();
                                for (EmcTransaction emcTransaction : mEmcTransactionSet) {
                                    if (emcTransaction.getFee().equals("")) {
                                        emcTransactionsWithoutFee.add(emcTransaction);
                                    }
                                }
                                emcTxWithoutFeeAlsoAdded = true;
                            }

                            if (emcTransactionsWithoutFee.isEmpty()) {
                                publishProgress("");
                            } else {
                                transactionGetPrevious();
                            }
                        } else {
                            transactionGet();
                        }
                    } else if (jsonObject.getInt("id") == 6) {

                        String rawTransaction = jsonObject.getString("result");
                        byte[] raw = HEX.decode(rawTransaction);
                        EmercoinTransaction transactionPrev = new EmercoinTransaction(EmercoinNetwork.get(), raw);
                        int utxoIndex = (int) inputsList.get(inputsPos).getIndexOutputFromPrevTx();
                        Log.d("utxoIndex", utxoIndex + "");
                        inputValue += transactionPrev
                                .getOutputs()
                                .get(utxoIndex)
                                .getValue()
                                .longValue();

                        inputsPos++;
                        if (inputsPos == inputsList.size()) {
                            long totalAmountFromOutputs = emcTransactionsWithoutFee.get(mPositionEmcTransaction).getTotalAmountInOutPuts();
                            long fee = inputValue - totalAmountFromOutputs;
                            emcTransactionsWithoutFee.get(mPositionEmcTransaction).setFee(String.valueOf(fee));
                            inputValue = 0;
                            inputsPos = 0;
                            mPositionEmcTransaction++;
                            if (mPositionEmcTransaction == emcTransactionsWithoutFee.size()) {
                                mEmcTransactionSet.addAll(emcTransactionsWithoutFee);
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
                    if (mEmercoinHistoryFragment != null) {
                        mEmercoinHistoryFragment.hidePleaseWaitDialog();
                    }
                    if (mDashboardFragment != null) {
                        Log.d("DashbFraHidePleaseWait", "Hide History Emercoin Presenter");
                        mDashboardFragment.hidePleaseWaitDialogForEmercoinHistoryPresenter();
                    }
                }
            }, mContext);
            mTcpClientEmc.run();
            return mTcpClientEmc;
        }

        void doProgress() {
            publishProgress("progress");
        }

        @Override
        protected void onPostExecute(TcpClientEmc mTcpClientEmc) {
            super.onPostExecute(mTcpClientEmc);
            Log.d("onPostExecute", "onPostExecute started");

            if (!mTcpClientEmc.error.isEmpty()) {
                Log.d("error for dialog", mTcpClientEmc.error);
                showAlertDialog(mContext, mContext.getString(R.string.could_not_connect_for) + "Emercoin");
            }

            if (mDashboardFragment != null) {
                Log.d("DashbFrHidePleaseWait", "Hide History Emercoin Presenter");
                mDashboardFragment.hidePleaseWaitDialogForEmercoinHistoryPresenter();
            }
            if (mEmercoinHistoryFragment != null) {
                Log.d("hidePleaseWaitDialog", "Hide History Bitcoin Presenter");
                mEmercoinHistoryFragment.hidePleaseWaitDialog();
            }

            if (mTcpClientEmc != null) {
                mTcpClientEmc.stopClient();
            }
            positionGetHistory = 0;
            height = 0;
            mPositionOfSupportPojo = 0;
            mPositionEmcTransaction = 0;
            emcTxWithoutFeeAlsoAdded = false;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

//            Log.d("SET EMC TRANSACTION SIZE", mEmcTransactionSet.size() + "");
//            Log.d("SET EMC TRANSACTION INFO", mEmcTransactionSet.toString());

            if (values[0].contains("error")) {
                showAlertDialog(mContext, values[1]);
                if (mEmercoinHistoryFragment != null) {
                    mEmercoinHistoryFragment.hidePleaseWaitDialog();
                }
                if (mDashboardFragment != null) {
//                    Log.d("DashboardFragmentHidePleaseWaitDialog", "Hide History Emercoin Presenter");
                    mDashboardFragment.hidePleaseWaitDialogForEmercoinHistoryPresenter();
                }
                mTcpClientEmc.stopClient();
                positionGetHistory = 0;
                height = 0;
                mPositionOfSupportPojo = 0;
                mPositionEmcTransaction = 0;
                emcTxWithoutFeeAlsoAdded = false;
                return;
            } else if (values[0].contains("progress")) {
                //progress updating
                if (mEmercoinHistoryFragment != null) {
                    mEmercoinHistoryFragment.setDownloadProgress(downloadProgress);
                    mEmercoinHistoryFragment.setTotalProgress(totalProgress);
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
                mTcpClientEmc.stopClient();
                positionGetHistory = 0;
                height = 0;
                mPositionOfSupportPojo = 0;
                mPositionEmcTransaction = 0;
                emcTxWithoutFeeAlsoAdded = false;

                SplashActivity.mEmcTransactionsListForSplashActivity = new ArrayList<>();
                SplashActivity.mEmcTransactionsListForSplashActivity.addAll(mEmcTransactionSet);
                if (!mEmcTransactionSet.isEmpty()) {
                    List<EmcTransaction> newListofEmcTx = new ArrayList<>();
                    newListofEmcTx.addAll(mEmcTransactionSet);
                    sortTransactionList(newListofEmcTx);

                    if (mEmercoinHistoryFragment != null) {
                        mEmercoinHistoryFragment.setEmcTransactionsList(newListofEmcTx);
                    }

                    App.getDbInstance().emcTransactionDao().deleteAll();
                    App.getDbInstance().emcTransactionDao().insertListEmcAdresses(newListofEmcTx);
                }

                if (mEmercoinHistoryFragment != null) {
                    mEmercoinHistoryFragment.hidePleaseWaitDialog();
                }
                if (mDashboardFragment != null) {
//                    Log.d("DashboardFragmentHidePleaseWaitDialog", "Hide History Emercoin Presenter");
                    mDashboardFragment.hidePleaseWaitDialogForEmercoinHistoryPresenter();
                }
                this.cancel(true);
            }
        }
    }

    private void sortTransactionList(List<EmcTransaction> emcTransactionList) {

        List<EmcTransaction> emcTransactionListPending = new ArrayList<>();
        List<EmcTransaction> emcTransactionListFinished = new ArrayList<>();

        for (EmcTransaction emcTransaction : emcTransactionList) {
            if (emcTransaction.getBlock().equals("0") | emcTransaction.getBlock().equals("-1")) {
                emcTransactionListPending.add(emcTransaction);
            } else {
                emcTransactionListFinished.add(emcTransaction);
            }
        }

        Collections.sort(emcTransactionListPending);
        Collections.reverse(emcTransactionListPending);

        Collections.sort(emcTransactionListFinished);
        Collections.reverse(emcTransactionListFinished);

        emcTransactionList.clear();
        emcTransactionList.addAll(emcTransactionListPending);
        emcTransactionList.addAll(emcTransactionListFinished);
    }

    private void getEmcBalance() {

        balanceEmc = null;
        countEmc = 0;
        satoshiSumEmc = 0;

        totalProgress += 25;

        mConnectTaskEmcGetBalance = new ConnectTaskEmcGetBalance();
        mConnectTaskEmcGetBalance.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    private class ConnectTaskEmcGetBalance extends AsyncTask<String, String, TcpClientEmc> {

        @Override
        protected TcpClientEmc doInBackground(String... message) {

            mTcpClientEmcGetBalance = new TcpClientEmc(response -> {
                //response received from server
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 1) {
                        get20Emc();
                    } else if (jsonObject.getInt("id") == 2) {

                        Log.d("jsonObject", jsonObject.toString());
                        JSONObject balanceResult = new JSONObject(jsonObject.getString("result"));
                        Log.d("balanceResultJSON", balanceResult.toString());

                        String balanceConfirmed = balanceResult.getString("confirmed");
                        String balanceUnconfirmed = balanceResult.getString("unconfirmed");

                        Log.d("balanceUnconfirmed", balanceUnconfirmed);

                        if (balanceUnconfirmed.contains("-") | listStringAddressesForChange.contains(listStringAllAddresses.get(countEmc))) {
                            long totalValue = Long.valueOf(balanceConfirmed) + Long.valueOf(balanceUnconfirmed);
                            satoshiSumEmc += totalValue;
                        } else {
                            satoshiSumEmc += Long.valueOf(balanceConfirmed);
                        }

                        countEmc++;
                        if (countEmc < 25) {
                            get20Emc();
                        }
                        if (countEmc == 25) {
                            publishProgress("");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    publishProgress("");
                }
            }, mContext);
            mTcpClientEmcGetBalance.run();

            return mTcpClientEmcGetBalance;
        }

        @Override
        protected void onPostExecute(TcpClientEmc mTcpClientEmcGetBalance) {
            super.onPostExecute(mTcpClientEmcGetBalance);
            if (!mTcpClientEmcGetBalance.error.isEmpty()) {
                showAlertDialog(mContext, mContext.getString(R.string.could_not_connect_for) + "Emercoin");
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("onProgressUpdate", "start");

            BigDecimal balanceEmcBigDecimal = new BigDecimal(satoshiSumEmc).divide(new BigDecimal(1000000));
            balanceEmc = String.valueOf(balanceEmcBigDecimal);
            SPHelper.getInstance().putStringValue(EMC_BALANCE_KEY, balanceEmc);

            String courseEmc = SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY);

            if (courseEmc != null && !courseEmc.equals("?") && !courseEmc.isEmpty()) {
                BigDecimal courseBigDecimal = new BigDecimal(courseEmc);
                BigDecimal balanceEmcInUsdBigDecimal = courseBigDecimal.multiply(balanceEmcBigDecimal);
                SPHelper.getInstance().putStringValue(EMC_BALANCE_IN_USD_KEY,
                        String.valueOf(balanceEmcInUsdBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)));
            }

            mTcpClientEmcGetBalance.stopClient();
        }
    }

    private void get20Emc() {

        String emcAddress = listStringAllAddresses.get(countEmc);
        if (mTcpClientEmcGetBalance != null) {
            downloadProgress++;
            String jsonRequest = JsonRpcHelper.createRpcRequest(2, METHOD_GET_BALANCE, emcAddress);
//            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 2,\"method\":\"blockchain.address.get_balance\",\"params\":[\"" + emcAddress + "\"]}";
            mTcpClientEmcGetBalance.sendMessage(jsonRequest);
        }
    }
}