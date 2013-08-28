package com.variable.demo.api;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by coreymann on 7/2/13.
 */
public class ColorUtils {

    private static final int COLOR_FLOAT_TO_INT_FACTOR = 255;


    public static int RGBToColor(final float pRed, final float pGreen, final float pBlue) {
        Log.d("ColorUtils", "Before Scan= " + pRed + ", " + pGreen + " , " + pBlue);


        return Color.rgb(normalizeFloat(pRed), normalizeFloat(pGreen), normalizeFloat(pBlue));
    }


    private static int normalizeFloat(float f){
        double f2 = Math.max(0.0, Math.min(1.0, f));
        return (int) Math.floor(f2 == 1.0 ? COLOR_FLOAT_TO_INT_FACTOR : f2 * (COLOR_FLOAT_TO_INT_FACTOR + 1));
    }
}
