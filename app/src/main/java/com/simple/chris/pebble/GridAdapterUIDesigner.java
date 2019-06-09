package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GridAdapterUIDesigner extends ArrayAdapter<String> {

    String[] topLeftHex;
    String[] bottomRightHex;
    String[] names;
    int[] leftColour;
    int[] rightColour;
    Context mContext;

    public GridAdapterUIDesigner(@NonNull Context context, String[] topLeftHex, String[] bottomRightHex, String[] names, int[] leftColour, int[] rightColour) {
        super(context, R.layout.gridview_module_ui_designer);
        this.topLeftHex = topLeftHex;
        this.bottomRightHex = bottomRightHex;
        this.names = names;
        this.leftColour = leftColour;
        this.rightColour = rightColour;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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
            //mViewHolder.mName = (TextView) convertView.findViewById(R.id.backgroundName);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{leftColour[position], rightColour[position]}
        );
        mViewHolder.mGradient.setBackgroundDrawable(gradientDrawable);
        GradientDrawable topColourCircle = new GradientDrawable();
        topColourCircle.setShape(GradientDrawable.OVAL);
        topColourCircle.setStroke(5, Color.parseColor(topLeftHex[position]));
        GradientDrawable bottomColourCircle = new GradientDrawable();
        bottomColourCircle.setShape(GradientDrawable.OVAL);
        bottomColourCircle.setStroke(5, Color.parseColor(bottomRightHex[position]));
        mViewHolder.mTopColourCircle.setBackground(topColourCircle);
        mViewHolder.mBottomColourCircle.setBackground(bottomColourCircle);
        //mViewHolder.mTopColourCircle.setColorFilter(R.color.colorPrimary);
        //mViewHolder.mBottomColourCircle.setImageTintList(ColorStateList.valueOf(Color.parseColor(bottomRightHex[position])));
        //mViewHolder.mTopColourCircle.setBackgroundTintList(Color.parseColor(R.color.colorPrimary));
        mViewHolder.mTopColourHex.setText(topLeftHex[position]);
        mViewHolder.mBottomColourHex.setText(bottomRightHex[position]);
        //mViewHolder.mName.setText(names[position]);
        Log.e("Info", ""+Color.parseColor(topLeftHex[position]));
        return convertView;
    }

    static class ViewHolder {
        ImageView mGradient;
        TextView mTopColourHex;
        TextView mBottomColourHex;
        ImageView mTopColourCircle;
        ImageView mBottomColourCircle;
        //TextView mName;
    }
}
