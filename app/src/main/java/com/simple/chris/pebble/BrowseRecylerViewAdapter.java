package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BrowseRecylerViewAdapter extends RecyclerView.Adapter<BrowseRecylerViewAdapter.ViewHolder> {

    Context context;
    private ArrayList<HashMap<String, String>> browseItems;

    private ItemClickListener clickListener;
    private LayoutInflater layoutInflater;

    BrowseRecylerViewAdapter(Context context, ArrayList<HashMap<String, String>> browseItems) {
        this.layoutInflater = LayoutInflater.from(context);

        this.context = context;
        this.browseItems = browseItems;
    }

    @NonNull
    @Override
    public BrowseRecylerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.module_browse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseRecylerViewAdapter.ViewHolder holder, int position) {
        HashMap<String, String> details;
        details = browseItems.get(position);
        holder.gradientName.setText(details.get("backgroundName"));
        int startColour = Color.parseColor(details.get("startColour"));
        int endColour = Color.parseColor(details.get("endColour"));

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{startColour, endColour}
        );
        gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()));
        holder.gradientDisplay.setBackground(gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return browseItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView gradientName;
        ImageView gradientDisplay;

        ViewHolder(View itemView) {
            super(itemView);
            gradientName = itemView.findViewById(R.id.gradientName);
            gradientDisplay = itemView.findViewById(R.id.gradient);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface  ItemClickListener {
        void onItemClick(View view, int position);
    }
}
