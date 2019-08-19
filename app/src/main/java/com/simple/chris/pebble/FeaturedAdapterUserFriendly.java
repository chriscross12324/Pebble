package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FeaturedAdapterUserFriendly extends PagerAdapter {

    int startColour;
    int endColour;
    Context context;
    ArrayList<HashMap<String, String>> map;
    LayoutInflater layoutInflater;

    public FeaturedAdapterUserFriendly(Context context, ArrayList<HashMap<String, String>> map) {
        super();
        this.context = context;
        this.map = map;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return map.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = this.layoutInflater.inflate(R.layout.featured_card, container, false);
        try {
            Log.e("INFO", "Got here");
            HashMap<String, String> details;
            details = map.get(position);
            startColour = Color.parseColor(details.get("leftColour"));
            endColour = Color.parseColor(details.get("rightColour"));
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.gradient = view.findViewById(R.id.gradient);
            viewHolder.gradientName = view.findViewById(R.id.gradientName);
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{startColour, endColour}
            );
            gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                    context.getResources().getDisplayMetrics()));
            viewHolder.gradient.setBackgroundDrawable(gradientDrawable);
            viewHolder.gradient.setTransitionName(details.get("backgroundName"));
            viewHolder.gradientName.setText(details.get("backgroundName"));
            container.addView(view);
        } catch (Exception e) {
            Log.e("FeaturedAdapterUF", e.getLocalizedMessage());
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (View) object == view;
    }


    static class ViewHolder {
        ImageView gradient;
        TextView gradientName;
    }
}
