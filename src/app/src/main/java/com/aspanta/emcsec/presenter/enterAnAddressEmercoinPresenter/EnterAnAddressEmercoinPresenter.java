package com.aspanta.emcsec.presenter.enterAnAddressEmercoinPresenter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.SPHelper;
import com.aspanta.emcsec.db.room.EmcAddress;
import com.aspanta.emcsec.db.room.EmcAddressForChange;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOEmercoinAlreadyUsed;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOEmercoinFromChange;
import com.aspanta.emcsec.model.apiTCP.TcpClientEmc;
import com.aspanta.emcsec.tools.EmercoinNetworkPeerCoin;
import com.aspanta.emcsec.tools.UTXOEmercoin;
import com.aspanta.emcsec.ui.activities.MainActivity;
import com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment.EnterAnAddressEmercoinFragment;
import com.aspanta.emcsec.ui.fragment.enterAnAddressEmercoinFragment.IEnterAnAddressEmercoinFragment;
import com.aspanta.emcsec.ui.fragment.sendCoinFragment.SendCoinFragment;
import com.matthewmitchell.peercoinj.core.Address;
import com.matthewmitchell.peercoinj.core.AddressFormatException;
import com.matthewmitchell.peercoinj.core.Coin;
import com.matthewmitchell.peercoinj.core.ECKey;
import com.matthewmitchell.peercoinj.core.NetworkParameters;
import com.matthewmitchell.peercoinj.core.ScriptException;
import com.matthewmitchell.peercoinj.core.Sha256Hash;
import com.matthewmitchell.peercoinj.core.Transaction;
import com.matthewmitchell.peercoinj.core.TransactionInput;
import com.matthewmitchell.peercoinj.core.TransactionOutPoint;
import com.matthewmitchell.peercoinj.crypto.TransactionSignature;
import com.matthewmitchell.peercoinj.script.Script;
import com.matthewmitchell.peercoinj.script.ScriptBuilder;

import org.bitcoinj.core.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aspanta.emcsec.tools.Config.CHANGE_ADDRESS_EMC;
import static com.aspanta.emcsec.tools.Config.CURRENT_CURRENCY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_IN_USD_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_BALANCE_KEY;
import static com.aspanta.emcsec.tools.Config.EMC_EXCHANGE_RATE_KEY;
import static com.aspanta.emcsec.tools.Config.METHOD_GET_BALANCE;
import static com.aspanta.emcsec.tools.Config.METHOD_LISTUNSPENT;
import static com.aspanta.emcsec.tools.JsonRpcHelper.createRpcRequest;
import static com.aspanta.emcsec.ui.activities.MainActivity.showAlertDialog;
import static com.aspanta.emcsec.tools.InternetConnection.internetConnectionChecking;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.downloadProgressDashboard;
import static com.aspanta.emcsec.ui.fragment.dashboardFragment.DashboardFragment.totalProgressDashboard;
import static com.matthewmitchell.peercoinj.core.Utils.HEX;


public class EnterAnAddressEmercoinPresenter implements IEnterAnAddressEmercoinPresenter {

    private IEnterAnAddressEmercoinFragment mFragment;
    private Context mContext;

    //fields for getting balance
    private String balanceEmc;
    private int countEmc;
    private long satoshiSumEmc;
    private String currency;

    //fields for sending emc
    private NetworkParameters params = EmercoinNetworkPeerCoin.get();
    private TcpClientEmc mTcpClientEmc;

    private List<String> mListEmcAddresses = new ArrayList<>();
    private List<UTXOEmercoin> mUTXOs = new ArrayList<>();
    List<UTXOEmercoinAlreadyUsed> utxoEmercoinAlreadyUsedList;
    private UTXOEmercoinFromChange mUTXOEmercoinFromChange;
    private List<UTXOEmercoinFromChange> utxoEmercoinFromChangeList;
    private List<String> mListEmcAddressesForChange = new ArrayList<>();

    private int positionGetListUnspent = 0;
    private long utxosValue;
    private long amountToSend;
    private long fee = 100;
    private Transaction transaction;
    private boolean done;
    private String addressToSend;
    private String addressForChange;

    private ConnectTaskEmc mConnectTaskEmc;
    private ConnectTaskEmcGetBalance mConnectTaskEmcGetBalance;

    public EnterAnAddressEmercoinPresenter(Context context, IEnterAnAddressEmercoinFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public void sendEmercoin(String address, String amountToSend) {

        downloadProgressDashboard = 0;
        totalProgressDashboard = 25;

        utxoEmercoinAlreadyUsedList = new ArrayList<>();
        utxoEmercoinAlreadyUsedList = App.getDbInstance().utxoEmercoinAlreadyUsedDao().getAll();

        String dbUTXOsAlreadyUsedChangeOrNot = "";
        for (int i = 0; i < utxoEmercoinAlreadyUsedList.size(); i++) {
            long timeToRemove = utxoEmercoinAlreadyUsedList.get(i).getTimeToRemove();
            if (System.currentTimeMillis() - timeToRemove > 604800000) {
                utxoEmercoinAlreadyUsedList.remove(i);
                i--;
                dbUTXOsAlreadyUsedChangeOrNot = "change";
            }
        }
        if (dbUTXOsAlreadyUsedChangeOrNot.equals("change")) {
            App.getDbInstance().utxoEmercoinAlreadyUsedDao().deleteAll();
            App.getDbInstance().utxoEmercoinAlreadyUsedDao().insertListUTXOEmercoinAlready(utxoEmercoinAlreadyUsedList);
        } else {
            Log.d("DB have no changes", utxoEmercoinAlreadyUsedList.toString());
        }

        utxoEmercoinFromChangeList = new ArrayList<>();
        utxoEmercoinFromChangeList = App.getDbInstance().utxoEmercoinFromChangeDao().getAll();

        String dbUTXOsFromChangeChangeOrNot = "";
        for (int i = 0; i < utxoEmercoinFromChangeList.size(); i++) {
            long timeToRemove = utxoEmercoinFromChangeList.get(i).getTimeToRemove();
            if (System.currentTimeMillis() - timeToRemove > 86400) {
                utxoEmercoinFromChangeList.remove(i);
                i--;
                dbUTXOsFromChangeChangeOrNot = "change";
            }
        }
        if (dbUTXOsFromChangeChangeOrNot.equals("change")) {
            App.getDbInstance().utxoEmercoinFromChangeDao().deleteAll();
            App.getDbInstance().utxoEmercoinFromChangeDao().insertListUTXOEmercoinFromChange(utxoEmercoinFromChangeList);
        } else {
            Log.d("DB have no changes", utxoEmercoinFromChangeList.toString());
        }

        this.addressToSend = address;
        this.amountToSend = (long) (Double.valueOf(amountToSend) * 1000000);

        String balance = SPHelper.getInstance().getStringValue(EMC_BALANCE_KEY);
        if (balance != null && !balance.equals("") && !balance.equals("?")) {
            long balanceLong = (long) (Double.valueOf(balance) * 1000000);
            if (balanceLong <= this.amountToSend) {
                showAlertDialog(mContext, mContext.getString(R.string.insufficient_funds));
                return;
            }
        }

        addressForChange = SPHelper.getInstance().getStringValue(CHANGE_ADDRESS_EMC);
        mFragment.showPleaseWaitDialog();

        for (EmcAddress emcAddress : App.getDbInstance().emcAddressDao().getAll()) {
            mListEmcAddresses.add(emcAddress.getAddress());
        }

        for (EmcAddressForChange emcAddressForChange : App.getDbInstance().emcAddressForChangeDao().getAll()) {
            mListEmcAddresses.add(emcAddressForChange.getAddress());
            mListEmcAddressesForChange.add(emcAddressForChange.getAddress());
        }

        transaction = new Transaction(params);
        mConnectTaskEmc = new ConnectTaskEmc();
        mConnectTaskEmc.execute("");
    }

    private void getListUnspent() {
        if (positionGetListUnspent < 25) {
            String address = mListEmcAddresses.get(positionGetListUnspent);
            downloadProgressDashboard++;
            mConnectTaskEmc.doProgress();

            String jsonRequest = createRpcRequest(2, METHOD_LISTUNSPENT, address);
            mTcpClientEmc.sendMessage(jsonRequest);
        }
    }

    private class ConnectTaskEmc extends AsyncTask<String, String, TcpClientEmc> {

        @SuppressLint("LongLogTag")
        @Override
        protected TcpClientEmc doInBackground(String... message) {

            mTcpClientEmc = new TcpClientEmc(response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("id") == 1) {
                        getListUnspent();
                    } else if (jsonObject.getInt("id") == 2) {

                        Log.d("JSON GET LIST UNSPENT", jsonObject.toString());

                        JSONArray getUnspentJSONArray = jsonObject.getJSONArray("result");
                        Log.d("getUnspentJSONArray", getUnspentJSONArray.toString());

                        int lengthOfUnspentArray = getUnspentJSONArray.length();
                        Log.d("lengthOfUnspentArray", lengthOfUnspentArray + "");

                        for (int i = 0; i < lengthOfUnspentArray; i++) {

                            JSONObject jsonObject1 = getUnspentJSONArray.getJSONObject(i);
                            Log.d("jsonObject1", jsonObject1.toString());

                            String address = mListEmcAddresses.get(positionGetListUnspent);
                            Address address1 = null; // creating an address for UTXOEmercoin
                            try {
                                address1 = new Address(params, address);
                            } catch (AddressFormatException e) {
                                e.printStackTrace();
                            }

                            Sha256Hash sha256Hash = new Sha256Hash(Utils.HEX.decode(jsonObject1.getString("tx_hash")));
                            Script scriptForUTXO = ScriptBuilder.createOutputScript(address1); // creating script for UTXOEmercoin
                            Coin coinValue = Coin.valueOf(jsonObject1.getLong("value"));
                            mUTXOs.add(new UTXOEmercoin(sha256Hash, jsonObject1.getInt("tx_pos"), coinValue, jsonObject1.getInt("height"), false, scriptForUTXO, address));
                        }

                        positionGetListUnspent++;
                        getListUnspent();

                        if (positionGetListUnspent == 25) {

                            Collections.sort(mUTXOs);
                            Collections.reverse(mUTXOs);

                            Log.d("utxoEmercoinAlreadyUsedList", utxoEmercoinAlreadyUsedList.toString());

                            List<UTXOEmercoin> utxoEmercoinListLocal = new ArrayList<>();
                            for (UTXOEmercoinAlreadyUsed utxoEmercoinAlreadyUsed : utxoEmercoinAlreadyUsedList) {
                                Address address1 = null; // creating an address for UTXOEmercoin
                                try {
                                    address1 = new Address(params, utxoEmercoinAlreadyUsed.getAddress());
                                } catch (AddressFormatException e) {
                                    e.printStackTrace();
                                }
                                Sha256Hash sha256Hash = new Sha256Hash(Utils.HEX.decode(utxoEmercoinAlreadyUsed.getTx_id()));
                                Script scriptForUTXO = ScriptBuilder.createOutputScript(address1); // creating script for UTXOEmercoin
                                Coin coinValue = Coin.valueOf(utxoEmercoinAlreadyUsed.getValue());
                                utxoEmercoinListLocal.add(new UTXOEmercoin(sha256Hash, utxoEmercoinAlreadyUsed.getIndex(), coinValue, 0, false, scriptForUTXO, address1.toString()));
                            }

                            utxoEmercoinAlreadyUsedList.clear();

                            Log.d("UTXOs before removing utxoEmercoinListLocal", mUTXOs.toString());
                            mUTXOs.removeAll(utxoEmercoinListLocal);
                            Log.d("UTXOs before after utxoEmercoinListLocal", mUTXOs.toString());

                            for (UTXOEmercoinFromChange utxoEmercoinFromChange : utxoEmercoinFromChangeList) {
                                Address address1 = null; // creating an address for UTXOEmercoin
                                try {
                                    address1 = new Address(params, utxoEmercoinFromChange.getAddress());
                                } catch (AddressFormatException e) {
                                    e.printStackTrace();
                                }
                                Sha256Hash sha256Hash = new Sha256Hash(Utils.HEX.decode(utxoEmercoinFromChange.getTx_id()));
                                Script scriptForUTXO = ScriptBuilder.createOutputScript(address1); // creating script for UTXOEmercoin
                                Coin coinValue = Coin.valueOf(utxoEmercoinFromChange.getValue());
                                mUTXOs.add(0, new UTXOEmercoin(sha256Hash, utxoEmercoinFromChange.getIndex(), coinValue, 0, false, scriptForUTXO, address1.toString()));
                            }

                            Log.d("UTXOS LIST", mUTXOs.toString());

                            for (UTXOEmercoin utxo : mUTXOs) {

                                if (!done) {
                                    utxosValue += utxo.getValue().longValue();
                                    Log.d("utxosValue on start of for each  ", utxosValue + "");

                                    /*Formation an outpoint with position of utxo in previous transaction
                                     *and with hash of previous transaction.
                                     *Signing this outpoint with privKey and formation a signed input.
                                     */

                                    utxoEmercoinAlreadyUsedList.add(new UTXOEmercoinAlreadyUsed(
                                            utxo.getHash().toString(),
                                            utxo.getIndex(),
                                            utxo.getValue().longValue(),
                                            utxo.getAddress(),
                                            System.currentTimeMillis()
                                    ));

                                    TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
                                    TransactionInput transactionInput = new TransactionInput(params, transaction, new byte[0], outPoint);
                                    transaction.addInput(transactionInput);

                                    if (utxosValue > (amountToSend + fee)) {

                                        //adding an output with amount for sending and an address of recipient
                                        try {
                                            transaction.addOutput(Coin.valueOf(amountToSend),
                                                    new Address(params, addressToSend));
                                        } catch (AddressFormatException e) {
                                            e.printStackTrace();
                                        }

                                        if (utxosValue - (amountToSend + fee) >= 10000) {

                                            transaction.clearOutputs();

                                            //adding an output with amount for sending and an address of recipient
                                            try {
                                                transaction.addOutput(Coin.valueOf(amountToSend),
                                                        new Address(params, addressToSend));
                                            } catch (AddressFormatException e) {
                                                e.printStackTrace();
                                            }

                                            //adding an output with value for change without fee and an address for change
                                            try {
                                                transaction.addOutput(Coin.valueOf(utxosValue - (amountToSend + fee)),
                                                        new Address(params, addressForChange));
                                            } catch (AddressFormatException e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            long newFee = utxosValue - amountToSend;
                                            Log.d("utxosValue", utxosValue + "");
                                            Log.d("newFee", newFee + "");
                                            Log.d("amountToSend", amountToSend + "");
                                        }

                                        for (int i = 0; i < transaction.getInputs().size(); i++) {
                                            TransactionInput transactionInput1 = transaction.getInput(i);
                                            byte[] privKeyBytes = HEX.decode(SPHelper.getInstance().getStringValue(mUTXOs.get(i).getAddress()));
                                            ECKey ecKey = ECKey.fromPrivate(privKeyBytes);

                                            Script scriptPubKey = null;
                                            try {
                                                scriptPubKey = ScriptBuilder.createOutputScript(new Address(params, mUTXOs.get(i).getAddress()));
                                            } catch (AddressFormatException e) {
                                                e.printStackTrace();
                                            }

                                            Sha256Hash hash = transaction.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false);
                                            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
                                            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
                                            if (scriptPubKey.isSentToRawPubKey()) {
                                                transactionInput1.setScriptSig(ScriptBuilder.createInputScript(txSig));
                                            } else {
                                                if (!scriptPubKey.isSentToAddress()) {
                                                    throw new ScriptException("Don\'t know how to sign for this kind of scriptPubKey: " + scriptPubKey);
                                                }
                                                transactionInput1.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
                                            }
                                        }

                                        //serialization of the transaction and getting raw transaction
                                        byte[] bytesRawTransaction = transaction.peercoinSerialize();
                                        String rawTransaction = HEX.encode(bytesRawTransaction);
                                        Log.d("rawTransaction", rawTransaction);

                                        if (transaction.getOutputs().size() >= 2) {

                                            int txOutputSize = transaction.getOutputs().size();
                                            Address addressForUnconfirmedUTXO = transaction.getOutput(txOutputSize - 1).getAddressFromP2PKHScript(params);
                                            mUTXOEmercoinFromChange = new UTXOEmercoinFromChange(
                                                    transaction.getHashAsString(),
                                                    txOutputSize - 1,
                                                    transaction.getOutput(txOutputSize - 1).getValue().longValue(),
                                                    addressForUnconfirmedUTXO.toString(),
                                                    System.currentTimeMillis());
                                        } else {
                                            mUTXOEmercoinFromChange = null;
                                        }

                                        String jsonRequest = "{\"jsonrpc\":\"2.0\",\"id\": 3,\"method\":\"blockchain.transaction.broadcast\",\"params\":[\"" + rawTransaction + "\"]}";
                                        mTcpClientEmc.sendMessage(jsonRequest);
//                                        publishProgress("1", "MOCK");

                                        done = true;
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
                    } else if (jsonObject.getInt("id") == 3) {

                        Log.d("RESPONSE BROADCAST STARTED", "RESPONSE BROADCAST STARTED");
                        String responseOfBroadcastTransaction = jsonObject.getString("result");
                        Log.d("MESSAGE FROM BROADCAST = ", "" + responseOfBroadcastTransaction);
                        publishProgress("1", responseOfBroadcastTransaction);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    publishProgress("");
                }
            }, mContext);
            mTcpClientEmc.run();
            return mTcpClientEmc;
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

                        mTcpClientEmc.stopClient();
                        positionGetListUnspent = 0;
                        utxosValue = 0;
                        done = false;
                        mUTXOs = new ArrayList<>();

                        mFragment.hidePleaseWaitDialog();
                        this.cancel(true);

                    } else {
                        //add list used UTXO to DB
                        Log.d("utxoEmercoinAlreadyUsedList", utxoEmercoinAlreadyUsedList.toString());
                        App.getDbInstance().utxoEmercoinAlreadyUsedDao().insertListUTXOEmercoinAlready(utxoEmercoinAlreadyUsedList);
                        //add unconfirmed UTXO to DB
                        if (mUTXOEmercoinFromChange != null) {
                            Log.d("mUTXOEmercoinFromChange", mUTXOEmercoinFromChange.toString());
                            App.getDbInstance().utxoEmercoinFromChangeDao().deleteAll();
                            App.getDbInstance().utxoEmercoinFromChangeDao().insertAll(mUTXOEmercoinFromChange);
                        } else {
                            App.getDbInstance().utxoEmercoinFromChangeDao().deleteAll();
                        }

                        Thread t = new Thread(() -> {
                            try {
                                Thread.sleep(3000);

                                mTcpClientEmc.stopClient();
                                positionGetListUnspent = 0;
                                utxosValue = 0;
                                done = false;
                                mUTXOs = new ArrayList<>();

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

                    mTcpClientEmc.stopClient();
                    positionGetListUnspent = 0;
                    utxosValue = 0;
                    done = false;
                    mUTXOs = new ArrayList<>();

                    this.cancel(true);

                    break;
                default:
                    mFragment.hidePleaseWaitDialog();
                    Toast.makeText(mContext, "Exception", Toast.LENGTH_SHORT).show();

                    mTcpClientEmc.stopClient();
                    positionGetListUnspent = 0;
                    utxosValue = 0;
                    done = false;
                    mUTXOs = new ArrayList<>();

                    this.cancel(true);
                    break;
            }
        }

        @Override
        protected void onPostExecute(TcpClientEmc tcpClientEmc) {
            super.onPostExecute(tcpClientEmc);
            mTcpClientEmc.stopClient();
            positionGetListUnspent = 0;
            utxosValue = 0;
            done = false;
            mUTXOs = new ArrayList<>();
        }
    }

    @Override
    public void unsubscribe() {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void getEmcBalance() {

        currency = SPHelper.getInstance().getStringValue(CURRENT_CURRENCY);
        Log.d("CURRENT_CURRENCY", currency);

        balanceEmc = null;
        countEmc = 0;
        satoshiSumEmc = 0;

        downloadProgressDashboard = 0;
        totalProgressDashboard = 25;

        mFragment.showPleaseWaitDialog();
        mFragment.setDownloadProgress(downloadProgressDashboard);
        mFragment.setTotalProgress(totalProgressDashboard);

        if (internetConnectionChecking(mContext)) {
            Log.d("internetConnectionChecking", "true");
            mConnectTaskEmcGetBalance = new ConnectTaskEmcGetBalance();
            mConnectTaskEmcGetBalance.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } else {
            Log.d("internetConnectionChecking", "false");
            mFragment.hidePleaseWaitDialog();
            showAlertDialog(mContext, mContext.getString(R.string.could_not_connect));
        }
    }

    private class ConnectTaskEmcGetBalance extends AsyncTask<String, String, TcpClientEmc> {

        @Override
        protected TcpClientEmc doInBackground(String... message) {

            mTcpClientEmc = new TcpClientEmc(response -> {
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

                        if (balanceUnconfirmed.contains("-") | mListEmcAddressesForChange.contains(mListEmcAddresses.get(countEmc))) {
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
            mFragment.hidePleaseWaitDialog();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("onProgressUpdate", "start");

            if (values[0].equals("progress")) {
                mFragment.setDownloadProgress(downloadProgressDashboard);
            } else {
                BigDecimal balanceEmcBigDecimal = new BigDecimal(satoshiSumEmc).divide(new BigDecimal(1000000));
                balanceEmc = String.valueOf(balanceEmcBigDecimal);
                SPHelper.getInstance().putStringValue(EMC_BALANCE_KEY, balanceEmc);

                String courseEmc = SPHelper.getInstance().getStringValue(EMC_EXCHANGE_RATE_KEY);
                BigDecimal courseBigDecimal = new BigDecimal(courseEmc);

                BigDecimal balanceEmcInUsdBigDecimal = courseBigDecimal.multiply(balanceEmcBigDecimal);
                SPHelper.getInstance().putStringValue(EMC_BALANCE_IN_USD_KEY,
                        String.valueOf(balanceEmcInUsdBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)));

                Log.d("BALANCE EMC", balanceEmc);
                mTcpClientEmc.stopClient();
                mFragment.hidePleaseWaitDialog();

                try {
                    MainActivity mainActivity = ((MainActivity) ((EnterAnAddressEmercoinFragment) mFragment).getActivity());
                    mainActivity.setCurrentItemNavigationView(1);
                    SendCoinFragment sendCoinFragment = SendCoinFragment.newInstance(0);
                    mainActivity.navigator(sendCoinFragment, sendCoinFragment.getCurrentTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void get20Emc() {

        String emcAddress = mListEmcAddresses.get(countEmc);
        if (mTcpClientEmc != null) {
            downloadProgressDashboard++;
            mConnectTaskEmcGetBalance.doProgress();
            String jsonRequest = createRpcRequest(2, METHOD_GET_BALANCE, emcAddress);
            mTcpClientEmc.sendMessage(jsonRequest);
        }
    }
}


