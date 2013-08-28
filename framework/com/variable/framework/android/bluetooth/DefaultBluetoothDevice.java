package com.variable.framework.android.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.variable.framework.node.interfaces.CommunicationController;
/**
* Created by coreymann on 6/10/13.
*/
public class DefaultBluetoothDevice implements CommunicationController {
    public static final int NODE_DEVICE_INIT_COMPLETE = 10;

    private final BluetoothService mService;
    private static final BluetoothAdapter  mAdapter = BluetoothAdapter.getDefaultAdapter();

    public DefaultBluetoothDevice(BluetoothService service){
    	mService = service;
    }

    @Override
    public void connect(String address) {
        mService.connect(address);
    }

    @Override
    public void disconnect(String address) {
        mService.stop();
    }

    @Override
    public boolean sendString(String data) {
        mService.write(data.getBytes());
        return true;
    }

}