package com.example.android.notekeeper;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    @Rule
    public ActivityTestRule<NoteListActivity> mNoteListActivityRule
            = new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void onCreateNote(){

    }
}