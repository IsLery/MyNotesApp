package com.islery.mynotesapp.ui.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.islery.mynotesapp.data.NoteRepository;
import com.islery.mynotesapp.data.models.Note;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteListViewModel extends AndroidViewModel {

    private LiveData<List<Note>> notesList;
    private NoteRepository repository;
    private Disposable disposable;
    private String searchQuery;
    private Pair<Long, Long> dateRange;
    private MutableLiveData<Map<Filters,Boolean>> filters;

    public NoteListViewModel(@NonNull Application application) {
        super(application);
        repository = NoteRepository.getInstance(application.getApplicationContext());
        searchQuery = "";
        Map<Filters,Boolean> mapFilters = new HashMap<>(2);
        //SORT BY DATE or ALPHABETIC
        mapFilters.put(Filters.SORT_BY_ALPHA,false);
        //ASC OR DESC
        mapFilters.put(Filters.ORDER_ASC,false);
        //HAS SEARCH
        mapFilters.put(Filters.HAS_SEARCH,false);
        //DATE RANGE OR ALL NOTES
        mapFilters.put(Filters.DATA_IN_RANGE,false);
        //IF RANGE SELECTION WAS CHANGED
        mapFilters.put(Filters.DATA_RANGE_TRIGGER,false);
        filters = new MutableLiveData<>(mapFilters);


        notesList = Transformations.switchMap(filters,map -> {
            if (dateRange!=null){
                if (map.get(Filters.HAS_SEARCH)){
                    return repository.getSearchInDateRange(dateRange,map.get(Filters.ORDER_ASC),map.get(Filters.SORT_BY_ALPHA),searchQuery);
                }
                return repository.getNotesInDateRange(dateRange,map.get(Filters.ORDER_ASC),map.get(Filters.SORT_BY_ALPHA));
            }
            if (map.get(Filters.HAS_SEARCH)){
               return repository.getOrderedSearchNotes(map.get(Filters.ORDER_ASC),map.get(Filters.SORT_BY_ALPHA),searchQuery);
            }else {
               return repository.getOrderedNotes(map.get(Filters.ORDER_ASC),map.get(Filters.SORT_BY_ALPHA));
            }
        });

    }

    LiveData<List<Note>> getNoteLiveData(){
        return notesList;
    }

    void getSearchResult(String query){
        searchQuery = query;
        Map<Filters,Boolean> map = filters.getValue();
        map.put(Filters.HAS_SEARCH,true);
        filters.setValue(map);

    }



    void clearSearch(){
        searchQuery = "";
        Map<Filters,Boolean> map = filters.getValue();
        map.put(Filters.HAS_SEARCH,false);
        filters.setValue(map);
    }

    void deleteNote(final Note note){
        Callable<Void> clb = () -> {
            repository.deleteNoteTask(note);
            return null;
        };
       disposable = Completable.fromCallable(clb).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    //if already set - change order, returns false if set
    public boolean setAlphaSort(){
        Map<Filters,Boolean> map = filters.getValue();
        if (map.get(Filters.SORT_BY_ALPHA)){
            map.put(Filters.ORDER_ASC,!map.get(Filters.ORDER_ASC));
            filters.setValue(map);
            return false;
        }else {
            map.put(Filters.SORT_BY_ALPHA,true);
            map.put(Filters.ORDER_ASC,true);
            filters.setValue(map);
            return true;
        }
    }

    public boolean setDateSort(){
        Map<Filters,Boolean> map = filters.getValue();
        if (!map.get(Filters.SORT_BY_ALPHA)){
            map.put(Filters.ORDER_ASC,!map.get(Filters.ORDER_ASC));
            filters.setValue(map);
            return false;
        }else {
            map.put(Filters.SORT_BY_ALPHA,false);
            map.put(Filters.ORDER_ASC,false);
            filters.setValue(map);
            return true;
        }
    }

    public void setDateRange(Pair<Long, Long> selection){
        if (selection.first != null && selection.second != null) {
            Map<Filters, Boolean> map = filters.getValue();
            if (dateRange == null) {
                dateRange = selection;
                map.put(Filters.DATA_IN_RANGE, true);
            }else if (dateRange.first != selection.first || dateRange.second != selection.second){
                dateRange = selection;
                map.put(Filters.DATA_RANGE_TRIGGER,!map.get(Filters.DATA_RANGE_TRIGGER));
            }
            filters.setValue(map);
        }
    }

    public void clearDateRange(){
        dateRange = null;
        Map<Filters,Boolean> map = filters.getValue();
        map.put(Filters.DATA_IN_RANGE,false);
        filters.setValue(map);
    }

    public Pair<Long, Long> getDateRange() {
        return dateRange;
    }

    public boolean isCurrentOrderAsc(){
        return filters.getValue().get(Filters.ORDER_ASC);
    }

    public boolean isCurrentSortAlpha(){
        return filters.getValue().get(Filters.SORT_BY_ALPHA);
    }


    @Override
    protected void onCleared() {
        if (disposable != null){
            disposable.dispose();
        }
        super.onCleared();
    }


}
