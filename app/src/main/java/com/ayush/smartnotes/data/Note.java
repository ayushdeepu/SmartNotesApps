package com.ayush.smartnotes.data;

/** A note stored entirely on the device. */
public class Note {
    public long id;
    public String title;
    public String content;
    public String category;
    public boolean pinned;
    public long createdAt;
    public long updatedAt;

    public Note(long id, String title, String content, String category,
                boolean pinned, long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.pinned = pinned;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
