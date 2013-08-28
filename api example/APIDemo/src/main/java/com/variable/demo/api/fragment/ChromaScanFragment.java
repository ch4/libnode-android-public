package com.variable.demo.api.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.variable.demo.api.NodeApplication;
import com.variable.demo.api.R;
import com.variable.framework.chroma.ChromaDevice;
import com.variable.framework.node.NodeDevice;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by Corey_2 on 8/28/13.
 */
public class ChromaScanFragment extends ChromaFragment {


    public static final String TAG = ChromaScanFragment.class.getName();
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private NodeDevice node;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanced){
        super.onCreateView(inflater,view, savedInstanced);

        final View rootView = inflater.inflate(R.layout.single_scan, null, false);
        rootView.findViewById(R.id.btnSingleScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ;
                node.requestChromaReading();
            }
        });

        node  = ((NodeApplication)getActivity().getApplication()).getActiveNode();
        return rootView;
    }

    @Override
    public void onChromaDeviceCreated(NodeDevice nodeDevice, ChromaDevice chromaDevice) {
        mProgressDialog.dismiss();

        if(chromaDevice != null){
            Log.wtf(TAG, "Chroma Unable to Initialize...Possible Due to no chroma on NODE");
        }
    }



    @Override
    public void onResume(){
        super.onResume();

        if(node.getChromaDevice() == null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Initializing Chroma");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            node.requestChromaInstanceAsync(getActivity());
        }
    }
    @Override
    public void onColorUpdate(int color){
        super.onColorUpdate(color);

        getView().findViewById(R.id.imgScanColor).setBackgroundColor(color);
    }


    @Override
    public void onTimeStampUpdate(Date timeStamp){
        super.onTimeStampUpdate(timeStamp);
        ((TextView) getView().findViewById(R.id.scanTitle)).setText(timeStamp.toString());
    }

    @Override
    public void onRGBUpdate(float r, float g, float b){
        super.onRGBUpdate(r, g, b);

        String text = formatter.format(r) + " , " + formatter.format(g) + " , " + formatter.format(b);
        ((TextView) getView().findViewById(R.id.txtRGB)).setText(text);
    }


    @Override
    public void onLABUpdate(float l, float a, float b){
        super.onLABUpdate(l, a, b);
        String text = formatter.format(l) + " , " + formatter.format(a) + " , " + formatter.format(b);
        ((TextView) getView().findViewById(R.id.txtLab)).setText(text);
    }


    @Override
    public void onHexValue(String hex){
        super.onHexValue(hex);
        ((TextView) getView().findViewById(R.id.txtHex)).setText(hex);
    }






}
