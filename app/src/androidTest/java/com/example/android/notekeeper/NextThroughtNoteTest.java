package com.example.android.notekeeper;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.*;


public class NextThroughtNoteTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void nextThroughNotes(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

        onView(withId(R.id.recycler_note_item)).perform(RecyclerViewActions
                .actionOnItemAtPosition( 0 , click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        int index = 0;

        onView(withId(R.id.spinner_courses)).check(matches(withText(notes.get(index).getCourse().getTitle())));
        onView(withId(R.id.text_title_note)).check(matches(withText(notes.get(index).getTitle())));
        onView(withId(R.id.text_note_text)).check(matches(withText(notes.get(index).getText())));
    }

}