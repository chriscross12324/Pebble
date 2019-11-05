package com.simple.chris.pebble;

import android.content.Context;

public class Calculations {

    public static int screenMeasure(Context context, String value){
        switch (value) {
            case "height":
                return context.getResources().getDisplayMetrics().heightPixels;
            case "width":
                return context.getResources().getDisplayMetrics().widthPixels;

        }
        return 0;
    }

}
