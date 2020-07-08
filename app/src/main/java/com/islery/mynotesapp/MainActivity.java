package com.islery.mynotesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.islery.mynotesapp.databinding.ActivityMainBinding;
import com.islery.mynotesapp.ui.list.NoteListFragment;
import com.islery.mynotesapp.ui.note.NoteFragment;

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
        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof NoteFragment){
            NoteFragment noteFragment = (NoteFragment) fragment;
            if (noteFragment.isOldItemInEditMode()){
                noteFragment.disableEditMode();
            }else {
                getSupportFragmentManager().popBackStack();
            }
        }else {
            super.onBackPressed();
        }
    }
}
