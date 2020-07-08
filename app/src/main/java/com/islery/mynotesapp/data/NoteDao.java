package com.islery.mynotesapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.islery.mynotesapp.data.models.Note;


import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long[] insertNotes(Note... notes);
    //will return numbers of inserted, void is ok

    @Query("SELECT * FROM notes_table WHERE timestamp BETWEEN :from AND :to ORDER BY"
            + " CASE WHEN :isAsc = 1 AND :isAlphabetic = 1 THEN title END COLLATE NOCASE ASC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 1 THEN title END COLLATE NOCASE DESC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 0 THEN timestamp END DESC, "
            + "CASE WHEN :isAsc = 1 AND :isAlphabetic = 0 THEN timestamp END ASC")
    LiveData<List<Note>> getNotesInDateRange(long from, long to, boolean isAsc, boolean isAlphabetic);

    @Query("SELECT * FROM notes_table WHERE (timestamp BETWEEN :from AND :to) AND (title LIKE :query OR content LIKE :query) ORDER BY"
            + " CASE WHEN :isAsc = 1 AND :isAlphabetic = 1 THEN title END COLLATE NOCASE ASC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 1 THEN title END COLLATE NOCASE DESC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 0 THEN timestamp END DESC, "
            + "CASE WHEN :isAsc = 1 AND :isAlphabetic = 0 THEN timestamp END ASC")
    LiveData<List<Note>> getSearchInDateRange(long from, long to, boolean isAsc, boolean isAlphabetic,String query);


    @Query("SELECT * FROM notes_table ORDER BY CASE WHEN :isAsc = 1 AND :isAlphabetic = 1 THEN title END COLLATE NOCASE ASC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 1 THEN title END COLLATE NOCASE DESC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 0 THEN timestamp END DESC, "
            + "CASE WHEN :isAsc = 1 AND :isAlphabetic = 0 THEN timestamp END ASC")
    LiveData<List<Note>> getNotesOrdered(boolean isAsc, boolean isAlphabetic);

    @Query("SELECT * FROM notes_table WHERE title LIKE :query OR content LIKE :query ORDER BY CASE WHEN :isAsc = 1 AND :isAlphabetic = 1 THEN title END ASC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 1 THEN title END DESC, "
            + "CASE WHEN :isAsc = 0 AND :isAlphabetic = 0 THEN timestamp END DESC, "
            + "CASE WHEN :isAsc = 1 AND :isAlphabetic = 0 THEN timestamp END ASC")
    LiveData<List<Note>> getNoteSearchOrderedQuery(boolean isAsc, boolean isAlphabetic, String query);

    @Delete
    int delete(Note... notes);

    @Update
    int update(Note... notes);
}
