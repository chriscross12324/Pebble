package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {

    Context context;
    private ArrayList<HashMap<String, String>> map;
    private LayoutInflater layoutInflater;

    public FeaturedAdapter(ArrayList<HashMap<String, String>> map, Context context) {
        this.map = map;
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.layoutInflater.inflate(R.layout.featured_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            HashMap<String, String> details;
            details = map.get(position);
            int startColour = Color.parseColor(details.get("leftColour"));
            int endColour = Color.parseColor(details.get("rightColour"));
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{startColour, endColour}
            );
            gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                    context.getResources().getDisplayMetrics()));
            holder.gradient.setBackgroundDrawable(gradientDrawable);
            holder.gradient.setTransitionName(details.get("backgroundName"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                holder.gradient.setOutlineSpotShadowColor(endColour);
            }

            holder.gradientName.setText(details.get("backgroundName"));

        } catch (Exception e) {
            Log.e("ERR", "pebble.featured_adapter: "+e.getLocalizedMessage());
        }

        holder.itemView.setOnClickListener(view -> {

        });
    }

    public int getItemCount() {
        return map.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gradient;
        TextView gradientName;

        public ViewHolder(View itemView) {
            super(itemView);
            gradient = itemView.findViewById(R.id.gradient);
            gradientName = itemView.findViewById(R.id.gradientName);
        }
    }

}
