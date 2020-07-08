package com.islery.mynotesapp.ui.note;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


import com.islery.mynotesapp.data.models.Note;
import com.islery.mynotesapp.data.NoteRepository;
import com.islery.mynotesapp.utils.UndoRedo.UndoRedoTextManager;


import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteViewModel extends AndroidViewModel {
        private NoteRepository repository;
        private Disposable disposable;
        private UndoRedoTextManager manager;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application.getApplicationContext());
        manager = new UndoRedoTextManager();
    }

   public void saveNewNote(final Note note){
       Callable<Void> callable = new Callable<Void>() {
           @Override
           public Void call() throws Exception {
               repository.insertNoteTask(note);
               return null;
           }
       };
       disposable = Completable.fromCallable(callable).subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe();
   }

   public void updateNote(final Note note){
       Callable<Void> callable = new Callable<Void>() {
           @Override
           public Void call() throws Exception {
               repository.updateNoteTask(note);
               return null;
           }
       };
       disposable = Completable.fromCallable(callable).subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe();
   }

    public void deleteNote(final Note note){
        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                repository.deleteNoteTask(note);
                return null;
            }
        };
        disposable = Completable.fromCallable(callable).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    protected void onCleared() {
        if (disposable != null){
            disposable.dispose();
        }
        super.onCleared();
    }

    public UndoRedoTextManager getManager(){
        return manager;
    }
}
