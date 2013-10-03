package com.variable.demo.api.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
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
public class ThermaFragment extends Fragment implements INode.ThermaListener {
    public static final String TAG = ThermaFragment.class.getName();

    private TextView temperatureText;
    private Switch   irLedsSwitch;
    private int temperatureUnit = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.therma, null, false);
        temperatureText = (TextView) root.findViewById(R.id.txtTherma);
        temperatureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(++temperatureUnit == 2){
                   temperatureUnit = 0;
               }
            }
        });
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        //Unregister for therma event.
        DefaultNotifier.instance().removeThermaListener(this);

        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null)
        {
            node.setStreamModeIRTherma(false); //Turns streaming and the ir off.
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Register for Therma Event
        DefaultNotifier.instance().addThermaListener(this);

        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null)
        {
            node.setStreamModeIRTherma(true); //Turns streaming and the ir off.
        }
    }

    @Override
    public void onTemperatureReading(NodeDevice nodeDevice, Float reading) {
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
                  String unitSymbol = " ºC";
                  if(temperatureUnit  == 1){
                      value =  value * 1.8000f + 32;
                      unitSymbol = " ºF";
                  }
                  temperatureText.setText(formatter.format(value) +  unitSymbol);
                  break;

        }
      }
    };
}
