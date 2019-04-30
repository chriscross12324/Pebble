package com.simple.chris.pebble;

import android.content.Context;
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

public class GridAdapter extends ArrayAdapter<String> {

    String[] names;
    int[] leftColour;
    int[] rightColour;
    Context mContext;

    public GridAdapter(@NonNull Context context, String[] names, int[] leftColour, int[] rightColour) {
        super(context, R.layout.gridview_module);
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
            convertView = mInflator.inflate(R.layout.gridview_module, parent, false);
            mViewHolder.mGradient = (ImageView) convertView.findViewById(R.id.backgroundGradient);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.backgroundName);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{leftColour[position], rightColour[position]}
        );
        mViewHolder.mGradient.setBackgroundDrawable(gradientDrawable);
        mViewHolder.mName.setText(names[position]);
        Log.e("Info", ""+names[position]);
        return convertView;
    }

    static class ViewHolder {
        ImageView mGradient;
        TextView mName;
    }
}
