package com.example.note_teamorange_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.note_teamorange_android.R;

import com.example.note_teamorange_android.database.NotesDatabase;
import com.example.note_teamorange_android.entities.Note;

import java.util.List;

import javax.sql.ConnectionPoolDataSource;

import static com.example.note_teamorange_android.database.NotesDatabase.getDatabase;

//main1111
public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_NOTE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE

                );
            }
        });
        getNotes();
//
    }
    //just as u need a async task to save a note, u'll also need it to get notes from the database
    private void getNotes(){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                .getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                Log.d("MY_NOTES", notes.toString());
            }

        }
        new GetNotesTask().execute();
    }
}
