package com.example.android.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.android.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.android.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

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

        readDisplayStateValues();


        mTextNoteTitle = findViewById(R.id.text_title_note);
        mTextNotetext = findViewById(R.id.text_note_text);

        if(!mIsNewNote) {
            loadNoteData();
        }

        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        }else{
            restoreOriginalValues(savedInstanceState);
        }
    }

    private void loadNoteData() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

       // String courseId = "android_intents";
       // String titleStart = "dynamic";

        int id = mNotePosition;

        String selelction = NoteInfoEntry._ID + " = ?" ;

        String[] selectionArgs = {Integer.toString(id)};

        String[] noteColumns =   {
                NoteInfoEntry.COLUMN_COURSE_ID ,
                NoteInfoEntry.COLUMN_NOTE_TITLE ,
                NoteInfoEntry.COLUMN_NOTE_TEXT };

        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME ,
                noteColumns ,
                selelction ,
                selectionArgs ,
                null , null , null);

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
       // String t = mNote.getText();
        mTextNotetext.setText(noteText);
        //String t2 = mNote.getTitle();
        mTextNoteTitle.setText(noteTitle);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        CourseInfo course = DataManager.getInstance().getCourse(coursId);
        int courseindex = courses.indexOf(course);
        mSpinnerCourse.setSelection(courseindex);
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
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
      //  mNote = dm.getNotes().get(mNotePosition);
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
        }

        return super.onOptionsItemSelected(item);
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
                DataManager.getInstance().removeNote(mNotePosition);
            }
            storePreviousNoteValues();
        }else {
           // saveNote();
        }
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalCourseId);
        mNote.setCourse(course);
        mNote.setText(mOriginalNoteText);
        mNote.setTitle(mOriginalNoteTitle);

    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourse.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNotetext.getText().toString());
    }
}

