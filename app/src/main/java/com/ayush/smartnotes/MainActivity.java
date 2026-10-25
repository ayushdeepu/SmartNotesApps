package com.ayush.smartnotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.smartnotes.data.Note;
import com.ayush.smartnotes.data.NotesDbHelper;
import com.ayush.smartnotes.ui.NoteAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    public static final String EXTRA_NOTE_ID = "note_id";
    private final String[] categories = {"All notes", "Study", "Work", "Personal", "Ideas"};
    private TextView[] categoryViews;
    private String selectedCategory = "All notes";
    private EditText searchInput;
    private TextView notesCount, emptyTitle, emptySubtitle;
    private LinearLayout emptyState;
    private ListView notesList;
    private NoteAdapter adapter;
    private NotesDbHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applySystemBars(findViewById(R.id.mainRoot));
        database = new NotesDbHelper(this);
        searchInput = findViewById(R.id.searchInput);
        notesCount = findViewById(R.id.notesCount);
        emptyState = findViewById(R.id.emptyState);
        emptyTitle = findViewById(R.id.emptyTitle);
        emptySubtitle = findViewById(R.id.emptySubtitle);
        notesList = findViewById(R.id.notesList);
        adapter = new NoteAdapter(this, note -> {
            try {
                database.togglePin(note);
                showNotes();
            } catch (Exception ex) {
                Toast.makeText(this, "Could not update pin", Toast.LENGTH_SHORT).show();
            }
        });
        notesList.setAdapter(adapter);
        notesList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra(EXTRA_NOTE_ID, adapter.getItem(position).id);
            startActivity(intent);
        });
        findViewById(R.id.addNoteButton).setOnClickListener(v ->
                startActivity(new Intent(this, EditorActivity.class)));
        categoryViews = new TextView[]{
                findViewById(R.id.chipAll), findViewById(R.id.chipStudy),
                findViewById(R.id.chipWork), findViewById(R.id.chipPersonal),
                findViewById(R.id.chipIdeas)
        };
        for (int i = 0; i < categoryViews.length; i++) {
            final int index = i;
            categoryViews[i].setOnClickListener(v -> {
                selectedCategory = categories[index];
                updateChips();
                showNotes();
            });
        }
        updateChips();
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { showNotes(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (database != null) showNotes();
    }

    private void updateChips() {
        for (int i = 0; i < categoryViews.length; i++) {
            boolean chosen = categories[i].equals(selectedCategory);
            categoryViews[i].setBackgroundResource(chosen ?
                    R.drawable.bg_chip_selected : R.drawable.bg_chip);
            categoryViews[i].setTextColor(getColor(chosen ? R.color.accent : R.color.muted));
        }
    }

    private void showNotes() {
        List<Note> filtered = new ArrayList<>();
        String query = searchInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        for (Note note : database.allNotes()) {
            boolean categoryMatches = selectedCategory.equals("All notes") ||
                    selectedCategory.equals(note.category);
            boolean queryMatches = query.isEmpty() ||
                    note.title.toLowerCase(Locale.ROOT).contains(query) ||
                    note.content.toLowerCase(Locale.ROOT).contains(query);
            if (categoryMatches && queryMatches) filtered.add(note);
        }
        adapter.replace(filtered);
        notesCount.setText(filtered.size() + (filtered.size() == 1 ? " note" : " notes"));
        boolean empty = filtered.isEmpty();
        notesList.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (empty) {
            boolean filtering = !query.isEmpty() || !selectedCategory.equals("All notes");
            emptyTitle.setText(filtering ? "Nothing found" : "No notes yet");
            emptySubtitle.setText(filtering ? "Try a different search or category." :
                    "Tap New note to capture your first idea.");
        }
    }

    /** Android 15+ draws edge to edge. Leave room for system bars. */
    public static void applySystemBars(View root) {
        if (Build.VERSION.SDK_INT >= 35) {
            int left = root.getPaddingLeft(), top = root.getPaddingTop();
            int right = root.getPaddingRight(), bottom = root.getPaddingBottom();
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(left + bars.left, top + bars.top,
                        right + bars.right, bottom + bars.bottom);
                return insets;
            });
            root.requestApplyInsets();
        }
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
