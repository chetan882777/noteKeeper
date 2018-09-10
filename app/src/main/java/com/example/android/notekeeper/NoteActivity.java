package com.example.android.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    private NoteInfo mNote;
    private boolean mIsNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Refernce to spinner in == content_note.xml
        Spinner spinnerCourse = (Spinner) findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>
                        (this , android.R.layout.simple_spinner_item , courses);

        adapterCourses.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerCourse.setAdapter(adapterCourses);

        readDisplayStateValues();

        EditText textNoteTitle = findViewById(R.id.text_title_note);
        EditText textNotetext = findViewById(R.id.text_note_Text);

        if(!mIsNewNote) {
            displayNote(spinnerCourse, textNoteTitle, textNotetext);
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
        mNote = intent.getParcelableExtra(NoteInfo.NOTE_INFO);
        mIsNewNote = mNote == null;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
