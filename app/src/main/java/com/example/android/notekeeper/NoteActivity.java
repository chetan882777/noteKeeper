package com.example.android.notekeeper;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.android.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.android.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.example.android.notekeeper.NoteKeeperProviderContract.Courses;
import com.example.android.notekeeper.NoteKeeperProviderContract.Notes;

import java.util.List;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ORIGINAL_NOTE_COURSE_ID  = "com.example.android.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE  = "com.example.android.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT  = "com.example.android.notekeeper.ORIGINAL_NOTE_TEXT";

    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourse;
    private EditText mTextNoteTitle;
    private EditText mTextNotetext;
    private int mNotePosition;
    private boolean mIsCanceling;
    private String mOriginalCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private int mPosition;
    private NoteKeeperOpenHelper mOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;
    private SimpleCursorAdapter mAdapterCourses;
    private final int LOADER_NOTE_ID = 0;
    private final int LOADER_COURSES_ID = 1;
    private Boolean mCoursesQueryFinished;
    private Boolean mNotesQueryFinished;
    private Uri mNoteUri;
    private String TAG = NoteActivity.class.getSimpleName();

    @Override
    protected void onDestroy() {
        mOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mOpenHelper = new NoteKeeperOpenHelper(this);

        // Refernce to spinner in == content_note.xml
        mSpinnerCourse = (Spinner) findViewById(R.id.spinner_courses);

        mAdapterCourses = new SimpleCursorAdapter
                (this , android.R.layout.simple_spinner_item , null ,
                        new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE} ,
                        new int[]{android.R.id.text1} ,0);

        mAdapterCourses.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        mSpinnerCourse.setAdapter(mAdapterCourses);

        getLoaderManager().initLoader(LOADER_COURSES_ID , null , this);

        readDisplayStateValues();


        mTextNoteTitle = findViewById(R.id.text_title_note);
        mTextNotetext = findViewById(R.id.text_note_text);

        if(!mIsNewNote) {
            getLoaderManager().initLoader(LOADER_NOTE_ID , null , this);
        }

        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        }else{
            restoreOriginalValues(savedInstanceState);
        }
    }

    private void loadCourseData() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        String[] coursesColumns = new String[]{
                CourseInfoEntry.COLUMN_COURSE_TITLE ,
                CourseInfoEntry.COLUMN_COURSE_ID ,
                CourseInfoEntry._ID
        };

        Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME , coursesColumns ,
                null , null, null , null ,
                CourseInfoEntry.COLUMN_COURSE_TITLE);

        mAdapterCourses.changeCursor(cursor);
    }

    private void loadNoteData() {


        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        mNoteCursor.moveToNext();
        displayNote();
    }

    private void restoreOriginalValues(Bundle savedInstanceState) {
        mOriginalCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE); }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID , mOriginalCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE  , mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT , mOriginalNoteText);
    }

    private void saveOriginalNoteValues() {
        if(mIsNewNote)
            return;
        mOriginalCourseId = mNoteCursor.getString(mCourseIdPos);
        mOriginalNoteTitle = mNoteCursor.getString(mNoteTitlePos);
        mOriginalNoteText = mNoteCursor.getString(mNoteTextPos);
    }

    private void displayNote() {
        String coursId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);

        mTextNotetext.setText(noteText);
        mTextNoteTitle.setText(noteTitle);

        int courseindex = getIndexOfCourseId(coursId);
        mSpinnerCourse.setSelection(courseindex);
    }

    private int getIndexOfCourseId(String courseId){
        Cursor cursor = mAdapterCourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int coursorRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while (more){
            String coursorCourseId = cursor.getString(courseIdPos);

            if(coursorCourseId.equals(courseId))
                break;

            coursorRowIndex++;
            more = cursor.moveToNext();
        }
        return coursorRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNotePosition = intent.getIntExtra(NoteInfo.NOTE_POSITION , POSITION_NOT_SET);
        mIsNewNote = mNotePosition == POSITION_NOT_SET;
        if(mIsNewNote) {
            createNewNote();
        }
         //   mNote = DataManager.getInstance().getNotes().get(mNotePosition);

    }

    private void createNewNote() {
        AsyncTask<ContentValues , Void , Uri> task = new AsyncTask<ContentValues, Void, Uri>() {

            private ProgressBar progressBar = null;

            @Override
            protected void onPreExecute() {
                progressBar = findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(1);
            }

            @Override
            protected Uri doInBackground(ContentValues... contentValues) {
                ContentValues insertValue = contentValues[0];

                Log.d(TAG , "doInBackground method " + Thread.currentThread().getId());
                simulateLongRunningWork();

                simulateLongRunningWork();
                Uri rowUri = getContentResolver().insert(Notes.CONTENT_URI , insertValue);
                return rowUri;
            }

            @Override
            protected void onPostExecute(Uri uri) {
                mNoteUri = uri;

                displaySnackbar(mNoteUri.toString());
                Log.d(TAG , "onPostExecute method " + Thread.currentThread().getId());
            }
        };

        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_COURSE_ID , "");
        values.put(Notes.COLUMN_NOTE_TITLE , "");
        values.put(Notes.COLUMN_NOTE_TEXT , "");

        Log.d(TAG , "Call to execute " + Thread.currentThread().getId());
        task.execute(values);
         }


    private void displaySnackbar(String message) {
        View view = findViewById(R.id.spinner_courses);
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    private void simulateLongRunningWork() {
        try {
            Thread.sleep(2000);
        } catch(Exception ex) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendMail();
            return true;
        }else if (id == R.id.action_cancel_note){
            mIsCanceling = true;
            finish();
        } else if(id == R.id.action_next){
            moveNext();
        }else if (id == R.id.action_set_notification){
            setNotificationReminder();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNotificationReminder() {
        String noteText = mTextNotetext.getText().toString();
        String noteTitle = mTextNoteTitle.getText().toString();
        int noteId= (int) ContentUris.parseId(mNoteUri);
        NoteReminderNotification.notify(this , noteText , noteTitle, noteId);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        menuItem.setEnabled(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValues();
        displayNote();
        invalidateOptionsMenu();
    }

    private void sendMail() {
        CourseInfo course = (CourseInfo) mSpinnerCourse.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what i learned in plural sight course \""
                + course.getTitle() + "\"\n" + mTextNotetext.getText().toString();

        Intent mailIntent = new Intent(Intent.ACTION_SEND);

        mailIntent.setType("message/rfc2822");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT , subject);
        mailIntent.putExtra(Intent.EXTRA_TEXT , text);

        startActivity(mailIntent);
        }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCanceling){
            if(mIsNewNote){
                deleteNoteFormDatabase();
            }
            storePreviousNoteValues();
        }else {
            saveNote();
        }
    }

    private void deleteNoteFormDatabase() {
        final String selection = NoteInfoEntry._ID + " = ?";
        final String[] slection_args = {Integer.toString(mNotePosition)};

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                db.delete(NoteInfoEntry.TABLE_NAME , selection , slection_args);
                return null;
            }
        };

    task.execute();
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalCourseId);
        mNote.setCourse(course);
        mNote.setText(mOriginalNoteText);
        mNote.setTitle(mOriginalNoteTitle);

    }

    private void saveNote() {
        String courseId = selectedCourseId();
        String noteTitle = mTextNoteTitle.getText().toString();
        String noteText = mTextNotetext.getText().toString();

        saveNoteToDatabase(courseId , noteTitle , noteText);
    }

    private String selectedCourseId() {
        int selectedPosition = mSpinnerCourse.getSelectedItemPosition();

        Cursor cursor = mAdapterCourses.getCursor();
        cursor.moveToPosition(selectedPosition);

        String courseId = cursor.getString(mCourseIdPos);

        return courseId;
    }


    private void saveNoteToDatabase(String courseId , String noteTitle , String noteText){
        String selection = NoteInfoEntry._ID + " = ?";
        String[] slelection_args = {Integer.toString(mNotePosition)};

        ContentValues values = new ContentValues();
        values.put(NoteInfoEntry.COLUMN_COURSE_ID , courseId);
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE , noteTitle);
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT , noteText);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        db.update(NoteInfoEntry.TABLE_NAME , values , selection , slelection_args);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader =  null;

        if(id == LOADER_NOTE_ID){
            loader = createLoaderNotes();
        }else if(id == LOADER_COURSES_ID){
            loader = createLoaderCourses();
        }
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        mCoursesQueryFinished = false;

        Uri uri = Courses.CONTENT_URI;


        String[] coursesColumns = new String[]{
                Courses.COLUMN_COURSE_TITLE ,
                Courses.COLUMN_COURSE_ID ,
                Courses._ID
        };

        return new CursorLoader(this , uri , coursesColumns ,
                null , null ,Courses.COLUMN_COURSE_TITLE);
    }

    private CursorLoader createLoaderNotes() {
        mNotesQueryFinished = false;
        String[] noteColumns =   {
                Notes.COLUMN_COURSE_ID ,
                Notes.COLUMN_NOTE_TITLE ,
                Notes.COLUMN_NOTE_TEXT };

        mNoteUri = ContentUris.withAppendedId(Notes.CONTENT_URI , mNotePosition);

        return new CursorLoader(this , mNoteUri , noteColumns ,
                null , null , null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTE_ID){
            loadFinishedNotes(data);
        }else if(loader.getId() == LOADER_COURSES_ID){
            mAdapterCourses.changeCursor(data);
            mCoursesQueryFinished = true;
            displayNoteWhenQuariesFinished();
        }
    }

    private void loadFinishedNotes(Cursor data) {
        mNoteCursor = data;
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        mNoteCursor.moveToNext();
        mNotesQueryFinished = true;

        displayNoteWhenQuariesFinished();
    }

    private void displayNoteWhenQuariesFinished() {
        if(mNotesQueryFinished && mCoursesQueryFinished){
            displayNote();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTE_ID){
            if(mNoteCursor != null)
                mNoteCursor.close();
        }else if(loader.getId() == LOADER_COURSES_ID){
            mAdapterCourses.changeCursor(null);
        }
    }
}

