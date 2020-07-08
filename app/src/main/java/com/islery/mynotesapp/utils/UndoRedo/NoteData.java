package com.islery.mynotesapp.utils.UndoRedo;

import java.util.Objects;

public class NoteData {
    int id;
    String text;

    public NoteData(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "NoteData{" +
                "id=" + id +
                ", text=" + text +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteData noteData = (NoteData) o;
        return id == noteData.id &&
                Objects.equals(text, noteData.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
