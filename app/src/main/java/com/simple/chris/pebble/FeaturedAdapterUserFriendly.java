package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FeaturedAdapterUserFriendly extends PagerAdapter {

    Context context;
    private ArrayList<HashMap<String, String>> map;
    private LayoutInflater layoutInflater;

    FeaturedAdapterUserFriendly(Context context, ArrayList<HashMap<String, String>> map) {
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
            int startColour = Color.parseColor(details.get("leftColour"));
            int endColour = Color.parseColor(details.get("rightColour"));
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.gradient = view.findViewById(R.id.gradient);
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{startColour, endColour}
            );
            gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                    context.getResources().getDisplayMetrics()));
            viewHolder.gradient.setBackgroundDrawable(gradientDrawable);
            viewHolder.gradient.setTransitionName(details.get("backgroundName"));

            //UserFriendly
            viewHolder.gradientName = view.findViewById(R.id.gradientName);
            viewHolder.gradientName.setText(details.get("backgroundName"));

            //UIDesigner
            viewHolder.startCircle = view.findViewById(R.id.startCircle);
            viewHolder.endCircle = view.findViewById(R.id.endCircle);
            viewHolder.startHex = view.findViewById(R.id.startHex);
            viewHolder.endHex = view.findViewById(R.id.endHex);
            GradientDrawable startGD = new GradientDrawable();
            startGD.setShape(GradientDrawable.OVAL);
            startGD.setStroke(5, startColour);
            GradientDrawable endGD = new GradientDrawable();
            endGD.setShape(GradientDrawable.OVAL);
            endGD.setStroke(5, endColour);
            viewHolder.startCircle.setBackgroundDrawable(startGD);
            viewHolder.endCircle.setBackgroundDrawable(endGD);
            viewHolder.startHex.setText(details.get("leftColour"));
            viewHolder.endHex.setText(details.get("rightColour"));

            if (Values.uiDesignerMode) {
                viewHolder.gradientName.setVisibility(View.GONE);
            } else {
                viewHolder.startCircle.setVisibility(View.GONE);
                viewHolder.endCircle.setVisibility(View.GONE);
                viewHolder.startHex.setVisibility(View.GONE);
                viewHolder.endHex.setVisibility(View.GONE);
            }
            container.addView(view);
        } catch (Exception e) {
            Log.e("FeaturedAdapterUF", e.getLocalizedMessage());
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }


    static class ViewHolder {
        ImageView gradient;
        ImageView startCircle;
        ImageView endCircle;
        TextView startHex;
        TextView endHex;
        TextView gradientName;
    }
}
