package com.variable.framework.node;

import android.bluetooth.BluetoothDevice;

import com.variable.framework.bluetooth.utility.BluetoothType;
import com.variable.framework.manager.DeviceManager;
import com.variable.framework.node.interfaces.CommunicationController;

/**
 * Created by coreymann on 6/10/13.
 */
public class AndroidNodeDevice extends NodeDevice {
	private static final DeviceManager<AndroidNodeDevice> MANAGER = new DeviceManager<AndroidNodeDevice>();
	private CommunicationController bluetoothController;
	

    /**
     * Gets or Creates a new instance from the BluetoothDevice object,if  not present in the NodeManager.
     *
     * @param device
     * @return
     */
    public static NodeDevice getOrCreateNodeFromBluetoothDevice(BluetoothDevice device, CommunicationController controller){
        AndroidNodeDevice node = (AndroidNodeDevice) getManager().findFromAddress(device.getAddress());
        if(node == null){
            node = new AndroidNodeDevice(controller, device.getAddress());
            node.setName(device.getName());
            node.setBluetoothController(controller);

            getManager().add(node);
        }else if(!node.getCommunicationController().equals(controller)){
            node.setBluetoothController(controller);
        }

        return node;
    }

    private void setBluetoothController(CommunicationController controller){
    	bluetoothController = controller;
    }
    
    @Override
    public CommunicationController getCommunicationController() { return bluetoothController; } 
    

    public AndroidNodeDevice(CommunicationController portalController, String address) {
        super( address);
        
        bluetoothController = portalController;
    }
    
    @Override
    public boolean sendBytes(byte[] data, int offset, int length){
    	return bluetoothController.sendString(new String(data, offset, length));
    }

    @Override
    public void connect(){
    	bluetoothController.connect(getAddress());
    }

    @Override
    public void disconnect(){
        bluetoothController.disconnect(getAddress());
    }
    
    
    public static DeviceManager<AndroidNodeDevice> getManager(){
    	return MANAGER;
    }
}
