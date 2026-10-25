package com.ayush.smartnotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.smartnotes.data.Note;
import com.ayush.smartnotes.data.NotesDbHelper;
import com.ayush.smartnotes.util.SmartText;

/** Create and edit notes, with safe automatic saving on exit. */
public class EditorActivity extends Activity {
    private static final String STATE_ID = "saved_note_id";
    private final String[] categories = {"Personal", "Study", "Work", "Ideas"};
    private TextView[] categoryViews;
    private TextView heading, wordCount, pinToggle;
    private android.widget.ImageButton deleteButton;
    private EditText titleInput, bodyInput;
    private NotesDbHelper database;
    private long noteId = -1L;
    private long originalCreatedAt;
    private boolean pinned = false;
    private boolean dirty = false;
    private boolean suppressAutoSave = false;
    private String category = "Personal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        MainActivity.applySystemBars(findViewById(R.id.editorRoot));
        database = new NotesDbHelper(this);
        titleInput = findViewById(R.id.titleInput);
        bodyInput = findViewById(R.id.bodyInput);
        heading = findViewById(R.id.editorHeading);
        wordCount = findViewById(R.id.wordCount);
        pinToggle = findViewById(R.id.pinToggle);
        deleteButton = findViewById(R.id.deleteButton);
        noteId = savedInstanceState != null ? savedInstanceState.getLong(STATE_ID, -1L)
                : getIntent().getLongExtra(MainActivity.EXTRA_NOTE_ID, -1L);

        if (noteId != -1L) {
            Note note = database.findById(noteId);
            if (note == null) {
                Toast.makeText(this, "This note was deleted", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            originalCreatedAt = note.createdAt;
            titleInput.setText(note.title);
            bodyInput.setText(note.content);
            category = note.category;
            pinned = note.pinned;
            heading.setText("Edit note");
            deleteButton.setVisibility(View.VISIBLE);
        }

        categoryViews = new TextView[]{findViewById(R.id.editPersonal),
                findViewById(R.id.editStudy), findViewById(R.id.editWork),
                findViewById(R.id.editIdeas)};
        for (int i = 0; i < categoryViews.length; i++) {
            final int index = i;
            categoryViews[i].setOnClickListener(v -> {
                if (!category.equals(categories[index])) {
                    category = categories[index];
                    dirty = true;
                    updateCategoryViews();
                }
            });
        }
        updateCategoryViews();
        updatePinButton();
        updateWordCount();

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                dirty = true;
                updateWordCount();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        titleInput.addTextChangedListener(watcher);
        bodyInput.addTextChangedListener(watcher);

        findViewById(R.id.backButton).setOnClickListener(v -> exitEditor());
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            if (saveNote(true)) finish();
        });
        deleteButton.setOnClickListener(v -> confirmDelete());
        findViewById(R.id.shareButton).setOnClickListener(v -> shareNote());
        findViewById(R.id.autoTitleButton).setOnClickListener(v -> {
            String content = bodyInput.getText().toString();
            if (content.trim().isEmpty()) {
                Toast.makeText(this, "Write some note text first", Toast.LENGTH_SHORT).show();
            } else {
                titleInput.setText(SmartText.suggestTitle(content));
                Toast.makeText(this, "Title suggested from your note", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.summaryButton).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Quick preview")
                        .setMessage(SmartText.quickPreview(bodyInput.getText().toString()))
                        .setPositiveButton("OK", null)
                        .show());
        pinToggle.setOnClickListener(v -> {
            pinned = !pinned;
            dirty = true;
            updatePinButton();
        });
    }

    private void updateCategoryViews() {
        for (int i = 0; i < categoryViews.length; i++) {
            boolean selected = categories[i].equals(category);
            categoryViews[i].setBackgroundResource(selected ?
                    R.drawable.bg_chip_selected : R.drawable.bg_chip);
            categoryViews[i].setTextColor(getColor(selected ? R.color.accent : R.color.muted));
        }
    }

    private void updatePinButton() {
        pinToggle.setText(pinned ? "Pinned ✓" : "Pin note");
        pinToggle.setTextColor(getColor(pinned ? R.color.accent : R.color.muted));
        pinToggle.setBackgroundResource(pinned ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
    }

    private void updateWordCount() {
        String body = bodyInput.getText().toString();
        wordCount.setText(SmartText.wordCount(body) + " words · " +
                body.length() + " characters");
    }

    /** Returns true for success, including an intentionally empty new draft. */
    private boolean saveNote(boolean showToast) {
        if (!dirty && noteId != -1L) return true;
        String title = titleInput.getText().toString().trim();
        String content = bodyInput.getText().toString().trim();
        if (noteId == -1L && title.isEmpty() && content.isEmpty()) return true;
        boolean generatedTitle = title.isEmpty();
        if (generatedTitle) title = SmartText.suggestTitle(content);
        boolean wasNewNote = noteId == -1L;
        Note note = new Note(noteId, title, content, category, pinned,
                originalCreatedAt, System.currentTimeMillis());
        try {
            noteId = database.save(note);
            originalCreatedAt = note.createdAt;
            if (generatedTitle) titleInput.setText(title);
            dirty = false;
            if (wasNewNote) {
                heading.setText("Edit note");
                deleteButton.setVisibility(View.VISIBLE);
            }
            if (showToast) Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't save note. Please try again.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void exitEditor() {
        if (saveNote(false)) finish();
    }

    private void confirmDelete() {
        if (noteId == -1L) return;
        new AlertDialog.Builder(this)
                .setTitle("Delete note?")
                .setMessage("This will permanently remove the note from your device.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    try {
                        database.delete(noteId);
                        suppressAutoSave = true;
                        dirty = false;
                        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not delete note", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void shareNote() {
        String title = titleInput.getText().toString().trim();
        String content = bodyInput.getText().toString().trim();
        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Write something before sharing", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT,
                (title.isEmpty() ? "" : title + "\n\n") + content);
        startActivity(Intent.createChooser(share, "Share note with"));
    }

    @Override
    public void onBackPressed() {
        exitEditor();
    }

    @Override
    protected void onPause() {
        // Handles Home, app switching and screen lock as well as the back button.
        if (!suppressAutoSave && dirty) saveNote(false);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save before rotation to avoid creating a duplicate new note.
        if (!suppressAutoSave && dirty) saveNote(false);
        outState.putLong(STATE_ID, noteId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
