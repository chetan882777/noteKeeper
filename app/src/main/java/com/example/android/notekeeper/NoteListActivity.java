package com.example.android.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {


    private ArrayAdapter<NoteInfo> mAdapterNote;

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

     //   final ListView listNotes = (ListView) findViewById(R.id.list_note);

       // List<NoteInfo> notes = DataManager.getInstance().getNotes();

      //  mAdapterNote = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , notes);

        //listNotes.setAdapter(mAdapterNote);

        //listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       //     @Override
         //   public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
           //     Intent intent = new Intent(NoteListActivity.this , NoteActivity.class);
 //               NoteInfo mNote = (NoteInfo) listNotes.getItemAtPosition(i);
//                String t = mNote.getText();
             //   intent.putExtra(NoteInfo.NOTE_POSITION, position);
               // startActivity(intent);
           // }
       // });

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_note_item);
        final LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(notesLayoutManager);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        final NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);

        recyclerView.setAdapter(noteRecyclerAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        mAdapterNote.notifyDataSetChanged();
    }
}
