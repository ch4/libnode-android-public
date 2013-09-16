package com.variable.demo.api.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.variable.demo.api.MessageConstants;
import com.variable.demo.api.NodeApplication;
import com.variable.demo.api.R;
import com.variable.framework.dispatcher.DefaultNotifier;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.interfaces.INode;

import java.text.DecimalFormat;

/**
 * Created by coreymann on 9/16/13.
 */
public class ThermoCoupleFragment extends Fragment implements INode.ThermaCoupleListener {
    public static final String TAG = ThermoCoupleFragment.class.getName();
    private TextView temperatureText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.thermocouple, null, false);
        temperatureText = (TextView) root.findViewById(R.id.txtThermoCoupleReading);

        return root;
    }


    @Override
    public void onPause() {
        super.onPause();

        //Unregister for thermoCouple event.
        DefaultNotifier.instance().removeThermaCoupleListener(this);

        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null)
        {
            node.setStreamModeThermocouple(false, (short) 0 , (short) 0); //Turns streaming and the ir off.
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Register for ThermoCouple Event
        DefaultNotifier.instance().addThermaCoupleListener(this);
        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null)
        {
           node.setStreamModeThermocouple(true, (short) 0, (short) 0);
        }
    }

    @Override
    public void onThermoCoupleReading(NodeDevice nodeDevice, Float reading) {
        Message m = mHandler.obtainMessage(MessageConstants.MESSAGE_THERMA_TEMPERATURE);
        m.getData().putFloat(MessageConstants.FLOAT_VALUE_KEY, reading);
        m.sendToTarget();
    }

    private final Handler mHandler = new Handler(){
        private final DecimalFormat formatter = new DecimalFormat("0.00");
        @Override
        public void handleMessage(Message msg)
        {
            float value = msg.getData().getFloat(MessageConstants.FLOAT_VALUE_KEY);
            switch(msg.what){
                case MessageConstants.MESSAGE_THERMA_TEMPERATURE:
                    temperatureText.setText(formatter.format(value) + " °C");
                    break;

            }
        }
    };


}
