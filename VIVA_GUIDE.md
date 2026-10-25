# Smart Notes App — Easy Viva Guide (Java + XML)

## 30-second introduction
My project is **Smart Notes App**, an offline Android application made using **Java** and **XML** in Android Studio. It allows users to create, edit, search, categorize, pin, share and delete notes. I used **SQLite** to store notes on the device, so no internet or login is needed.

## Simple flow
1. Android opens `MainActivity.java` and displays `activity_main.xml`.
2. `NotesDbHelper.java` loads notes from a local SQLite database.
3. `NoteAdapter.java` shows each note inside a `ListView`.
4. When a note is tapped or added, `EditorActivity.java` shows `activity_editor.xml`.
5. Saving a note inserts or updates data in SQLite.

## Common viva questions

**Q1. What is Android Studio?**  An IDE for developing Android applications.

**Q2. Why use Java?**  To write the logic and handle user actions.

**Q3. Why use XML?**  To design the app's screens and UI elements.

**Q4. What is an Activity?**  An Android component representing a screen.

**Q5. What is SQLite?**  A lightweight local database used for saving notes offline.

**Q6. What is SQLiteOpenHelper?**  A helper class that creates and manages the SQLite database.

**Q7. How does search work?**  The app checks note titles and content for the search text and filters the list.

**Q8. How are notes sorted?**  Pinned notes first, then recently updated notes.

**Q9. What is an Intent?**  A way to open another Activity or share text with other applications.

**Q10. What makes the app 'smart'?**  It suggests a title based on the first line of text and shows a quick preview. These are simple offline text rules, not AI services.

**Q11. Is an internet connection needed?**  No. Notes are stored on the phone.

**Q12. How does editing work?**  An existing note's ID is passed to the editor. SQLite updates that record instead of adding a duplicate.

## Demo to show your teacher
1. Open the app and show sample notes.
2. Tap **New note**, enter a title and text, select **Study**, tap **Save**.
3. Search for the saved note.
4. Open it, update its text and save.
5. Pin it to the top.
6. Open it again, share or delete it.
7. Close and reopen the app to show SQLite persistence.

## Core files to remember
- `MainActivity.java` — home screen and search.
- `EditorActivity.java` — add/edit/delete/share a note.
- `NotesDbHelper.java` — SQLite database.
- `Note.java` — one note's data fields.
- `NoteAdapter.java` — note rows in a ListView.
- `SmartText.java` — quick preview, title suggestion and word count.
- `activity_main.xml`, `activity_editor.xml`, `item_note.xml` — XML layout files.
