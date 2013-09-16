package com.variable.demo.api;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.variable.demo.api.fragment.ClimaFragment;
import com.variable.demo.api.fragment.MainOptionsFragment;
import com.variable.demo.api.fragment.MotionFragment;
import com.variable.demo.api.fragment.OxaFragment;
import com.variable.demo.api.fragment.ThermaFragment;
import com.variable.demo.api.fragment.ThermoCoupleFragment;
import com.variable.framework.android.bluetooth.BluetoothService;
import com.variable.framework.android.bluetooth.DefaultBluetoothDevice;
import com.variable.framework.dispatcher.DefaultNotifier;
import com.variable.framework.node.AndroidNodeDevice;
import com.variable.framework.node.DataLogSetting;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.adapter.StatusAdapter;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    private static BluetoothService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Init Bluetooth Stuff
        ensureBluetoothIsOn();
        mService = new BluetoothService(mHandler);
        NodeApplication.setServiceAPI(mService);


    }


    @Override
    public void onResume(){
        super.onResume();

        ensureBluetoothIsOn();

        //Start Options Fragment
        Fragment frag = new MainOptionsFragment().setOnClickListener(this);
        animateToFragment(frag, MainOptionsFragment.TAG);
    }

    @Override
    public void onPause(){
        super.onResume();

        NodeDevice node = ((NodeApplication) getApplication()).getActiveNode();
        if(node != null){
            node.disconnect(); //Clean up after ourselves.
        }

        while(getSupportFragmentManager().popBackStackImmediate()) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onConnected(final NodeDevice node)
    {

        //Set the NODE as connected and its connection status as connecting.
        node.isConnected(true);
        node.setConnectionStatus(BluetoothService.STATE_CONNECTING);

        //Issuing Initialization Commands
        node.requestKoreConfiguration();
        node.requestSerial();
        node.requestVersion();
        node.requestQuietModeStatus();

        //Requesting Datalog Information.
        DataLogSetting dlogSettings = node.getDatalogSettings();
        dlogSettings.requestPeriod();
        dlogSettings.requestIsDataloggingEnabled();
        dlogSettings.requestState();
        dlogSettings.requestFreeMemorySpace();

        node.requestModuleVersions();
        node.requestStatus();
        node.requestModuleSubtypes(); //By issuing model version last we will know that all the commands have been recieved and initialized when we recieve the model subtypes.



        //Listen for the Model Sub Types
        final StatusAdapter statusListener =  new StatusAdapter() {
            @Override
            public void onSubModulesUpdate(NodeDevice node){
                Log.d(TAG, "onSubModuleUpdate() " + node.getSubModuleA() + " [" + node.getSubModuleA() + "] , " + node.getModuleB() + " [" + node.getSubModuleB() + "]");
                DefaultNotifier.instance().removeStatusListener(this);
                node.setConnectionStatus(BluetoothService.STATE_CONNECTED);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.obtainMessage(DefaultBluetoothDevice.NODE_DEVICE_INIT_COMPLETE).sendToTarget();
                    }
                });
            }

            @Override
            public void onModulesUpdate(NodeDevice device) {
                Log.d(TAG, "onModuleUpdate() " + node.getSubModuleA() + "  , " + node.getModuleB());
            }
        };

        //Register for the event.
        DefaultNotifier.instance().addStatusListener(statusListener);


        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //Check if this NODE is connected successfully.
                if(node.getConnectionStatus() != BluetoothService.STATE_CONNECTED){
                    DefaultNotifier.instance().removeStatusListener(statusListener);
                    onCommunicationInitFailed(node);
                }
            }
        }, 2500);

    }

    /**
     * Signifies that NODE is ready for communication.
     * @param node
     */
    public void onCommunicationInitCompleted(NodeDevice node)
    {
        Toast.makeText(this, node.getName() + " is now ready for use.", Toast.LENGTH_SHORT);
    }

    public void onDisconnect(NodeDevice node)
    {
        Toast.makeText(this, node.getName() + " disconnected.", Toast.LENGTH_SHORT);
    }

    public void onCommunicationInitFailed(NodeDevice node)
    {
        Toast.makeText(this, node.getName() + " failed initialization.", Toast.LENGTH_SHORT);
    }

    /**
     * Invokes a new intent to request to start the bluetooth, if not already on.
     */
    private void ensureBluetoothIsOn(){
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(btIntent);
        }
    }

    /**
     * Checks if a fragment with the specified tag exists already in the Fragment Manager. If present, then removes fragment.
     *
     * Animates out to the specified fragment.
     *
     *
     * @param frag
     * @param tag
     */
    public void animateToFragment(final Fragment frag, final String tag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment existingFrag = getSupportFragmentManager().findFragmentByTag(tag);
        if(existingFrag != null){
            getSupportFragmentManager().beginTransaction().remove(existingFrag).commit();
        }

        ft.replace(R.id.center_fragment_container, frag, tag);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnMotion:
                animateToFragment(new MotionFragment(), MotionFragment.TAG);
                break;

            case R.id.btnClima:
                animateToFragment(new ClimaFragment(), ClimaFragment.TAG);
                break;

            case R.id.btnTherma:
                animateToFragment(new ThermaFragment(), ThermaFragment.TAG);
                break;

            case R.id.btnOxa:
                animateToFragment(new OxaFragment(), OxaFragment.TAG);
                break;

            case R.id.btnThermoCouple:
                animateToFragment(new ThermoCoupleFragment(), ThermoCoupleFragment.TAG);
                break;
        }
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        private ProgressDialog mProgressDialog;

        @Override
        public void handleMessage(Message msg) {
            NodeDevice node = ((NodeApplication) getApplication()).getActiveNode();
            switch (msg.what) {
                case BluetoothService.MESSAGE_DEVICE_ADDRESS:
                    //Retrieve the active
                    String address = msg.getData().getString(BluetoothService.DEVICE_ADDRESS);
                    node = AndroidNodeDevice.getManager().findFromAddress(address);
                    ((NodeApplication) getApplication()).setActiveNode(node);
                    break;

                case BluetoothService.MESSAGE_STATE_CHANGE:
                    String address2 = msg.getData().getString(BluetoothService.DEVICE_ADDRESS);
                    if(address2 != null){
                        node            = AndroidNodeDevice.getManager().findFromAddress(address2);
                    }
                    switch(msg.arg1){
                        case BluetoothService.STATE_CONNECTING:

                            if(mProgressDialog == null){
                                mProgressDialog = new ProgressDialog(MainActivity.this);
                            }else{
                                closeDialog(mProgressDialog);
                            }

                            buildDialog(node, mProgressDialog, "Connecting...");

                            break;

                        case  BluetoothService.STATE_CONNECTED:


                            if(mProgressDialog == null){
                                mProgressDialog = new ProgressDialog(MainActivity.this);
                            }else{
                                closeDialog(mProgressDialog);
                            }
                            buildDialog(node, mProgressDialog, "Initializing...");

                            //Log.d(TAG, "Connected Message Recieved for " + ((BaseApplication) getApplication()).getActiveNode().getName());
                            onConnected(node);

                            break;
                        case BluetoothService.STATE_DISCONNECTED:

                            closeDialog(mProgressDialog);

                            //Log.d(TAG, node.getName() + " is now disconnected");
                            onDisconnect(node);

                            break;

                    }

                    break;

                //Handle Node Successfull initialized.
                case DefaultBluetoothDevice.NODE_DEVICE_INIT_COMPLETE:
                    Log.d(TAG, "NodeDevice Init Completed in Handler");
                    closeDialog(mProgressDialog);
                    onCommunicationInitCompleted(node);
                    break;
            }
        }


        private final void closeDialog(ProgressDialog progressDialog){
            mProgressDialog.dismiss();
            mProgressDialog = new ProgressDialog(MainActivity.this);
        }

        private final void buildDialog(final NodeDevice node, ProgressDialog progressDialog, String message){
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    node.disconnect();
                }
            });

            mProgressDialog.setTitle("Bluetooth Connection");
            mProgressDialog.setMessage(message);

            if(!mProgressDialog.isShowing()) { mProgressDialog.show(); }
        }
    };


}
