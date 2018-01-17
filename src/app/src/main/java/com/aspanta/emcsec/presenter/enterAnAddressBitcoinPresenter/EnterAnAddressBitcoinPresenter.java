package com.aspanta.emcsec.presenter.enterAnAddressBitcoinPresenter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.BtcAddress;
import com.aspanta.emcsec.db.room.BtcAddressForChange;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOBitcoinAlreadyUsed;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOBitcoinFromChange;
import com.aspanta.emcsec.model.apiTCP.TcpClientBtc;
import com.aspanta.emcsec.tools.UTXOBitcoin;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment.EnterAnAddressBitcoinFragment;
import com.aspanta.emcsec.ui.fragment.enterAnAddressBitcoinFragment.IEnterAnAddressBitcoinFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.SendCoinFragment;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.BTC_COURSE_KEY;
import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_BTC;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.showAlertDialog;
import static com.aspanta.emcsec.tools.InternetConnection.internetConnectionChecking;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;
import static org.bitcoinj.core.Utils.HEX;

public class EnterAnAddressBitcoinPresenter implements IEnterAnAddressBitcoinPresenter {

    private IEnterAnAddressBitcoinFragment mFragment;
    private Context mContext;

    //fields for getting balance
    private String balanceBtc;
    private int countBtc;
    private int satoshiSumBtc;
    private String currency;

    private NetworkParameters params = MainNetParams.get();
    private TcpClientBtc mTcpClientBtc;

    private List<String> mListBtcAddresses = new ArrayList<>();
    private List<UTXOBitcoin> mUTXOs = new ArrayList<>();

    List<UTXOBitcoinAlreadyUsed> mUTXOBitcoinAlreadyUsedList;
    private UTXOBitcoinFromChange mUTXOBitcoinFromChange;
    private List<UTXOBitcoinFromChange> mUTXOBitcoinFromChangeList;
    private List<String> mListBtcAddressesForChange = new ArrayList<>();

    private int positionGetListUnspent = 0;
    private long utxosValue = 0;
    private long amountToSend;
    private long feePerKb;
    private Transaction transaction;
    private boolean done = false;
    private String addressToSend;
    private String addressForChange;
    private ConnectTaskBtc mConnectTaskBtc;
    private ConnectTaskBtcGetBalance mConnectTaskBtcGetBalance;

    public EnterAnAddressBitcoinPresenter(Context context, IEnterAnAddressBitcoinFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public void sendBitcoin(String address, String amountToSend, String feePerKb) {

        downloadProgressDashboard = 0;
        totalProgressDashboard = 25;

        mUTXOBitcoinAlreadyUsedList = new ArrayList<>();
        mUTXOBitcoinAlreadyUsedList = App.getDbInstance().utxoBitcoinAlreadyUsedDao().getAll();

        String dbUTXOsAlreadyUsedChangeOrNot = "";
        for (int i = 0; i < mUTXOBitcoinAlreadyUsedList.size(); i++) {
            long timeToRemove = mUTXOBitcoinAlreadyUsedList.get(i).getTimeToRemove();
            if (System.currentTimeMillis() - timeToRemove > 604800000) {
                mUTXOBitcoinAlreadyUsedList.remove(i);
                i--;
                dbUTXOsAlreadyUsedChangeOrNot = "change";
            }
        }
        if (dbUTXOsAlreadyUsedChangeOrNot.equals("change")) {
            App.getDbInstance().utxoBitcoinAlreadyUsedDao().deleteAll();
            App.getDbInstance().utxoBitcoinAlreadyUsedDao().insertListUTXOBitcoinAlready(mUTXOBitcoinAlreadyUsedList);
        } else {
            Log.d("DB have no changes", mUTXOBitcoinAlreadyUsedList.toString());
        }

        mUTXOBitcoinFromChangeList = new ArrayList<>();
        mUTXOBitcoinFromChangeList = App.getDbInstance().utxoBitcoinFromChangeDao().getAll();

        String dbUTXOsFromChangeChangeOrNot = "";
        for (int i = 0; i < mUTXOBitcoinFromChangeList.size(); i++) {
            long timeToRemove = mUTXOBitcoinFromChangeList.get(i).getTimeToRemove();
            if (System.currentTimeMillis() - timeToRemove > 86400) {
                mUTXOBitcoinFromChangeList.remove(i);
                i--;
                dbUTXOsFromChangeChangeOrNot = "change";
            }
        }

        if (dbUTXOsFromChangeChangeOrNot.equals("change")) {
            App.getDbInstance().utxoBitcoinFromChangeDao().deleteAll();
            App.getDbInstance().utxoBitcoinFromChangeDao().insertListUTXOBitcoinFromChange(mUTXOBitcoinFromChangeList);
        } else {
            Log.d("DB have no changes", mUTXOBitcoinFromChangeList.toString());
        }

        this.addressToSend = address;
        this.amountToSend = (long) (Double.valueOf(amountToSend) * 100000000);

        String balance = SharedPreferencesHelper.getInstance().getStringValue(BTC_BALANCE_KEY);
        if (balance != null && !balance.equals("") && !balance.equals("?")) {
            long balanceLong = (long) (Double.valueOf(balance) * 100000000);
            if (balanceLong <= this.amountToSend) {
                showAlertDialog(mContext, mContext.getString(R.string.insufficient_funds));
                return;
            }
        }

        addressForChange = SharedPreferencesHelper.getInstance().getStringValue(CHANGE_ADDRESS_BTC);
        mFragment.showPleaseWaitDialog();
        mFragment.setDownloadProgress(downloadProgressDashboard);
        mFragment.setTotalProgress(totalProgressDashboard);

        for (BtcAddress btcAddress : App.getDbInstance().btcAddressDao().getAll()) {
            mListBtcAddresses.add(btcAddress.getAddress());
        }

        for (BtcAddressForChange btcAddressForChange : App.getDbInstance().btcAddressForChangeDao().getAll()) {
            mListBtcAddresses.add(btcAddressForChange.getAddress());
            mListBtcAddressesForChange.add(btcAddressForChange.getAddress());
        }

        this.feePerKb = (long) (Double.valueOf(feePerKb) * 100000000);

        Log.d("amountToSend", this.amountToSend + "");
        Log.d("feePerKb", this.feePerKb + "");

        transaction = new Transaction(params);
        mConnectTaskBtc = new ConnectTaskBtc();
        mConnectTaskBtc.execute("");
    }

    private void getListUnspent() {
        if (positionGetListUnspent < 25) {
            String address = mListBtcAddresses.get(positionGetListUnspent);
            downloadProgressDashboard++;
            mConnectTaskBtc.doProgress();
            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 6,\"method\":\"blockchain.address.listunspent\",\"params\":[\"" + address + "\"]}";
            mTcpClientBtc.sendMessage(jsonRequest);
        }
    }

    private class ConnectTaskBtc extends AsyncTask<String, String, TcpClientBtc> {

        @SuppressLint("LongLogTag")
        @Override
        protected TcpClientBtc doInBackground(String... message) {

            mTcpClientBtc = new TcpClientBtc(response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 3) {
                        getListUnspent();
                    } else if (jsonObject.getInt("id") == 6) {

                        Log.d("JSON GET LIST UNSPENT", jsonObject.toString());

                        JSONArray getUnspentJSONArray = jsonObject.getJSONArray("result");
                        Log.d("getUnspentJSONArray", getUnspentJSONArray.toString());

                        int lengthOfUnspentArray = getUnspentJSONArray.length();
                        Log.d("lengthOfUnspentArray", lengthOfUnspentArray + "");

                        for (int i = 0; i < lengthOfUnspentArray; i++) {

                            JSONObject jsonObject1 = getUnspentJSONArray.getJSONObject(i);
                            String address = mListBtcAddresses.get(positionGetListUnspent);

                            Address address1 = Address.fromBase58(params, address);

                            Log.d("addressForUtxo", address);
                            Log.d("tx_hash", jsonObject1.getString("tx_hash"));

                            Script scriptForUTXO = ScriptBuilder.createOutputScript(address1);
                            Sha256Hash sha256Hash = Sha256Hash.wrap(jsonObject1.getString("tx_hash"));
                            Coin coinValue = Coin.valueOf(jsonObject1.getLong("value"));
                            mUTXOs.add(new UTXOBitcoin(sha256Hash, jsonObject1.getInt("tx_pos"), coinValue, jsonObject1.getInt("height"), true, scriptForUTXO, address));
                        }

                        positionGetListUnspent++;
                        getListUnspent();

                        if (positionGetListUnspent == 25) {

                            Collections.sort(mUTXOs);
                            Collections.reverse(mUTXOs);

                            Log.d("utxoBitcoinAlreadyUsedList", mUTXOBitcoinAlreadyUsedList.toString());

                            List<UTXOBitcoin> utxoBitcoinListLocal = new ArrayList<>();
                            for (UTXOBitcoinAlreadyUsed utxoBitcoinAlreadyUsed : mUTXOBitcoinAlreadyUsedList) {

                                Address address1 = Address.fromBase58(params, utxoBitcoinAlreadyUsed.getAddress());

                                Sha256Hash sha256Hash = Sha256Hash.wrap(Utils.HEX.decode(utxoBitcoinAlreadyUsed.getTx_id()));
                                Script scriptForUTXO = ScriptBuilder.createOutputScript(address1); // creating script for UTXOEmercoin
                                Coin coinValue = Coin.valueOf(utxoBitcoinAlreadyUsed.getValue());
                                utxoBitcoinListLocal.add(new UTXOBitcoin(sha256Hash, utxoBitcoinAlreadyUsed.getIndex(), coinValue, 0, false, scriptForUTXO, address1.toString()));
                            }

                            mUTXOBitcoinAlreadyUsedList.clear();

                            Log.d("UTXOs before removing utxoBitcoinListLocal", mUTXOs.toString());
                            mUTXOs.removeAll(utxoBitcoinListLocal);
                            Log.d("UTXOs before after utxoBitcoinListLocal", mUTXOs.toString());

                            for (UTXOBitcoinFromChange utxoBitcoinFromChange : mUTXOBitcoinFromChangeList) {

                                Address address1 = Address.fromBase58(params, utxoBitcoinFromChange.getAddress());

                                Sha256Hash sha256Hash = Sha256Hash.wrap(Utils.HEX.decode(utxoBitcoinFromChange.getTx_id()));
                                Script scriptForUTXO = ScriptBuilder.createOutputScript(address1); // creating script for UTXOBitcoin
                                Coin coinValue = Coin.valueOf(utxoBitcoinFromChange.getValue());
                                mUTXOs.add(0, new UTXOBitcoin(sha256Hash, utxoBitcoinFromChange.getIndex(), coinValue, 0, false, scriptForUTXO, address1.toString()));
                            }

                            Log.d("UTXOS LIST", mUTXOs.toString());

                            for (UTXOBitcoin utxo : mUTXOs) {

                                if (!done) {
                                    utxosValue += utxo.getValue().longValue();
                                    Log.d("utxosValue on start of for each  ", utxosValue + "");

                                    /*
                                    *  Formation an outpoint with position of utxo in previous transaction
                                    *  and with hash of previous transaction.
                                    *  Signing this outpoint with privKey and formation a signed input.
                                    */

                                    mUTXOBitcoinAlreadyUsedList.add(new UTXOBitcoinAlreadyUsed(
                                            utxo.getHash().toString(),
                                            utxo.getIndex(),
                                            utxo.getValue().longValue(),
                                            utxo.getAddress(),
                                            System.currentTimeMillis()
                                    ));

                                    TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
                                    TransactionInput transactionInput = new TransactionInput(params, transaction, new byte[0], outPoint);
                                    transaction.addInput(transactionInput);

                                    if (utxosValue > amountToSend) {

                                        transaction.clearOutputs();
                                        //adding an output with amount for sending and an address of recipient
                                        transaction.addOutput(Coin.valueOf(amountToSend), Address.fromBase58(params, addressToSend));

                                        if (utxosValue - amountToSend >= 546) {

                                            transaction.clearOutputs();
                                            //adding an output with amount for sending and an address of recipient
                                            transaction.addOutput(Coin.valueOf(amountToSend), Address.fromBase58(params, addressToSend));
                                            //adding an output with value for change without fee and an address for change
                                            transaction.addOutput(Coin.valueOf(utxosValue - amountToSend),
                                                    Address.fromBase58(params, addressForChange)); // Address for change
                                        }

                                        if (transaction.getOutputs().size() == 1) {

                                            //signing inputs
                                            for (int i = 0; i < transaction.getInputs().size(); i++) {
                                                TransactionInput transactionInput1 = transaction.getInput(i);
                                                byte[] privKeyBytes = HEX.decode(SharedPreferencesHelper.getInstance().getStringValue(mUTXOs.get(i).getAddress()));
                                                ECKey ecKey = ECKey.fromPrivate(privKeyBytes);
                                                Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(params, mUTXOs.get(i).getAddress()));
                                                Sha256Hash hash = transaction.hashForSignature(i, scriptPubKey, Transaction.SigHash.SINGLE, true);
                                                ECKey.ECDSASignature ecSig = ecKey.sign(hash);
                                                TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.SINGLE, true);
                                                if (scriptPubKey.isSentToRawPubKey()) {
                                                    transactionInput1.setScriptSig(ScriptBuilder.createInputScript(txSig));
                                                } else {
                                                    if (!scriptPubKey.isSentToAddress()) {
                                                        throw new ScriptException("Don\'t know how to sign for this kind of scriptPubKey: " + scriptPubKey);
                                                    }
                                                    transactionInput1.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
                                                }
                                            }

                                            byte[] bytesRawTransaction = transaction.bitcoinSerialize();
                                            String rawTransaction = HEX.encode(bytesRawTransaction);
                                            long sizeOfRawTransaction1 = bytesRawTransaction.length;

                                            Log.d("rawTransaction1", rawTransaction);
                                            Log.d("sizeOfRawTransaction1", sizeOfRawTransaction1 + "");

                                            long fee = feePerKb / 1000 * sizeOfRawTransaction1;

                                            if (utxosValue - amountToSend > fee) {
//                                                publishProgress("1", "MOCK");
                                                String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 7,\"method\":\"blockchain.transaction.broadcast\",\"params\":[\"" + rawTransaction + "\"]}";
                                                mTcpClientBtc.sendMessage(jsonRequest);
                                                done = true;
                                            }

                                            mUTXOBitcoinFromChange = null;

                                        } else {

                                            //saving UnconfirmedUTXO
                                            int txOutputSize = transaction.getOutputs().size();
                                            Address addressForUnconfirmedUTXO = transaction.getOutput(txOutputSize - 1).getAddressFromP2PKHScript(params);
                                            mUTXOBitcoinFromChange = new UTXOBitcoinFromChange(
                                                    transaction.getHashAsString(),
                                                    txOutputSize - 1,
                                                    transaction.getOutput(txOutputSize - 1).getValue().longValue(),
                                                    addressForUnconfirmedUTXO.toString(),
                                                    System.currentTimeMillis());

                                            //signing inputs
                                            for (int i = 0; i < transaction.getInputs().size(); i++) {
                                                TransactionInput transactionInput1 = transaction.getInput(i);
                                                byte[] privKeyBytes = HEX.decode(SharedPreferencesHelper.getInstance().getStringValue(mUTXOs.get(i).getAddress()));
                                                ECKey ecKey = ECKey.fromPrivate(privKeyBytes);
                                                Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(params, mUTXOs.get(i).getAddress()));
                                                Sha256Hash hash = transaction.hashForSignature(i, scriptPubKey, Transaction.SigHash.SINGLE, true);
                                                ECKey.ECDSASignature ecSig = ecKey.sign(hash);
                                                TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.SINGLE, true);
                                                if (scriptPubKey.isSentToRawPubKey()) {
                                                    transactionInput1.setScriptSig(ScriptBuilder.createInputScript(txSig));
                                                } else {
                                                    if (!scriptPubKey.isSentToAddress()) {
                                                        throw new ScriptException("Don\'t know how to sign for this kind of scriptPubKey: " + scriptPubKey);
                                                    }
                                                    transactionInput1.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
                                                }
                                            }

                                            byte[] bytesRawTransaction2 = transaction.bitcoinSerialize();
                                            String rawTransaction2 = HEX.encode(bytesRawTransaction2);
                                            long sizeOfRawTransaction2 = bytesRawTransaction2.length;

                                            Log.d("rawTransaction2", rawTransaction2);
                                            Log.d("sizeOfRawTransaction2", sizeOfRawTransaction2 + "");

                                            //calculation fee for this transaction with value of feePerKb
                                            Coin fee = Coin.valueOf(feePerKb).multiply(sizeOfRawTransaction2).divide(1000);
                                            //getting change amount without fee
                                            Coin change = transaction.getOutput(1).getValue();
                                            //calculation changeWithoutFee for this transaction
                                            Coin changeWithoutFee = change.subtract(fee);

                                            Log.d("amountToSend", amountToSend + "");
                                            Log.d("changeWithoutFee", changeWithoutFee.longValue() + "");
                                            Log.d("fee", fee.longValue() + "");
                                            Log.d("change", change.longValue() + "");

                                            if (changeWithoutFee.longValue() >= 0) {

                                                Log.d("changeWithoutFee", changeWithoutFee.longValue() + "");
                                                transaction.getOutput(1).setValue(changeWithoutFee);
                                                byte[] bytesRawTransaction3 = transaction.bitcoinSerialize();
                                                String rawTransaction3 = HEX.encode(bytesRawTransaction3);
                                                Log.d("rawTransaction3", rawTransaction3);

//                                                publishProgress("1", "MOCK");
                                                String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 7,\"method\":\"blockchain.transaction.broadcast\",\"params\":[\"" + rawTransaction3 + "\"]}";
                                                mTcpClientBtc.sendMessage(jsonRequest);
                                                done = true;
                                            }
                                        }
                                    } else {
                                        transaction.clearOutputs();
                                    }
                                }
                            }
                            if (!done) {
                                Log.d("YOU HAVE NO ENOUGH MONEY!!!", "YOU HAVE NO ENOUGH MONEY!!!");
                                publishProgress("2");
                            }
                        }
                    } else if (jsonObject.getInt("id") == 7) {

                        Log.d("RESPONSE BROADCAST STARTED", "RESPONSE BROADCAST STARTED");
                        String responseOfBroadcastTransaction = jsonObject.getString("result");
                        Log.d("MESSAGE FROM BROADCAST = ", "" + responseOfBroadcastTransaction);
                        publishProgress("1", responseOfBroadcastTransaction);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    publishProgress("");
                }
            });
            mTcpClientBtc.run();
            return mTcpClientBtc;
        }

        void doProgress() {
            publishProgress("progress");
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            switch (values[0]) {
                case "progress":
                    mFragment.setDownloadProgress(downloadProgressDashboard);
                    mFragment.setTotalProgress(totalProgressDashboard);
                    break;
                case "1":
                    if (values[1].contains("the transaction was rejected by network rules")) {
                        showAlertDialog(mContext, mContext.getString(R.string.the_transaction_was_rejected_by_network_rules));
                        Log.d("REJECT:", values[1]);

                        mTcpClientBtc.stopClient();
                        positionGetListUnspent = 0;
                        utxosValue = 0;
                        done = false;
                        mUTXOs = new ArrayList<>();

                        mFragment.hidePleaseWaitDialog();
                        this.cancel(true);
                    } else {
                        //add list used UTXO to DB
                        Log.d("utxoBitcoinAlreadyUsedList", mUTXOBitcoinAlreadyUsedList.toString());
                        App.getDbInstance().utxoBitcoinAlreadyUsedDao().insertListUTXOBitcoinAlready(mUTXOBitcoinAlreadyUsedList);
                        //add unconfirmed UTXO to DB
                        if (mUTXOBitcoinFromChange != null) {
                            Log.d("mUTXOBitcoinFromChange", mUTXOBitcoinFromChange.toString());
                            App.getDbInstance().utxoBitcoinFromChangeDao().deleteAll();
                            App.getDbInstance().utxoBitcoinFromChangeDao().insertAll(mUTXOBitcoinFromChange);
                        } else {
                            App.getDbInstance().utxoEmercoinFromChangeDao().deleteAll();
                        }

                        Thread t = new Thread(() -> {
                            try {
                                mTcpClientBtc.stopClient();
                                positionGetListUnspent = 0;
                                utxosValue = 0;
                                done = false;
                                mUTXOs = new ArrayList<>();

                                Thread.sleep(3000);
                                mFragment.hidePleaseWaitDialog();
                                mFragment.showSuccessDialog();

                                this.cancel(true);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });

                        t.start();
                    }
                    break;
                case "2":
                    showAlertDialog(mContext, mContext.getString(R.string.insufficient_funds));
                    mFragment.hidePleaseWaitDialog();

                    mTcpClientBtc.stopClient();
                    positionGetListUnspent = 0;
                    utxosValue = 0;
                    done = false;
                    mUTXOs = new ArrayList<>();

                    this.cancel(true);
                    break;
                default:
                    mFragment.hidePleaseWaitDialog();
                    Toast.makeText(mContext, "Exception", Toast.LENGTH_SHORT).show();

                    mTcpClientBtc.stopClient();
                    positionGetListUnspent = 0;
                    utxosValue = 0;
                    done = false;
                    mUTXOs = new ArrayList<>();

                    break;
            }
        }

        @Override
        protected void onPostExecute(TcpClientBtc tcpClientBtc) {
            super.onPostExecute(tcpClientBtc);
            mTcpClientBtc.stopClient();
            positionGetListUnspent = 0;
            utxosValue = 0;
            done = false;
            mUTXOs = new ArrayList<>();
            mFragment.hidePleaseWaitDialog();
        }
    }

    @Override
    public void unsubscribe() {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void getBtcBalance() {

        currency = SharedPreferencesHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        Log.d("CURRENT_CURRENCY", currency);

        balanceBtc = null;
        countBtc = 0;
        satoshiSumBtc = 0;

        downloadProgressDashboard = 0;
        totalProgressDashboard = 25;

        mFragment.showPleaseWaitDialog();
        mFragment.setDownloadProgress(downloadProgressDashboard);
        mFragment.setTotalProgress(totalProgressDashboard);

        if (internetConnectionChecking(mContext)) {
            Log.d("internetConnectionChecking", "true");
            mConnectTaskBtcGetBalance = new ConnectTaskBtcGetBalance();
            mConnectTaskBtcGetBalance.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } else {
            Log.d("internetConnectionChecking", "false");
            mFragment.hidePleaseWaitDialog();
            showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
        }
    }

    private class ConnectTaskBtcGetBalance extends AsyncTask<String, String, TcpClientBtc> {

        @Override
        protected TcpClientBtc doInBackground(String... message) {

            mTcpClientBtc = new TcpClientBtc(response -> {
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

                        if (balanceUnconfirmed.contains("-") | mListBtcAddressesForChange.contains(mListBtcAddresses.get(countBtc))) {
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
            }
            mFragment.hidePleaseWaitDialog();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("onProgressUpdate", "start");

            if (values[0].equals("progress")) {
                mFragment.setDownloadProgress(downloadProgressDashboard);
            } else {
                BigDecimal balanceBtcBigDecimal = new BigDecimal(satoshiSumBtc).divide(new BigDecimal(100000000));
                balanceBtc = String.valueOf(balanceBtcBigDecimal);
                SharedPreferencesHelper.getInstance().putStringValue(BTC_BALANCE_KEY, balanceBtc);

                String courseBtc = SharedPreferencesHelper.getInstance().getStringValue(BTC_COURSE_KEY);
                BigDecimal courseBigDecimal = new BigDecimal(courseBtc);

                BigDecimal balanceBtcInUsdBigDecimal = courseBigDecimal.multiply(balanceBtcBigDecimal);
                SharedPreferencesHelper.getInstance().putStringValue(BTC_BALANCE_IN_USD_KEY,
                        String.valueOf(balanceBtcInUsdBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)));

                Log.d("BALANCE BTC", balanceBtc);
                mTcpClientBtc.stopClient();
                mFragment.hidePleaseWaitDialog();


                try {
                    MainActivity mainActivity = ((MainActivity) ((EnterAnAddressBitcoinFragment) mFragment).getActivity());
                    mainActivity.setCurrentItemNavigationView(1);
                    SendCoinFragment sendCoinFragment = SendCoinFragment.newInstance(1);
                    mainActivity.navigator(sendCoinFragment, sendCoinFragment.getCurrentTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void get20Btc() {

        String btcAddress = mListBtcAddresses.get(countBtc);
        if (mTcpClientBtc != null) {
            downloadProgressDashboard++;
            mConnectTaskBtcGetBalance.doProgress();
            String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 4,\"method\":\"blockchain.address.get_balance\",\"params\":[\"" + btcAddress + "\"]}";
            mTcpClientBtc.sendMessage(jsonRequest);
        }
    }
}
