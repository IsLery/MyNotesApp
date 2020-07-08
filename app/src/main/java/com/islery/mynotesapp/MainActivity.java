package com.islery.mynotesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.islery.mynotesapp.databinding.ActivityMainBinding;
import com.islery.mynotesapp.ui.list.NoteListFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,new NoteListFragment())
                .addToBackStack("list")
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("note_fragment");
        if (fragment != null){
            Log.d(TAG, "onBackPressed: back from fragment");
            getSupportFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }
}
