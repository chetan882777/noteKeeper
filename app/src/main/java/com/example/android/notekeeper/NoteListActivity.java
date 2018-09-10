package com.example.android.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteListActivity.this , NoteActivity.class);
                startActivity(intent);
            }
        });
        initializeDisplayContent();
    }

    private void initializeDisplayContent() {

        final ListView listNotes = (ListView) findViewById(R.id.list_note);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        ArrayAdapter<NoteInfo> adapterNote = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , notes);

        listNotes.setAdapter(adapterNote);

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NoteListActivity.this , NoteActivity.class);
                NoteInfo mNote = (NoteInfo) listNotes.getItemAtPosition(i);
                String t = mNote.getText();
                intent.putExtra(NoteInfo.NOTE_INFO , mNote);
                startActivity(intent);
            }
        });
    }

}
