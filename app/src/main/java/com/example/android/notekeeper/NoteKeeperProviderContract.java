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
        public static final String path = "courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI , path);
    }

    public static final class Notes implements BaseColumns , CourseIdColumn , NotesColumns{
        public static final String path = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI , path);
    }
}
