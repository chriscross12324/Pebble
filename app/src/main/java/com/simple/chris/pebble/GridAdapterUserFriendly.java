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
import java.util.List;

public class GridAdapterUserFriendly extends BaseAdapter {

    int leftColour;
    int rightColour;
    Context mContext;
    ArrayList<HashMap<String, String>> map;

    public GridAdapterUserFriendly(@NonNull Context context, ArrayList<HashMap<String, String>> map) {
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
                convertView = mInflator.inflate(R.layout.gridview_module_user_friendly, parent, false);
                mViewHolder.mGradient = (ImageView) convertView.findViewById(R.id.backgroundGradient);
                mViewHolder.mName = (TextView) convertView.findViewById(R.id.backgroundName);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{leftColour, rightColour}
            );
            mViewHolder.mGradient.setBackgroundDrawable(gradientDrawable);
            mViewHolder.mName.setText(details.get("backgroundName"));

        }catch (Exception e){
            Log.e("TAG", ""+e.getLocalizedMessage());
            //GradientsList.appCrashReceiver();
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView mGradient;
        TextView mName;
    }
}
