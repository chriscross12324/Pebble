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

    public FeaturedAdapter(Context context, ArrayList<HashMap<String, String>> map) {
        this.map = map;
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.layoutInflater.inflate(R.layout.module_featured, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            HashMap<String, String> details;
            details = map.get(position);
            holder.gradientName.setText(details.get("backgroundName"));
            int startColour = Color.parseColor(details.get("startColour"));
            int endColour = Color.parseColor(details.get("endColour"));

            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[] {startColour, endColour}
            );
            gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()));
            holder.gradientView.setBackground(gradientDrawable);
            holder.gradientView.setTransitionName(details.get("backgroundName"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                holder.gradientView.setOutlineSpotShadowColor(endColour);
            }
        } catch (Exception e) {
            Log.e("ERR", "pebble.featured_adapter.on_bind_view_holder: " + e.getLocalizedMessage());
        }

        holder.itemView.setOnClickListener(view -> {

        });
    }

    public int getItemCount() {
        try {
            return map.size();
        } catch (Exception e) {
            Log.e("ERR", "pebble.grid_adapter: " + e.getLocalizedMessage());
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView gradientInitials;
        TextView gradientName;
        TextView creator;
        TextView timeStamp;
        ImageView gradientView;

        public ViewHolder(View itemView) {
            super(itemView);
            gradientInitials = itemView.findViewById(R.id.gradientNameInitials);
            gradientName = itemView.findViewById(R.id.gradientName);
            creator = itemView.findViewById(R.id.creator);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            gradientView = itemView.findViewById(R.id.gradientView);
        }
    }

}
