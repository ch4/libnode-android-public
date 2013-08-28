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
public class OxaFragment extends Fragment implements INode.OxaListener {
    public static final String TAG = OxaFragment.class.getName();

    private TextView oxaText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.oxa, null, false);
        oxaText = (TextView) root.findViewById(R.id.txtOxa);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        DefaultNotifier.instance().removeOxaListener(this);

        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null)
        {
            node.setStreamModeOxa(false, (short) 0, (short) 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        DefaultNotifier.instance().addOxaListener(this);

        NodeDevice node = ((NodeApplication) getActivity().getApplication()).getActiveNode();
        if(node != null)
        {
            node.setStreamModeOxa(true, (short) 0, (short) 0);
        }
    }

    @Override
    public void onOxaBaselineUpdate(NodeDevice nodeDevice, float v) {

    }

    @Override
    public void onOxaUpdate(NodeDevice nodeDevice, Float aFloat) {
        Message m = mHandler.obtainMessage(MessageConstants.MESSAGE_OXA_READING);
        m.getData().putFloat(MessageConstants.FLOAT_VALUE_KEY, aFloat);
        m.sendToTarget();
    }

    @Override
    public void onOxaTempUpdate(NodeDevice nodeDevice, int i) {

    }

    private final Handler mHandler = new Handler(){
     private final DecimalFormat formatter = new DecimalFormat("0.00");

     @Override
     public void handleMessage(Message message)
     {
        float value = message.getData().getFloat(MessageConstants.FLOAT_VALUE_KEY);
        switch(message.what){
            case MessageConstants.MESSAGE_OXA_READING:
                oxaText.setText(formatter.format(value) + " PPM");
                break;
        }
      }
    };
}
