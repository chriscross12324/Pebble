package com.simple.chris.pebble;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private List<ViewPagerModel> content;
    private Context context;

    public ViewPagerAdapter(List<ViewPagerModel> content, Context context) {
        this.content = content;
        this.context = context;
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.featured_card, container, false);
        container.addView(view);

        ImageView gradient = view.findViewById(R.id.gradient);
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {content.get(position).getStartColour(), content.get(position).getEndColour()}
        );
        gradientDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                context.getResources().getDisplayMetrics()));
        gradient.setBackgroundDrawable(gradientDrawable);

        TextView gradientName = view.findViewById(R.id.gradientName);
        gradientName.setText(content.get(position).getName());

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
