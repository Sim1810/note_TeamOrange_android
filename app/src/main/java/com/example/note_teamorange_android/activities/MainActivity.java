package com.example.note_teamorange_android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.note_teamorange_android.R;

import com.example.note_teamorange_android.adaptors.NotesAdaptor;
import com.example.note_teamorange_android.database.NotesDatabase;
import com.example.note_teamorange_android.entities.Note;

import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionPoolDataSource;

import static com.example.note_teamorange_android.database.NotesDatabase.getDatabase;

//main1111
public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_NOTE =1;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdaptor notesAdaptor;

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

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        notesAdaptor = new NotesAdaptor(noteList);
        notesRecyclerView.setAdapter(notesAdaptor);

        getNotes();

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
                if (noteList.size() == 0){
                    noteList.addAll(notes);
                    notesAdaptor.notifyDataSetChanged();
                }else{
                    noteList.add(0,notes.get(0));
                    notesAdaptor.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
            }

        }
        new GetNotesTask().execute();
    }
}
