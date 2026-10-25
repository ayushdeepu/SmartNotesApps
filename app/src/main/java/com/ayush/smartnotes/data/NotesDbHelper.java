package com.ayush.smartnotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** Local SQLite database. No server, login, or internet permission needed. */
public class NotesDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_notes.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "notes";

    public NotesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "category TEXT NOT NULL DEFAULT 'Personal'," +
                "pinned INTEGER NOT NULL DEFAULT 0," +
                "created_at INTEGER NOT NULL," +
                "updated_at INTEGER NOT NULL)");
        // Example notes make the project ready to demonstrate right after installation.
        long now = System.currentTimeMillis();
        seed(db, "Welcome to Smart Notes",
                "Your notes are saved privately on this phone. Tap + New note to add one, " +
                "use the search box to find it, and tap the pin icon to keep it on top.",
                "Personal", true, now);
        seed(db, "Study checklist",
                "Revise Java fundamentals. Practice Android layouts. Review SQLite " +
                "database operations and prepare the project demonstration.",
                "Study", false, now - 3600000L);
        seed(db, "App feature ideas",
                "Add a note reminder feature. Explore backup and restore. " +
                "Create more useful smart tools for organizing thoughts.",
                "Ideas", false, now - 7200000L);
    }

    private void seed(SQLiteDatabase db, String title, String content,
                      String category, boolean pinned, long time) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("category", category);
        values.put("pinned", pinned ? 1 : 0);
        values.put("created_at", time);
        values.put("updated_at", time);
        db.insertOrThrow(TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // New migrations can be added here without dropping existing user data.
    }

    public long save(Note note) {
        long now = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put("title", note.title);
        values.put("content", note.content);
        values.put("category", note.category);
        values.put("pinned", note.pinned ? 1 : 0);
        values.put("updated_at", now);
        if (note.id == -1L) {
            values.put("created_at", now);
            long id = getWritableDatabase().insertOrThrow(TABLE, null, values);
            note.id = id;
            note.createdAt = now;
        } else {
            int changed = getWritableDatabase().update(TABLE, values, "id=?",
                    new String[]{String.valueOf(note.id)});
            if (changed == 0) {
                throw new IllegalStateException("The note no longer exists");
            }
        }
        note.updatedAt = now;
        return note.id;
    }

    public Note findById(long id) {
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? fromCursor(cursor) : null;
        }
    }

    public List<Note> allNotes() {
        List<Note> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, null, null,
                null, null, "pinned DESC, updated_at DESC, id DESC")) {
            while (cursor.moveToNext()) {
                result.add(fromCursor(cursor));
            }
        }
        return result;
    }

    public void togglePin(Note note) {
        note.pinned = !note.pinned;
        save(note);
    }

    public void delete(long id) {
        getWritableDatabase().delete(TABLE, "id=?", new String[]{String.valueOf(id)});
    }

    private Note fromCursor(Cursor cursor) {
        return new Note(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("content")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getInt(cursor.getColumnIndexOrThrow("pinned")) == 1,
                cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
        );
    }
}
