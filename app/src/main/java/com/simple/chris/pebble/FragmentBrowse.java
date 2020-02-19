package com.simple.chris.pebble;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.polyak.iconswitch.IconSwitch;

import java.util.HashMap;
import java.util.Objects;

public class FragmentBrowse extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    View rootView;
    EditText searchField;
    Spinner filterSpinner;
    IconSwitch viewType;
    Button button;
    ConstraintLayout headerHolder;
    CardView bottomSheet;

    BottomSheetBehavior bottomSheetBehavior;

    int headerHeight;
    int gridViewFirstVisiblePos;
    int gridViewLastVisiblePos;
    boolean headerHiden = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragement_browse, container, false);

        getAndSetBrowse();

        searchField = rootView.findViewById(R.id.searchField);
        filterSpinner = rootView.findViewById(R.id.filterSpinner);
        viewType = rootView.findViewById(R.id.viewType);
        button = rootView.findViewById(R.id.button);
        headerHolder = rootView.findViewById(R.id.headerHolder);
        bottomSheet = rootView.findViewById(R.id.bottomSheet);


        searchField.setText(Values.browseSearchField);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Values.browseSearchField = searchField.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterSpinner.setOnItemSelectedListener(this);
        viewType.setCheckedChangeListener(this::onCheckChanged);

        ViewTreeObserver vto = headerHolder.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(() -> {
            headerHeight = headerHolder.getMeasuredHeight();
            try {
                button.setOnClickListener(view -> {
                    UIAnimations.constraintLayoutObjectAnimator(headerHolder, "translationY", 0 - headerHeight, 600, 0, new DecelerateInterpolator(3));
                    headerHolder.setVisibility(View.GONE);
                });
            } catch (Exception e) {
            }

        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int peekHeight = (int) Math.round(screenHeight * .6);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(peekHeight);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    //Toast.makeText(getContext(), "Expanded", Toast.LENGTH_SHORT).show();
                    //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                //Log.e("ERR", ""+v);
            }
        });

        return rootView;
    }


    public void getAndSetBrowse() {
        try {
            RecyclerView gridView = rootView.findViewById(R.id.gradientGrid);
            gridView.setHasFixedSize(true);

            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            gridView.setLayoutManager(layoutManager);
            //GridAdapter gridAdapter = new GridAdapter(getActivity(), Values.browse);
            BrowseRecylerViewAdapter recyclerViewAdapter = new BrowseRecylerViewAdapter(getActivity(), Values.browse);
            gridView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.setClickListener(new BrowseRecylerViewAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent details = new Intent(getActivity(), GradientDetails.class);
                    HashMap<String, String> info = Values.browse.get(position);

                    String gradientName = info.get("backgroundName");
                    String startColour = info.get("startColour");
                    String endColour = info.get("endColour");
                    String description = info.get("description");

                    details.putExtra("gradientName", gradientName);
                    details.putExtra("startColour", startColour);
                    details.putExtra("endColour", endColour);
                    details.putExtra("description", description);

                    //Log.e("TAG", details.getStringExtra("startColor"));

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.gradient), gradientName);
                    startActivity(details, options.toBundle());
                }
            });

        } catch (Exception e) {
            Log.e("OOPS", e.getLocalizedMessage());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Values.INSTANCE.setFilterSpinner(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onCheckChanged(IconSwitch.Checked current) {
        Values.INSTANCE.setGridCount(current);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent details = new Intent(getActivity(), GradientDetails.class);
        HashMap<String, String> info = Values.browse.get(position);

        String gradientName = info.get("backgroundName");
        String startColour = info.get("startColour");
        String endColour = info.get("endColour");
        String description = info.get("description");

        details.putExtra("gradientName", gradientName);
        details.putExtra("startColour", startColour);
        details.putExtra("endColour", endColour);
        details.putExtra("description", description);

        //Log.e("TAG", details.getStringExtra("startColor"));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.gradient), gradientName);
        startActivity(details, options.toBundle());
    }

}
