package com.islery.mynotesapp.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.islery.mynotesapp.databinding.NoteRowBinding;
import com.islery.mynotesapp.data.models.Note;
import com.islery.mynotesapp.utils.NoteDiffUtilsCallback;
import com.islery.mynotesapp.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteHolder> {
    List<Note> noteList;
    OnNoteClickListener noteListener;

    public NoteListAdapter(OnNoteClickListener noteListener) {
        this.noteListener = noteListener;
        noteList = new ArrayList<>();
    }

    public void updateRv(List<Note> newList){
        NoteDiffUtilsCallback callback = new NoteDiffUtilsCallback(noteList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        noteList = newList;
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteRowBinding binding = NoteRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new NoteHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = noteList.get(position);

        holder.bind(noteListener, note);

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public Note getItem(int pos){
        return noteList.get(pos);
    }

    class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private NoteRowBinding binding;
        private OnNoteClickListener noteClickListener;

        public NoteHolder(NoteRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OnNoteClickListener listener, Note note){
            this.noteClickListener = listener;
            binding.noteRow.setOnClickListener(this);
            String title = note.getTitle();
            if (title.isEmpty()){
                title = note.getContent();
            }
            binding.noteTitleTxt.setText(title);
            String time = Utility.getFormatedDate(note.getTimestamp(),"dd MMM, hh:mm");
            binding.noteTimestamp.setText(time);
        }


        @Override
        public void onClick(View v) {
            noteClickListener.onNoteClick(getAdapterPosition());
        }
    }

    interface OnNoteClickListener{
        void onNoteClick(int pos);
    }
}
