package com.example.android.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteKeeperProviderContract {
    private NoteKeeperProviderContract(){}

    public static final String AUTHORITY = "com.example.android.notekeeper.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CourseIdColumn{
        String COLUMN_COURSE_ID = "course_id";
    }
    protected interface NotesColumns{
        String COLUMN_NOTE_TITLE = "note_title";
        String COLUMN_NOTE_TEXT = "note_text";
    }

    protected interface CoursesColumns{
        String COLUMN_COURSE_TITLE = "course_title";
    }

    public static final class Courses implements BaseColumns , CourseIdColumn , CoursesColumns{
        public static final String PATH = "courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI , PATH);
    }

    public static final class Notes implements BaseColumns , CourseIdColumn , NotesColumns , CoursesColumns{
        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI , PATH);
        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI , PATH_EXPANDED);
    }
}
