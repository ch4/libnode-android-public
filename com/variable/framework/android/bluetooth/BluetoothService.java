/*
* Copyright (C) 2009 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.variable.framework.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.variable.framework.node.AndroidNodeDevice;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.interfaces.CommunicationController;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
* This class does all the work for setting up and managing Bluetooth
* connections with other devices. It has a thread that listens for
* incoming connections, a thread for connecting with a device, and a
* thread for performing data transmissions when connected.
*/
public class BluetoothService{
    // Debugging
    private static final String TAG = "BluetoothReadService";
    private static final boolean D = true;


	private static final UUID BluetoothClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;

    public Handler setHandler(Handler h){
        synchronized (mHandler){
            mHandler = h;
        }
        return mHandler;
    }
    public Handler getHandler() {
        return mHandler;
    }

    private     Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;


    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    // --Commented out by Inspection (6/26/13 4:07 PM):public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING   = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED    = 3;  // now connected to a remote device
    public static final int STATE_DISCONNECTED = 4;

    //public static final int MESSAGE_DEVICE_NOT_PAIRED = 3;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_ADDRESS  = 2;
    public static final String DEVICE_ADDRESS = "com.variable.android.actions.DEVICE_ADDRESS";


    //Constants for bundle extras
    public static final String EXTRA_DEVICE = "com.variable.android.intent.actions.EXTRA_DEVICE";
    public static final String DISCOVERED_DEVICES_LIST = "com.variable.android.intent.action.DISCOVERED_DEVICES_LIST";

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothService(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(String address, int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        Message msg = mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1);
        msg.getData().putString(DEVICE_ADDRESS, address);
        msg.sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
        	mConnectThread.cancel();
        	mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }

        setState(mAdapter.getAddress(), STATE_NONE);
    }

    public int connectionCount(){
        return mConnectedThread == null ? 0 : 1;
    }
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);



        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(device.getAddress(), STATE_CONNECTING);
    }

    public synchronized void connect(String address){
        if(BluetoothAdapter.checkBluetoothAddress(address))
            connect(mAdapter.getRemoteDevice(address));
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "==========connected=========");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
        	mConnectThread.cancel();
        	mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, new DefaultBluetoothDevice(this));
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_ADDRESS);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_ADDRESS, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(device.getAddress(), STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "========stop=======");


        if (mConnectThread != null) {
        	mConnectThread.cancel();
        	mConnectThread = null;
        }

        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }

        setState(mAdapter.getAddress(), STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed(String address) {
        //TODO: Send a Connect Failed
        setState(address, STATE_DISCONNECTED);


    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost(String address) {
        setState(address, STATE_DISCONNECTED);
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;

            BluetoothSocket tmp = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                Log.d(TAG, "============Using Native Bluetooth==========");
                tmp = getSocketUsingAPI();
            }else{
                Log.d(TAG, "============Using Reflections for Bluetooth Socket========");
                tmp = getSocketUsingReflections();
            }

            mmSocket = tmp;
        }
        private final BluetoothSocket getSocketUsingAPI(){
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            BluetoothSocket tmp = null;
            try {
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(BluetoothClass_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            return tmp;
        }

        private final BluetoothSocket getSocketUsingReflections(){
            BluetoothSocket tmp = null;
            try {
                tmp  = edu.stanford.mobisocial.bluetooth.InsecureBluetooth.createRfcommSocketToServiceRecord(mmDevice, BluetoothService.BluetoothClass_UUID, true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception

                    mmSocket.connect();
                } catch (IOException e) {
                        connectionFailed(mmSocket.getRemoteDevice().getAddress());


                    // Close the socket
                    try {
                        mmSocket.close();
                    } catch (IOException e2) {
                        Log.e(TAG, "unable to close() socket during connection failure", e2);
                    }
                    // Start the service over to restart listening mode
                    //BluetoothSerialService.this.start();
                    return;
            }

             // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }



            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {    mmSocket.close();  }
            catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
  
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BufferedInputStream mmBuffInStream;
        private final OutputStream mmOutStream;
        private final NodeDevice mmNode;

        public ConnectedThread(BluetoothSocket socket, CommunicationController device) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            
            mmBuffInStream = new BufferedInputStream(tmpIn);
            mmOutStream = tmpOut;
            mmNode = AndroidNodeDevice.getOrCreateNodeFromBluetoothDevice(mmSocket.getRemoteDevice(), device);
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            final byte[] buffer = new byte[4096];

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    final int bytes = mmBuffInStream.read(buffer);
                    
                    for(int i=0; i < bytes; i++){
                    	mmNode.processByte(buffer[i]);
                    }
                    
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost(mmSocket.getRemoteDevice().getAddress());
                    break;
                }
            }
        }

     //   private final ExecutorService mService = Executors.newFixedThreadPool(5);
        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
            	
            	Thread.sleep(5);
            	
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                // mHandler.obtainMessage(BlueTerm.MESSAGE_WRITE, buffer.length, -1, buffer)
                //         .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
