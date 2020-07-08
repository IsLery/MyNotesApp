package com.islery.mynotesapp.utils.UndoRedo;

import android.util.Log;

import androidx.core.util.Pair;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class UndoRedoTextManager {
    // int - viewId, sequense - text
    private Deque<NoteData> textUndoHistory;
    private Deque<NoteData> textRedoHistory;
    private boolean perfomingUndo;
    private boolean perfomingRedo;
    private static final String TAG = "UndoRedoManager";

    public UndoRedoTextManager() {
        textUndoHistory = new ArrayDeque<>();
        textRedoHistory = new ArrayDeque<>();

    }

    public void textChanged(int viewId, CharSequence sequence){
        if (perfomingUndo) {
            perfomingUndo = false;
            textRedoHistory.addLast(new NoteData(viewId, sequence.toString()));
            Log.d(TAG, "textChanged: perfoming undo");
        }else if (perfomingRedo){
            perfomingRedo = false;
            NoteData data = new NoteData(viewId, sequence.toString());
            textUndoHistory.addLast(data);
        }  else {
            NoteData data = new NoteData(viewId, sequence.toString());
            textUndoHistory.addLast(data);
            Log.d(TAG, "text added: "+sequence);
        }
        Log.d(TAG, "textChanged: undo history "+textUndoHistory.toString());


    }

    public NoteData undoIsCalled(){
        Log.d(TAG, "undoIsCalled: ");
        NoteData res = textUndoHistory.removeLast();
        perfomingUndo = true;
        Log.d(TAG, "undoIsCalled: performing undo  "+perfomingUndo + " prev value: " + res.text);
        return res;
    }

    public NoteData redoIsCalled(){
        NoteData res = textRedoHistory.removeLast();
        perfomingRedo = true;
        return res;
    }

    public boolean canRedo(){
        Log.d(TAG, "canRedo: iscalled");
        return textRedoHistory.size() > 0;
    }

    public boolean canUndo(){

        Log.d(TAG, "canUndo: iscalled");
        return textUndoHistory.size() > 0;
    }

    public void clear(){
        textRedoHistory.clear();
        textUndoHistory.clear();
        Log.d(TAG, "clear: is called");
    }



}
