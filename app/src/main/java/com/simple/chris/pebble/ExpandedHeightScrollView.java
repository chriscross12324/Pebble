package com.simple.chris.pebble;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class ExpandedHeightScrollView extends GridView {

    public ExpandedHeightScrollView(Context context) {
        super(context);
    }

    public ExpandedHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedHeightScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST));
        getLayoutParams().height = getMeasuredHeight();
    }

}
