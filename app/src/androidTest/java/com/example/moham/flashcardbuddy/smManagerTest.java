package com.example.moham.flashcardbuddy;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by moham on 17/02/2017.
 */
@RunWith(AndroidJUnit4.class)
public class smManagerTest {

    private DBHandler dbHandler;
    private smManager smManager;


    @Before
    public void setUp() throws Exception {
        // getTargetContext().deleteDatabase(dbHandler.DATABASE_NAME);
        dbHandler = new DBHandler(getTargetContext());
        smManager = new smManager(getTargetContext());
        dbHandler.deleteTable("SuperMemo", null);
        dbHandler.deleteTable("Results", null);
        dbHandler.addFlashcard(new SuperMemo(0, "Sore", "That", 0, null, Flashcard.getCurrentDate(), SuperMemo.getCurrentDate(), 2.5f, -1), "SuperMemo");
        dbHandler.addResults("SuperMemo");
    }

    @After
    public void tearDown() throws Exception {
        dbHandler.close();
    }


    @Test
    public void smWordsAvaliableForReview() throws ParseException {
        int count = smManager.SuperMemoWordCount();
        assertEquals(1, count++);
        System.out.println("SuperMemo Count is " + count);
    }

    @Test
    public void testIntervalOne() {
        SuperMemo sm2 = new SuperMemo(0, "MOHAMMED", 2.5f, 4);
        int newInterval = sm2.getNextInterval(1);//Uses previous EF value, EF value decreases the harder to remember.
        double newEF = sm2.getNewEFactor();//After each response is made
        System.out.println("New eFactor is " + newEF);
        assertEquals(newInterval, 1);
        assertTrue((2.4 < newEF) && (newEF < 2.6));
    }

    @Test
    public void testIntervalTwo() {
        SuperMemo sm2 = new SuperMemo(0, "MOHAMMED", 0, 4);
        int newInterval = sm2.getNextInterval(1);//Uses previous EF value, EF value decreases the harder to remember.
        double newEF = sm2.getNewEFactor();//After each response is made
        System.out.println("New eFactor is " + newEF);
        assertEquals(newInterval, 1);
        assertEquals(newEF, 1.3, 0);
    }

    @Test
    public void testIntervalThree() {
        SuperMemo sm2 = new SuperMemo(0, "MOHAMMED", 2.36, 4);
        int newInterval = sm2.getNextInterval(1);//Uses previous EF value, EF value decreases the harder to remember.
        double newEF = sm2.getNewEFactor();//After each response is made
        System.out.println("New eFactor is " + newEF);
        assertEquals(newInterval, 1);
        assertEquals(newEF, 2.36, 0);
    }

    /* Only one date should be considered */
    @Test
    public void differentDates() throws ParseException {
        SuperMemo currentWord = new SuperMemo(0, "Sore", "That", 0, null, Flashcard.getCurrentDate(), SuperMemo.getCurrentDate(), 2.5f, 0);
        DateFormat format = new SimpleDateFormat("EEEE dd-MM-yyy");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(currentWord.getReviewDate()));

        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.add(Calendar.DATE, 10);  // number of days to add

        String newReviewDate = format.format(c.getTime());  // dt is now the new date
        System.out.println("New date is " + newReviewDate);
        currentWord.setReviewDate(newReviewDate);

        dbHandler.addFlashcard(currentWord, "SuperMemo");
        List<SuperMemo> sm = smManager.todaysWordReviewList();
        assertEquals(sm.size(),1);
    }

    @Test
    public void ratingAnswer() throws ParseException {
        DateFormat format = new SimpleDateFormat("EEEE dd-MM-yyy");
        String newReviewDate = "";
        List<SuperMemo> rows = smManager.todaysWordReviewList();//Won't work, has if condition. But one word should show still.
        SuperMemo sm = rows.get(0); //Original data, first card.

        int rating = 3;
        sm.setQualityOfResponse(rating);
        int newInterval = sm.getNextInterval(sm.getInterval() + 1);// Needs to increment the interval by 1 as a review has been just completed.
        sm.setInterval(newInterval);
        double newEF = sm.getNewEFactor();//After each response is made
        sm.setEFactor(newEF);

        Calendar c = Calendar.getInstance();
        smManager.updateSuperMemoWord(sm);
        dbHandler.updateResults("SuperMemo", Integer.toString(rating), sm.getInterval() + 1,sm.getSuccessCount(),0);

        List<SuperMemo> rows2 = smManager.getSuperMemoFlashcards();
        SuperMemo sm2 = rows2.get(0);//Updated data
        c.setTime(format.parse(sm.getReviewDate()));
        c.add(Calendar.DATE, sm.getInterval());  // number of days to add
        newReviewDate = format.format(c.getTime());  // dt is now the new date
        assertEquals(newReviewDate,sm2.getReviewDate());
        assertEquals(newInterval, sm2.getInterval());//Interval before, vs interval after update. Also checks if sm.setInterval works.
        assertEquals(rating, sm2.getQualityOfResponse());
        System.out.println("Old response is " + sm.getQualityOfResponse() + " Quality is " + sm2.getQualityOfResponse());
        assertEquals(newEF, 2.36,0);
    }

    @Test
    public void ratingAnswer2() throws ParseException {
        DateFormat format = new SimpleDateFormat("EEEE dd-MM-yyy");
        String newReviewDate = "";
        List<SuperMemo> rows = smManager.todaysWordReviewList();//Won't work, has if condition. But one word should show still.
        SuperMemo sm = rows.get(0); //Original data, first card.

        int rating = 4;
        sm.setQualityOfResponse(rating);
        int newInterval = sm.getNextInterval(sm.getInterval() + 1);// Needs to increment the interval by 1 as a review has been just completed.
        sm.setInterval(newInterval);
        double newEF = sm.getNewEFactor();//After each response is made
        sm.setEFactor(newEF);

        Calendar c = Calendar.getInstance();
        smManager.updateSuperMemoWord(sm);
        dbHandler.updateResults("SuperMemo", Integer.toString(rating), sm.getInterval() + 1,sm.getSuccessCount(),0);

        List<SuperMemo> rows2 = smManager.getSuperMemoFlashcards();
        SuperMemo sm2 = rows2.get(0);//Updated data
        c.setTime(format.parse(sm.getReviewDate()));
        c.add(Calendar.DATE, sm.getInterval());  // number of days to add
        newReviewDate = format.format(c.getTime());  // dt is now the new date
        assertEquals(newReviewDate,sm2.getReviewDate());
        assertEquals(newInterval, sm2.getInterval());//Interval before, vs interval after update. Also checks if sm.setInterval works.
        assertEquals(rating, sm2.getQualityOfResponse());
        System.out.println("Old response is " + sm.getQualityOfResponse() + " Quality is " + sm2.getQualityOfResponse());
        assertEquals(newEF, 2.5,0);
    }

    @Test
    public void ratingAnswer3() throws ParseException {
        DateFormat format = new SimpleDateFormat("EEEE dd-MM-yyy");
        String newReviewDate = "";
        List<SuperMemo> rows = smManager.todaysWordReviewList();//Won't work, has if condition. But one word should show still.
        SuperMemo sm = rows.get(0); //Original data, first card.

        int rating = 4;
        sm.setEFactor(2.36);
        sm.setQualityOfResponse(rating);
        int newInterval = sm.getNextInterval(sm.getInterval() + 1);// Needs to increment the interval by 1 as a review has been just completed.
        sm.setInterval(newInterval);
        double newEF = sm.getNewEFactor();//After each response is made
        sm.setEFactor(newEF);

        Calendar c = Calendar.getInstance();
        smManager.updateSuperMemoWord(sm);
        dbHandler.updateResults("SuperMemo", Integer.toString(rating), sm.getInterval() + 1,sm.getSuccessCount(),0);

        List<SuperMemo> rows2 = smManager.getSuperMemoFlashcards();
        SuperMemo sm2 = rows2.get(0);//Updated data
        c.setTime(format.parse(sm.getReviewDate()));
        c.add(Calendar.DATE, sm.getInterval());  // number of days to add
        newReviewDate = format.format(c.getTime());  // dt is now the new date
        assertEquals(newReviewDate,sm2.getReviewDate());
        assertEquals(newInterval, sm2.getInterval());//Interval before, vs interval after update. Also checks if sm.setInterval works.
        assertEquals(rating, sm2.getQualityOfResponse());
        System.out.println("Old response is " + sm.getQualityOfResponse() + " Quality is " + sm2.getQualityOfResponse());
        assertEquals(newEF, 2.36,0);
    }



}