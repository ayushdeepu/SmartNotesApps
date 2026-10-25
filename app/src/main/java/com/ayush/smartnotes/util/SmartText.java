package com.ayush.smartnotes.util;

/** Lightweight, explainable offline text helpers (not generative AI). */
public final class SmartText {
    private SmartText() {}

    /** Suggest a title from the first nonblank line of the note's content. */
    public static String suggestTitle(String body) {
        if (body == null || body.trim().isEmpty()) return "Untitled note";
        String candidate = "";
        for (String line : body.split("\\R")) {
            if (!line.trim().isEmpty()) {
                candidate = line.trim().replaceAll("\\s+", " ");
                break;
            }
        }
        if (candidate.length() <= 48) return candidate;
        int lastSpace = candidate.lastIndexOf(' ', 47);
        return candidate.substring(0, lastSpace > 20 ? lastSpace : 45).trim() + "…";
    }

    /** A short extractive preview: at most two sentences or 180 characters. */
    public static String quickPreview(String body) {
        if (body == null || body.trim().isEmpty()) return "Write something to see a preview.";
        String cleaned = body.trim().replaceAll("\\s+", " ");
        String[] sentences = cleaned.split("(?<=[.!?])\\s+");
        String result = sentences[0];
        if (sentences.length > 1 && result.length() < 110) {
            result += " " + sentences[1];
        }
        if (result.length() > 180) {
            int space = result.lastIndexOf(' ', 177);
            result = result.substring(0, space > 80 ? space : 177).trim() + "…";
        }
        return result;
    }

    public static int wordCount(String body) {
        if (body == null || body.trim().isEmpty()) return 0;
        return body.trim().split("\\s+").length;
    }
}
