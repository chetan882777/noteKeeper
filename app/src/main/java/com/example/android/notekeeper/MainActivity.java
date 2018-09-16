package com.example.android.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerItem;
    private LinearLayoutManager mNotesLayoutManager;
    private GridLayoutManager mCoursesLayoutManager;
    private CourseRecyclerAdapter mMCoursesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , NoteActivity.class);
                startActivity(intent);
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.pref_general , false);

        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync , false);

        PreferenceManager.setDefaultValues(this, R.xml.pref_notification , false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }

    private void initializeDisplayContent() {

        mRecyclerItem = (RecyclerView) findViewById(R.id.recycler_note_item);
        mNotesLayoutManager = new LinearLayoutManager(this);
        mCoursesLayoutManager = new GridLayoutManager(this , getResources().getInteger(R.integer.ocurse_grid_span) );

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mMCoursesRecyclerAdapter = new CourseRecyclerAdapter(this , courses);

        displayNotes();
    }

    private void displayNotes() {
        mRecyclerItem.setLayoutManager(mNotesLayoutManager);
        mRecyclerItem.setAdapter(mNoteRecyclerAdapter);
        selectNavigationNemuItem(R.id.nav_notes);
    }

    private void selectNavigationNemuItem(int id) {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = nv.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses() {
        mRecyclerItem.setLayoutManager(mCoursesLayoutManager);
        mRecyclerItem.setAdapter(mMCoursesRecyclerAdapter);
        selectNavigationNemuItem(R.id.nav_courses);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mNoteRecyclerAdapter.notifyDataSetChanged();
        updateNavheader();
    }

    private void updateNavheader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView textUserName = (TextView) findViewById(R.id.text_user_name);
        TextView textUserEmail = (TextView) findViewById(R.id.text_email_address);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("user_display_name" , "");
        String userEmail = pref.getString("user_email_address" , "");

        textUserEmail.setText(userEmail);
        textUserName.setText(userName);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(this , SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_courses) {
            displayCourses();

        } else if (id == R.id.nav_notes) {
            displayNotes();

        } else if (id == R.id.nav_share) {
            handleShare();

        } else if (id == R.id.nav_send) {
            handleSelection(getString(R.string.nav_send_message));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.recycler_note_item);
        Snackbar.make(view , "Share to --" + PreferenceManager.getDefaultSharedPreferences(this)
                .getString("user_fav_social" , ""), Snackbar.LENGTH_LONG).show();

    }

    private void handleSelection(String message) {
        View view = findViewById(R.id.recycler_note_item);
        Snackbar.make(view , message , Snackbar.LENGTH_LONG).show();
    }


}
