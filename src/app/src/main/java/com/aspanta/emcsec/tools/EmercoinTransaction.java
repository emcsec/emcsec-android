package com.aspanta.emcsec.tools;

import android.util.Log;

import org.bitcoinj.core.Message;
import org.bitcoinj.core.MessageSerializer;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.VarInt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class EmercoinTransaction extends Transaction {

    private long time;
    private long version;
    private ArrayList<TransactionInput> inputs;
    private ArrayList<TransactionOutput> outputs;

    private long lockTime;
    private int optimalEncodingMessageSize;

    public EmercoinTransaction(NetworkParameters params) {
        super(params);
    }

    public EmercoinTransaction(NetworkParameters params, byte[] payloadBytes) throws ProtocolException {
        super(params, payloadBytes);
    }

    public EmercoinTransaction(NetworkParameters params, byte[] payload, int offset) throws ProtocolException {
        super(params, payload, offset);
    }

    public EmercoinTransaction(NetworkParameters params, byte[] payload, int offset, @Nullable Message parent, MessageSerializer setSerializer, int length) throws ProtocolException {
        super(params, payload, offset, parent, setSerializer, length);
    }

    public EmercoinTransaction(NetworkParameters params, byte[] payload, @Nullable Message parent, MessageSerializer setSerializer, int length) throws ProtocolException {
        super(params, payload, parent, setSerializer, length);
    }

    @Override
    protected void parse() throws ProtocolException {

        Log.d("PARSE", "PARSE");
        cursor = offset;
        version = readUint32();
        time = readUint32();
        optimalEncodingMessageSize = 4;

        // First come the inputs.
        long numInputs = readVarInt();
        optimalEncodingMessageSize += VarInt.sizeOf(numInputs);
        inputs = new ArrayList<>((int) numInputs);
        for (long i = 0; i < numInputs; i++) {
            TransactionInput input = new TransactionInput(params, this, payload, cursor, serializer);
            inputs.add(input);
            long scriptLen = readVarInt(36);
            optimalEncodingMessageSize += 36 + VarInt.sizeOf(scriptLen) + scriptLen + 4;
            cursor += scriptLen + 4;
        }
        // Now the outputs
        long numOutputs = readVarInt();
        optimalEncodingMessageSize += VarInt.sizeOf(numOutputs);
        outputs = new ArrayList<>((int) numOutputs);
        for (long i = 0; i < numOutputs; i++) {
            TransactionOutput output = new TransactionOutput(params, this, payload, cursor, serializer);
            outputs.add(output);
            long scriptLen = readVarInt(8);
            optimalEncodingMessageSize += 8 + VarInt.sizeOf(scriptLen) + scriptLen;
            cursor += scriptLen;
        }
        lockTime = readUint32();
        optimalEncodingMessageSize += 4;
        length = cursor - offset;
    }

    @Override
    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    @Override
    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public long getTime() {
        return time;
    }

    public long getVersion() {
        return version;
    }

    public long getLockTime() {
        return lockTime;
    }

}
