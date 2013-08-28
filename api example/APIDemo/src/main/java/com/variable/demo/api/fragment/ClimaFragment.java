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
 * Created by coreymann on 8/13/13.
 */
public class ClimaFragment extends Fragment  implements INode.ClimaHumidityListener, INode.ClimaLightListener, INode.ClimaPressureListener, INode.ClimaTemperatureListener{
    public static final String TAG = ClimaFragment.class.getName();

    private TextView climaLightText;
    private TextView climaPressureText;
    private TextView climaTempText;
    private TextView climaHumidText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.clima, null, false);

       climaHumidText = (TextView) root.findViewById(R.id.txtClimaHumidity);
       climaLightText = (TextView) root.findViewById(R.id.txtClimaLight);
       climaPressureText = (TextView) root.findViewById(R.id.txtClimaPressure);
       climaTempText = (TextView) root.findViewById(R.id.txtClimaTemperature);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        DefaultNotifier.instance().addClimaHumidityListener(this);
        DefaultNotifier.instance().addClimaLightListener(this);
        DefaultNotifier.instance().addClimaTemperatureListener(this);
        DefaultNotifier.instance().addClimaPressureListener(this);

        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null){
            node.setStreamModeClimaTP(true, true, true); //Turns on all sensor for Clima
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        DefaultNotifier.instance().removeClimaHumidityListener(this);
        DefaultNotifier.instance().removeClimaLightListener(this);
        DefaultNotifier.instance().removeClimaTemperatureListener(this);
        DefaultNotifier.instance().removeClimaPressureListener(this);


        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null){
            node.setStreamModeClimaTP(false, false, false); //Turns off all sensor for Clima
        }
    }

    @Override
    public void onClimaHumidityUpdate(NodeDevice nodeDevice, float humidityLevel) {
        Message m = mHandler.obtainMessage(MessageConstants.MESSAGE_CLIMA_HUMIDITY);
        m.getData().putFloat(MessageConstants.FLOAT_VALUE_KEY, humidityLevel);
        m.sendToTarget();
    }

    @Override
    public void onClimaLightUpdate(NodeDevice nodeDevice, Float lightLevel) {
        Message m = mHandler.obtainMessage(MessageConstants.MESSAGE_CLIMA_LIGHT);
        m.getData().putFloat(MessageConstants.FLOAT_VALUE_KEY, lightLevel);
        m.sendToTarget();
    }

    @Override
    public void onClimaPressureUpdate(NodeDevice nodeDevice, Integer kPa) {
        Message m = mHandler.obtainMessage(MessageConstants.MESSAGE_CLIMA_PRESSURE);
        m.getData().putFloat(MessageConstants.FLOAT_VALUE_KEY, kPa);
        m.sendToTarget();
    }

    @Override
    public void onClimaTemperatureUpdate(NodeDevice nodeDevice, float temperature) {
        Message m = mHandler.obtainMessage(MessageConstants.MESSAGE_CLIMA_TEMPERATURE);
        m.getData().putFloat(MessageConstants.FLOAT_VALUE_KEY, temperature);
        m.sendToTarget();
    }

    private final Handler mHandler = new Handler(){
        private final DecimalFormat formatter = new DecimalFormat("0.00");

        @Override
        public void handleMessage(Message msg){

            float value = msg.getData().getFloat(MessageConstants.FLOAT_VALUE_KEY);
            switch(msg.what){
                case MessageConstants.MESSAGE_CLIMA_HUMIDITY:
                    climaHumidText.setText(formatter.format(value) + " %RH");
                    break;
                 case MessageConstants.MESSAGE_CLIMA_LIGHT:
                    climaLightText.setText(formatter.format(value) + " LUX");
                    break;

                case MessageConstants.MESSAGE_CLIMA_PRESSURE:
                    climaPressureText.setText(formatter.format(value) + " kPA");
                    break;

                case MessageConstants.MESSAGE_CLIMA_TEMPERATURE:
                    climaTempText.setText(formatter.format(value) + " C");
                    break;

            }

        }
    };
}
