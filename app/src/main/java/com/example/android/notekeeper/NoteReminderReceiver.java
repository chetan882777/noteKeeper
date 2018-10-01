package com.example.android.notekeeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoteReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_NOTE_TITLE = "com.example.android.notekeeper.NOTE_TITLE";
    public static final String EXTRA_NOTE_TEXT = "com.example.android.notekeeper.NOTE_TEXT";
    public static final String EXTRA_NOTE_ID = "com.example.android.notekeeper.NOTE_ID";

    @Override
    public void onReceive(Context context, Intent intent) {

        String noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE);
        String noteText = intent.getStringExtra(EXTRA_NOTE_TEXT);
        int noteId = intent.getIntExtra(EXTRA_NOTE_ID , 0);

        NoteReminderNotification.notify(context , noteText , noteTitle , noteId);
    }
}
