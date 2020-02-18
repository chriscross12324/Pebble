package com.simple.chris.pebble;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Main extends AppCompatActivity {

    Fragment browseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIElements.INSTANCE.setTheme(Main.this);
        setContentView(R.layout.activity_main);

        browseFragment = new FragmentBrowse();

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentHolder, browseFragment).commit();
    }
}
