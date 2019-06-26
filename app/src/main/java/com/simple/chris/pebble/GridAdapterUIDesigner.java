package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class GridAdapterUIDesigner extends BaseAdapter {

    int leftColour;
    int rightColour;
    Context mContext;
    ArrayList<HashMap<String, String>> map;

    public GridAdapterUIDesigner(@NonNull Context context, ArrayList<HashMap<String, String>> map) {
        super();
        this.mContext = context;
        this.map = map;
    }

    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public Object getItem(int position) {
        return map.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            HashMap<String, String> details;
            details = map.get(position);
            leftColour = Color.parseColor(details.get("leftColour"));
            rightColour = Color.parseColor(details.get("rightColour"));

            ViewHolder mViewHolder = new ViewHolder();
            if (convertView == null) {
                LayoutInflater mInflator = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflator.inflate(R.layout.gridview_module_ui_designer, parent, false);
                mViewHolder.mGradient = (ImageView) convertView.findViewById(R.id.backgroundGradient);
                mViewHolder.mTopColourHex = (TextView) convertView.findViewById(R.id.topColourHex);
                mViewHolder.mBottomColourHex = (TextView) convertView.findViewById(R.id.bottomColourHex);
                mViewHolder.mTopColourCircle = (ImageView) convertView.findViewById(R.id.topColourCircle);
                mViewHolder.mBottomColourCircle = (ImageView) convertView.findViewById(R.id.bottomColourCircle);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{leftColour, rightColour}
            );
            mViewHolder.mGradient.setBackgroundDrawable(gradientDrawable);
            mViewHolder.mGradient.setTransitionName(details.get("backgroundName"));
            GradientDrawable topColourCircle = new GradientDrawable();
            topColourCircle.setShape(GradientDrawable.OVAL);
            topColourCircle.setStroke(5, leftColour);
            GradientDrawable bottomColourCircle = new GradientDrawable();
            bottomColourCircle.setShape(GradientDrawable.OVAL);
            bottomColourCircle.setStroke(5, rightColour);
            mViewHolder.mTopColourCircle.setBackground(topColourCircle);
            mViewHolder.mBottomColourCircle.setBackground(bottomColourCircle);
            mViewHolder.mTopColourHex.setText(details.get("leftColour"));
            mViewHolder.mBottomColourHex.setText(details.get("rightColour"));
        }catch (Exception e){
            Log.e("UID", ""+e.getLocalizedMessage());
        }


        //mViewHolder.mName.setText(names[position]);
        return convertView;
    }

    static class ViewHolder {
        ImageView mGradient;
        TextView mTopColourHex;
        TextView mBottomColourHex;
        ImageView mTopColourCircle;
        ImageView mBottomColourCircle;
    }
}
