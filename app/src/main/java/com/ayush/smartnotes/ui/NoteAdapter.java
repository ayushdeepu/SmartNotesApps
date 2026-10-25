package com.ayush.smartnotes.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ayush.smartnotes.R;
import com.ayush.smartnotes.data.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Renders efficient recycled rows in the note list. */
public class NoteAdapter extends BaseAdapter {
    public interface PinListener { void onPin(Note note); }

    private final LayoutInflater inflater;
    private final Context context;
    private final PinListener pinListener;
    private final SimpleDateFormat formatter =
            new SimpleDateFormat("dd MMM · h:mm a", Locale.getDefault());
    private final List<Note> data = new ArrayList<>();

    public NoteAdapter(Context context, PinListener pinListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.pinListener = pinListener;
    }

    public void replace(List<Note> notes) {
        data.clear();
        data.addAll(notes);
        notifyDataSetChanged();
    }

    @Override public int getCount() { return data.size(); }
    @Override public Note getItem(int position) { return data.get(position); }
    @Override public long getItemId(int position) { return data.get(position).id; }
    @Override public boolean hasStableIds() { return true; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder h;
        if (row == null) {
            row = inflater.inflate(R.layout.item_note, parent, false);
            h = new Holder();
            h.title = row.findViewById(R.id.noteTitle);
            h.preview = row.findViewById(R.id.notePreview);
            h.category = row.findViewById(R.id.noteCategory);
            h.modified = row.findViewById(R.id.noteModified);
            h.pin = row.findViewById(R.id.pinButton);
            row.setTag(h);
        } else {
            h = (Holder) row.getTag();
        }

        Note note = getItem(position);
        h.title.setText(note.title);
        h.preview.setText(note.content.trim().isEmpty() ? "No additional text" :
                note.content.trim().replace('\n', ' '));
        h.category.setText(note.category);
        h.modified.setText(formatter.format(new Date(note.updatedAt)));
        h.pin.setColorFilter(context.getColor(note.pinned ? R.color.accent : R.color.muted),
                PorterDuff.Mode.SRC_IN);
        h.pin.setContentDescription(note.pinned ? "Unpin note" : "Pin note");
        h.pin.setOnClickListener(v -> pinListener.onPin(note));
        return row;
    }

    private static class Holder {
        TextView title, preview, category, modified;
        ImageButton pin;
    }
}
