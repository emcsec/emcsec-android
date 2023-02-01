package com.aspanta.emcsec.model.apiTCP;

import android.util.Log;

import com.aspanta.emcsec.db.SPHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static com.aspanta.emcsec.tools.Config.SERVER_HOST_BTC;
import static com.aspanta.emcsec.tools.Config.SERVER_PORT_BTC;

public class TcpClientBtc {

    private String SERVER_IP; //server IP address
    private int SERVER_PORT;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    private Socket socket;
    public String error = "";

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClientBtc(OnMessageReceived listener) {
        mMessageListener = listener;
        SERVER_IP = SPHelper.getInstance().getStringValue(SERVER_HOST_BTC);
        SERVER_PORT = SPHelper.getInstance().getIntValue(SERVER_PORT_BTC);
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
//            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.e("Tcp Client Btc", "C: Connecting...");

            //create a socket to make the connection with the server
//            socket = new Socket(serverAddr, SERVER_PORT);
            socket = new Socket();
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 5000);
            socket.setSoTimeout(20000);

            try {

                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                mBufferOut.println("{\"jsonrpc\":\"2.0\",\"id\": 3,\"method\":\"server.version\",\"params\":[]}");
                mBufferOut.flush();

                //in this while the client listens for the messages sent by the server
                while (mRun) {

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (SocketException se) {

                Log.e("Tcp Client Btc", "S: SocketException", se);

            } catch (SocketTimeoutException ste) {

                Log.e("Tcp Client Btc", "S: SocketTimeoutException", ste);
                error = "connection error";

            } catch (Exception e) {

                Log.e("Tcp Client Btc", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
                Log.e("SOCKET CLOSE", "CLOSE");

            }

        } catch (Exception e) {

            error = "connection error";
            Log.e("Tcp Client Btc", "C: Error", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}


