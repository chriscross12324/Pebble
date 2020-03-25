package com.simple.chris.pebble;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class StaggeredRecyclerAdapter extends RecyclerView.Adapter<StaggeredRecyclerAdapter.ImageViewHolder> {

    Context mContext;
    ArrayList<HashMap<String, String>> mItems;

    int startColour;
    int endColour;

    public StaggeredRecyclerAdapter(Context context, ArrayList<HashMap<String, String>> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.module_browse_normal, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaggeredRecyclerAdapter.ImageViewHolder holder, int position) {
        try {
            HashMap<String, String> item;
            item = mItems.get(position);
            startColour = Color.parseColor(item.get("startColour"));
            endColour = Color.parseColor(item.get("endColour"));

            //int randomHeight = new Random().nextInt((Values.cardMaxHeight - Values.cardMinHeight) + 1) + Values.cardMinHeight;

            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[] {startColour, endColour}
            );
            gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mContext.getResources().getDisplayMetrics()));
            holder.gradientView.getLayoutParams().height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, mContext.getResources().getDisplayMetrics()));
            holder.gradientView.setBackground(gradientDrawable);

            holder.gradientView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent details = new Intent(mContext, ActivityGradientDetails.class);
                    details.putExtra("gradientName", item.get("backgroundName"));
                    details.putExtra("startColour", item.get("startColour"));
                    details.putExtra("endColour", item.get("endColour"));
                    details.putExtra("description", item.get("description"));

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext, holder.gradientView, item.get("backgroundName"));
                    mContext.startActivity(details, options.toBundle());

                    //Vibration.INSTANCE.hFeedack(mContext);
                }
            });
        } catch (Exception e) {
            Log.e("ERR", "StaggeredRecyclerAdapter: " + e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView gradientView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            gradientView = itemView.findViewById(R.id.gradient);
        }
    }
}
