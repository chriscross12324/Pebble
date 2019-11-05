package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;

public class GridAdapter extends BaseAdapter {

    int leftColour;
    int rightColour;
    Context mContext;
    ArrayList<HashMap<String, String>> map;

    public GridAdapter(@NonNull Context context, ArrayList<HashMap<String, String>> map) {
        super();
        this.mContext = context;
        this.map = map;
    }

    @Override
    public int getCount() {
        try {
            return map.size();
        } catch (Exception e) {
            Log.e("ERR", "pebble.grid_adapter: " + e.getLocalizedMessage());
        }
        return 0;
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
            leftColour = Color.parseColor(details.get("startColour"));
            rightColour = Color.parseColor(details.get("endColour"));
            ViewHolder mViewHolder = new ViewHolder();

            if (convertView == null) {
                LayoutInflater mInflator = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflator.inflate(R.layout.all_card, parent, false);
                //mViewHolder.cardView = convertView.findViewById(R.id.cardView);
                mViewHolder.mGradient = (ImageView) convertView.findViewById(R.id.gradient);
                mViewHolder.mName = (TextView) convertView.findViewById(R.id.gradientName);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{leftColour, rightColour}
            );
            gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, mContext.getResources().getDisplayMetrics()));
            mViewHolder.mGradient.setBackgroundDrawable(gradientDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                mViewHolder.mGradient.setOutlineSpotShadowColor(rightColour);
            }

            //mViewHolder.cardView.setTransitionName(details.get("backgroundName"));
            mViewHolder.mName.setText(details.get("backgroundName"));

        }catch (Exception e){
            Log.e("ERR", "pebble.grid_adapter: "+e.getLocalizedMessage());
        }
        return convertView;
    }

    static class ViewHolder {
        //CardView cardView;
        ImageView mGradient;
        TextView mName;
    }
}
