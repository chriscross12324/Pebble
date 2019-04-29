package com.simple.chris.pebble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityStaggered extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staggered_test);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    private class GridHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public GridHolder(View itemView){
            super(itemView);
            //imageView = itemView.findViewById(R.id.)
        }
    }
}
