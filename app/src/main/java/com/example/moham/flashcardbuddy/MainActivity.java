package com.example.moham.flashcardbuddy;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.moham.flashcardbuddy.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHandler db = new DBHandler(this);
        // SQLiteDatabase s = openOrCreateDatabase("FlashcardBuddy",MODE_PRIVATE,null);
        // db.onCreate(s);
        // Log.d("TEST:", db.getDatabaseName());
        // db.showAllTables();
        // db.addFlashcard(new LeitnerSystem(2, "Kore", 4, 1), "LeitnerSystem");

        //db.deleteTable("LeitnerSystem");
        //db.deleteTable("SuperMemo");
        //  db.getAvaliableCards("LeitnerSystem");
        databaseStatus(db);
        displaySuperMemoWords(db);
        displayLeitnerSystemWords(db);
        try {
            wordsAvaliable();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //String wordCount = Integer.toString(db.getAvaliableCards("LeitnerSystem"));
        //  Log.d("Current count is ", wordCount);
        //Log.d("Table name:",db.getDatabaseName());
        //  //db.deleteTable("LeitnerSystem");
        //db.deleteTable("SuperMemo");
        ////db.dropTable("LeitnerSystem");
        //db.dropTable("SuperMemo");
        // Reading all shops


    }

    public void wordsAvaliable() throws ParseException {
        DBHandler db = new DBHandler(this);
        List<SuperMemo> rows = db.getSuperMemoFlashcards();
        DateFormat format = new SimpleDateFormat("dd-MM-yyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        int count = 0;
        for (SuperMemo flashcard : rows) {//For each card..
            Date reviewDate = format.parse(flashcard.getReviewDate());
            Date todaysDate = format.parse(flashcard.getCurrentDate());
            if (todaysDate.after(reviewDate) || todaysDate == reviewDate) {//If today is the review day
                count++;//Increment the count by 1
            }
        }
        if (count >= 1 && count <= 2) {
            Button btn = (Button) findViewById(R.id.start_review);
            btn.setEnabled(true);
        }
        TextView view = (TextView) findViewById(R.id.wordsReady);
        view.setText(Integer.toString(count));
    }

    public void openActivity(View view) {
        Intent intent = new Intent(this, LeitnerActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "It works!";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void databaseStatus(DBHandler db) {
        Flashcard flashcard = new Flashcard();
        if (db.checkDatabase("LeitnerSystem")) {
            Log.d("Empty database: ", "Adding data ..");
            db.addFlashcard(new LeitnerSystem(0, "Kore", "This", 0, null, flashcard.getCurrentDate(), flashcard.getCurrentDate(), 1), "LeitnerSystem");
            db.addFlashcard(new LeitnerSystem(0, "Sore", "That", 0, null, flashcard.getCurrentDate(), flashcard.getCurrentDate(), 1), "LeitnerSystem");
        } else {
            Log.d("Full LeitnerSystem:", "Enough data is already stored ..");
        }
        if (db.checkDatabase("SuperMemo")) {
            db.addFlashcard(new SuperMemo(0, "Kore", "This", 0, null, flashcard.getCurrentDate(), flashcard.getCurrentDate(), 2.5f, 0), "SuperMemo");
            db.addFlashcard(new SuperMemo(0, "Sore", "That", 0, null, flashcard.getCurrentDate(), flashcard.getCurrentDate(), 2.5f, 0), "SuperMemo");
        } else {
            Log.d("Full SuperMemo: ", "Enough data is already stored ..");
        }
    }

    public void displaySuperMemoWords(DBHandler db) {
        Log.d("SuperMemo: ", "Display SuperMemo cards..");
        List<SuperMemo> rows = null;
        try {
            rows = db.getSuperMemoFlashcards();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (SuperMemo flashcard : rows) {
            String log = "Id: " + flashcard.getId()
                    + " ,Word: " + flashcard.getWord()
                    + " ,Interval: " + flashcard.getInterval()
                    + " ,eFactor: " + flashcard.getEFactor()
                    + " ,Date added: " + flashcard.getDateAdded()
                    + " ,Review date: " + flashcard.getReviewDate();
            Log.d("SuperMemo cards: ", log);
        }
    }

    public void displayLeitnerSystemWords(DBHandler db) {
        Log.d("Leitner: ", "Display Leitner cards..");
        List<LeitnerSystem> rows = db.getLeitnerFlashcards();
        for (LeitnerSystem flashcard : rows) {
            String log = "Id: " + flashcard.getId()
                    + " ,Word: " + flashcard.getWord()
                    + " ,Interval: " + flashcard.getInterval()
                    + " ,Box Number: " + flashcard.getBoxNumnber()
                    + " ,Date added: " + flashcard.getDateAdded()
                    + " ,Review date: " + flashcard.getReviewDate();
            Log.d("Leitner cards: ", log);
        }
    }

    public void displayLeitnerSystemWords() {

    }
}
