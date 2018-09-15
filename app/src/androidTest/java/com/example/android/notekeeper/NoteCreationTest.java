package com.example.android.notekeeper;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.action.ViewActions.pressBack;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {

    public static DataManager sDataManager;
    @BeforeClass
    public static void classSetUp(){
        sDataManager = DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<NoteListActivity> mNoteListActivityRule
            = new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void onCreateNote(){
        final CourseInfo course = sDataManager.getCourse("java_lang");
        final String noteTitle = "this is the test title";
        final String noteText = "this is the test note text";

        // be aware of sequence of clicks

        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(course))).perform(click());

        onView(withId(R.id.text_title_note)).perform(typeText(noteTitle));
        onView(withId(R.id.text_note_text)).perform(typeText(noteText),
                closeSoftKeyboard());

        pressBack();
    }
}