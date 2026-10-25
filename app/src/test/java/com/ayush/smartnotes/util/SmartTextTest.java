package com.ayush.smartnotes.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class SmartTextTest {
    @Test public void suggestsTitleFromFirstNonBlankLine() {
        assertEquals("Finish Android assignment", SmartText.suggestTitle(
                "  \n  Finish Android assignment\nThen revise SQLite"));
    }

    @Test public void generatesFallbackTitle() {
        assertEquals("Untitled note", SmartText.suggestTitle("   "));
    }

    @Test public void wordCountHandlesWhitespace() {
        assertEquals(4, SmartText.wordCount("One  two\nthree\tfour"));
        assertEquals(0, SmartText.wordCount(" \n "));
    }

    @Test public void quickPreviewIsShort() {
        assertEquals("First sentence. Second sentence.", SmartText.quickPreview(
                "First sentence. Second sentence. Third sentence."));
        assertTrue(SmartText.quickPreview("x ".repeat(150)).length() <= 181);
    }
}
