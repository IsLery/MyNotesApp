package com.islery.mynotesapp.data;

import android.content.Context;
import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;

import com.islery.mynotesapp.data.models.Note;

import java.util.List;

public class NoteRepository {

    private static final String TAG = "NoteRepository";


    private NoteDatabase noteDatabase;

    public NoteRepository(Context context){
        noteDatabase = NoteDatabase.getInstance(context);
    }

    public void insertNoteTask(Note note){
        noteDatabase.getNoteDao().insertNotes(note);
       // new InsertAsyncTask(noteDatabase.getNoteDao()).execute(note);
    }

    public void updateNoteTask(Note note){
   //     new UpdateAsyncTask(noteDatabase.getNoteDao()).execute(note);
        noteDatabase.getNoteDao().update(note);

    }

    public LiveData<List<Note>> getNotes(){
        return noteDatabase.getNoteDao().getNotes();
    }

    public LiveData<List<Note>> getSearchResults(String query){
        return noteDatabase.getNoteDao().getNoteCustomQuery(query);
    }

    public void deleteNoteTask(Note note){
      noteDatabase.getNoteDao().delete(note);
    }

    public LiveData<List<Note>> getOrderedNotes(Boolean isAsc, Boolean isAlphabetic){
        Log.d(TAG, "getOrderedNotes: ");
        return noteDatabase.getNoteDao().getNotesOrdered(isAsc,isAlphabetic);
    }

    public LiveData<List<Note>> getOrderedSearchNotes(Boolean isAsc, Boolean isAlphabetic, String query){
        Log.d(TAG, "getOrderedSearchNotes: ");
        query = "%"+query+"%";
        return noteDatabase.getNoteDao().getNoteSearchOrderedQuery(isAsc,isAlphabetic, query);
    }

    public LiveData<List<Note>> getNotesInDateRange(Pair<Long,Long> range){
        return noteDatabase.getNoteDao().getNotesInDateRange(range.first,range.second);
    }


}
