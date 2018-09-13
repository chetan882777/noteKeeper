package com.example.android.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    private static DataManager sDataManager;

    @BeforeClass
    public static void classSetup() throws Exception{
        sDataManager = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception{
        sDataManager.getNotes().clear();
        sDataManager.initializeExampleNotes();
    }

    @Test
    public void createNewNote() {
        CourseInfo course = sDataManager.getCourse("android_async");
        String testTitle =  "test note title";
        String testText = "hi this is test note's text";

        int noteIndex = sDataManager.createNewNote();
        NoteInfo testNote = sDataManager.getNotes().get(noteIndex);
        testNote.setTitle(testTitle);
        testNote.setText(testText);
        testNote.setCourse(course);

        NoteInfo compareNote = sDataManager.getNotes().get(noteIndex);

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
        CourseInfo course = sDataManager.getCourse("android_async");
        String testTitle =  "test note title";
        String testText = "hi this is test note's text";
        String testText2 = "hi this is test note's text2";

        int noteIndex1 = sDataManager.createNewNote();
        NoteInfo testNote1 = sDataManager.getNotes().get(noteIndex1);
        testNote1.setTitle(testTitle);
        testNote1.setText(testText);
        testNote1.setCourse(course);

        int noteIndex2 = sDataManager.createNewNote();
        NoteInfo testNote2 = sDataManager.getNotes().get(noteIndex2);
        testNote2.setTitle(testTitle);
        testNote2.setText(testText2);
        testNote2.setCourse(course);

        int foundIndex1 = sDataManager.findNote(testNote1);
        assertEquals(foundIndex1 , noteIndex1);


        int foundIndex2 = sDataManager.findNote(testNote2);
        assertEquals(foundIndex2 , noteIndex2);
    }
}