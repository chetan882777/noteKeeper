package com.example.android.notekeeper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Before
    public void setUp() throws Exception{
        DataManager dm = DataManager.getInstance();
        dm.getNotes().clear();
        dm.initializeExampleNotes();
    }

    @Test
    public void createNewNote() {
        DataManager dm = DataManager.getInstance();
        CourseInfo course = dm.getCourse("android_async");
        String testTitle =  "test note title";
        String testText = "hi this is test note's text";

        int noteIndex = dm.createNewNote();
        NoteInfo testNote = dm.getNotes().get(noteIndex);
        testNote.setTitle(testTitle);
        testNote.setText(testText);
        testNote.setCourse(course);

        NoteInfo compareNote = dm.getNotes().get(noteIndex);

        /*
            assertSame(compareNote , testNote);

            here applying assertSame is not really effective as it checks for references but we
            need here to check ,is title and others are same or not !
        */

        assertEquals(compareNote.getCourse() , course);
        assertEquals(compareNote.getText() , testText);
        assertEquals(compareNote.getTitle() , testTitle);

    }

    @Test
    public void findSimilarNotes() throws Exception{
        DataManager dm = DataManager.getInstance();
        CourseInfo course = dm.getCourse("android_async");
        String testTitle =  "test note title";
        String testText = "hi this is test note's text";
        String testText2 = "hi this is test note's text2";

        int noteIndex1 = dm.createNewNote();
        NoteInfo testNote1 = dm.getNotes().get(noteIndex1);
        testNote1.setTitle(testTitle);
        testNote1.setText(testText);
        testNote1.setCourse(course);

        int noteIndex2 = dm.createNewNote();
        NoteInfo testNote2 = dm.getNotes().get(noteIndex2);
        testNote2.setTitle(testTitle);
        testNote2.setText(testText2);
        testNote2.setCourse(course);

        int foundIndex1 = dm.findNote(testNote1);
        assertEquals(foundIndex1 , noteIndex1);


        int foundIndex2 = dm.findNote(testNote2);
        assertEquals(foundIndex2 , noteIndex2);
    }
}