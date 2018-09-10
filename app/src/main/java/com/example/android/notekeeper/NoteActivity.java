package com.example.android.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourse;
    private EditText mTextNoteTitle;
    private EditText mTextNotetext;
    private int mNotePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Refernce to spinner in == content_note.xml
        mSpinnerCourse = (Spinner) findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>
                        (this , android.R.layout.simple_spinner_item , courses);

        adapterCourses.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        mSpinnerCourse.setAdapter(adapterCourses);

        readDisplayStateValues();

        mTextNoteTitle = findViewById(R.id.text_title_note);
        mTextNotetext = findViewById(R.id.text_note_text);

        if(!mIsNewNote) {
            displayNote(mSpinnerCourse, mTextNoteTitle, mTextNotetext);
        }
    }

    private void displayNote(Spinner spinnerCourse, EditText textNoteTitle, EditText textNotetext) {
        String t = mNote.getText();
        textNotetext.setText(t);
        String t2 = mNote.getTitle();
        textNoteTitle.setText(t2);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseindex = courses.indexOf(mNote.getCourse());
        spinnerCourse.setSelection(courseindex);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NoteInfo.NOTE_POSITION , POSITION_NOT_SET);
        mIsNewNote = position == POSITION_NOT_SET;
        if(mIsNewNote) {
            createNewNote();
        }else{
            mNote = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);
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
        if (id == R.id.send_send_mail) {
            sendMail();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        saveNote();
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourse.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNotetext.getText().toString());
    }
}

