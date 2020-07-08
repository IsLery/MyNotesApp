package com.islery.mynotesapp.utils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;


import com.islery.mynotesapp.data.models.Note;

import java.util.List;

public class NoteDiffUtilsCallback extends DiffUtil.Callback {
    List<Note> oldList, newList;

    public NoteDiffUtilsCallback(List<Note> oldList, List<Note> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Note oldItem = oldList.get(oldItemPosition);
        Note newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Note oldItem = oldList.get(oldItemPosition);
        Note newItem = newList.get(newItemPosition);
        return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getContent().equals(newItem.getContent());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Note oldItem = oldList.get(oldItemPosition);
        Note newItem = newList.get(newItemPosition);
        Bundle bundle = new Bundle();
        if (!oldItem.getTitle().equals(newItem.getTitle())){
            bundle.putString("title",newItem.getTitle());
        }
        if (!oldItem.getContent().equals(newItem.getContent())){
            bundle.putString("content",newItem.getContent());
        }
        if (bundle.size() == 0){
            return null;
        }
        else return bundle;
    }
}
