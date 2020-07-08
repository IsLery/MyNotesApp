package com.islery.mynotesapp.ui.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.islery.mynotesapp.R;
import com.islery.mynotesapp.databinding.NoteFragmentBinding;
import com.islery.mynotesapp.databinding.NoteTitleLayoutBinding;
import com.islery.mynotesapp.data.models.Note;
import com.islery.mynotesapp.utils.UndoRedo.NoteData;
import com.islery.mynotesapp.utils.UndoRedo.UndoRedoTextManager;
import com.islery.mynotesapp.utils.Utility;


public class NoteFragment extends Fragment implements View.OnClickListener  {

    private NoteFragmentBinding binding;
    private NoteTitleLayoutBinding toolbarBinding;
    private NoteViewModel mViewModel;
    private boolean isNewNote;
    private boolean isEditMode;
    private Note initNote;
    private UndoRedoTextManager textManager;
    private static final String TAG = "NoteFragment";



    public static NoteFragment newInstance(Note note) {
        NoteFragment fragment = new NoteFragment();
        if (note != null) {
            Bundle args = new Bundle();
            args.putParcelable("note", note);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null && getArguments().containsKey("note")){
            initNote = getArguments().getParcelable("note");

        }else {
            isNewNote = true;
            initNote = new Note("New note","",0);

        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = NoteFragmentBinding.inflate(inflater,container,false);
        toolbarBinding = binding.toolbarContent;
        mViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        textManager = mViewModel.getManager();
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarNote);
        initNoteData();
        if (isNewNote){
            enableEditMode();
        }else {
            disableEditMode();
        }
        setListeners();

        return binding.getRoot();
    }

    private void setListeners(){
        toolbarBinding.redoBtn.setOnClickListener(this);
        toolbarBinding.shareBtn.setOnClickListener(this);
        toolbarBinding.undoBtn.setOnClickListener(this);
        toolbarBinding.checkBtn.setOnClickListener(this);
        toolbarBinding.backToListBtn.setOnClickListener(this);
        toolbarBinding.editBtn.setOnClickListener(this);

        toolbarBinding.inputTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            textManager.textChanged(R.id.inputTitle,s);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            toolbarBinding.undoBtn.setEnabled(true);
            }
        });
        binding.noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textManager.textChanged(R.id.noteContent,s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                toolbarBinding.undoBtn.setEnabled(true);
            }
        });
    }

    private void initNoteData(){
        toolbarBinding.titleTxt.setText(initNote.getTitle());
        toolbarBinding.inputTitle.setText(initNote.getTitle());
        binding.noteContent.setText(initNote.getContent());

    }


    private void enableEditMode(){
        toolbarBinding.viewModeContainer.setVisibility(View.GONE);
        toolbarBinding.editModeContainer.setVisibility(View.VISIBLE);

        toolbarBinding.inputTitle.setVisibility(View.VISIBLE);
        toolbarBinding.titleTxt.setVisibility(View.GONE);

        toolbarBinding.redoBtn.setEnabled(false);
        binding.noteContent.setEnabled(true);
        isEditMode = true;
    }

    public void disableEditMode(){
        toolbarBinding.viewModeContainer.setVisibility(View.VISIBLE);
        toolbarBinding.editModeContainer.setVisibility(View.GONE);
        toolbarBinding.inputTitle.setVisibility(View.GONE);
        toolbarBinding.titleTxt.setVisibility(View.VISIBLE);
        toolbarBinding.titleTxt.setText(toolbarBinding.inputTitle.getText());
        binding.noteContent.setEnabled(false);
        binding.noteContent.setTextColor(Color.DKGRAY);
        isEditMode = false;
        textManager.clear();
    }


    private void saveChanges(){
            if (isNewNote) {
               validateAndSaveNewNote();
            } else {
               validateAndSaveExistingNote();
        }
    }

    private void validateAndSaveNewNote(){
    String content = binding.noteContent.getText().toString().trim();
    String title = toolbarBinding.inputTitle.getText().toString().trim();
    if (!content.isEmpty()){
        Note note = new Note(title,content,System.currentTimeMillis());
     mViewModel.saveNewNote(note);
    }
    else Toast.makeText(requireContext(), "Note was not saved", Toast.LENGTH_SHORT).show();
    getParentFragmentManager().popBackStack();
    }


    private void validateAndSaveExistingNote(){
        String content = binding.noteContent.getText().toString().trim();
        String title = toolbarBinding.inputTitle.toString().trim();
        if (initNote.getContent().equals(content) && initNote.getTitle().equals(title)){
            disableEditMode();
        }
        if (content.isEmpty()){
            mViewModel.deleteNote(initNote);
            Toast.makeText(requireContext(), "Note was deleted", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
        initNote.setTitle(title);
        initNote.setContent(content);
        initNote.setTimestamp(System.currentTimeMillis());
        mViewModel.updateNote(initNote);
        disableEditMode();
    }



    public boolean isOldItemInEditMode(){
    return isEditMode && !isNewNote;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.redoBtn:
                redoTextChanges();
                break;
            case  R.id.undoBtn:
                undoTextChanges();
                break;
            case  R.id.shareBtn:
                shareNote();
                break;
            case  R.id.checkBtn:
                saveChanges();
                break;
            case R.id.editBtn:
                enableEditMode();
                break;
            case  R.id.backToListBtn:
                getParentFragmentManager().popBackStack();
                break;
        }

    }

    private void shareNote() {
        Intent intent = new Intent(Intent.ACTION_SEND).setType("text/plain");
        if( requireContext().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {

            String send = getString(R.string.send_template,
                    toolbarBinding.titleTxt.getText().toString(),
                    binding.noteContent.getText().toString(),
                    Utility.getFormatedDate(initNote.getTimestamp(), "MM-yyy-dd hh:mm")
            );
            intent.putExtra(Intent.EXTRA_TEXT, send);
            startActivity(intent);
        }
        else Toast.makeText(requireContext(), "No apps are available for this action", Toast.LENGTH_SHORT).show();
    }

    private void undoTextChanges(){
       NoteData data = textManager.undoIsCalled();

        if (data.getId() == R.id.inputTitle){
            toolbarBinding.inputTitle.setText(data.getText());
        }else if (data.getId() == R.id.noteContent){
            binding.noteContent.setText(data.getText());
        }
        toolbarBinding.redoBtn.setEnabled(true);
        if (!textManager.canUndo()){
            toolbarBinding.undoBtn.setEnabled(false);
        }
    }


    private void redoTextChanges(){
        NoteData data = textManager.redoIsCalled();
        if (data.getId() == R.id.inputTitle){
            toolbarBinding.inputTitle.setText(data.getText());
        }else if (data.getId() == R.id.noteContent){
            binding.noteContent.setText(data.getText());
        }
        toolbarBinding.undoBtn.setEnabled(true);
        if (!textManager.canRedo()){
            toolbarBinding.redoBtn.setEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toolbarBinding = null;
        binding = null;
    }
}
