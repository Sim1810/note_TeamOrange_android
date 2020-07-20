package com.example.note_teamorange_android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.note_teamorange_android.R;

import com.example.note_teamorange_android.adaptors.NotesAdaptor;
import com.example.note_teamorange_android.database.NotesDatabase;
import com.example.note_teamorange_android.entities.Note;
import com.example.note_teamorange_android.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionPoolDataSource;

import static com.example.note_teamorange_android.database.NotesDatabase.getDatabase;


public class MainActivity extends AppCompatActivity implements NotesListener {

    ImageButton myImageButton;
    ImageButton recordVoice;

    public static final int REQUEST_CODE_ADD_NOTE = 1; //this request code is used to add a new note
    private static final int REQUEST_CODE_UPDATE_NOTE = 2;//this request code is used to update note
public  static final int REQUEST_CODE_SHOW_NOTES = 3;//used to display all notes

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdaptor notesAdaptor;

    private int noteClickedPostion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myImageButton = findViewById(R.id.imageMap);

        recordVoice = findViewById(R.id.addVoice);


        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoeNewActivity = new Intent(MainActivity.this, maps.class);
                startActivity(intentLoeNewActivity);

            }
        });
        recordVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecNewActivity = new Intent(MainActivity.this, recordingActivity.class);
                startActivity(intentRecNewActivity);

            }
        });

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
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        notesAdaptor = new NotesAdaptor(noteList, this);
        notesRecyclerView.setAdapter(notesAdaptor);

        getNotes(REQUEST_CODE_SHOW_NOTES, false);//this getNotes() method is called from onCreate() method of an activity.
        //it means the app is just started & we need to display all notes from the database &that's why we r passing
        // REQUEST_CODE_SHOW_NOTES to that method

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               notesAdaptor.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() !=0){
                    notesAdaptor.searchNotes(s.toString());
                }

            }
        });

    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPostion = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);

    }

    //just as u need a async task to save a note, u'll also need it to get notes from the database
    private void getNotes(final int requestCode, final boolean isNotDeleted){//getting requestCode as a method parameter



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
                if(requestCode ==REQUEST_CODE_SHOW_NOTES){//here request code is REQUEST_CODE_SHOW_NOTES, so w r adding
                    //all notes from db to notelist & notify adapter about the new data set.
                    noteList.addAll(notes);
                    notesAdaptor.notifyDataSetChanged();
                }else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0, notes.get(0));
                    notesAdaptor.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);//REQUEST_CODE_ADD_NOTE used we r adding an only first note
                    //(Newly added note) from db to notelist and notify the adapter for newly inserted item & scrolling
                    // recycler view to the top
                }else if(requestCode == REQUEST_CODE_UPDATE_NOTE){//this request code used to remove note from clickedposition&
                    //adding latest updated note from same position from db & notify adapter for item changed at the position.
                    noteList.remove(noteClickedPostion);
                    noteList.add(noteClickedPostion, notes.get(noteClickedPostion));
                    notesAdaptor.notifyItemChanged(noteClickedPostion);

                    if(isNotDeleted){
                        notesAdaptor.notifyItemRemoved(noteClickedPostion);
                    } else{
                        noteList.add(noteClickedPostion, notes.get(noteClickedPostion));
                        notesAdaptor.notifyItemChanged(noteClickedPostion);
                    }//if requestcode is update , first we remove note from list. then we chekd if the note is deleted or not.if note deleted then
                    //notifying adaptr aboout item removed.if note is not deleted then it must be updated that's why we r adding a newly updated note
                    //to that same position where w removed & notifying adapter about item changed.
                }

              /*  if (noteList.size() == 0){
                    noteList.addAll(notes);
                    notesAdaptor.notifyDataSetChanged();
                }else{
                    noteList.add(0,notes.get(0));
                    notesAdaptor.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);*/
            }

        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        } else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK ){
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));//getNotes() method is called from onActivityResult method of activity
                //& we checked current request code is forupdate note & the result is RESULT_OK.it means already available
                //note is updated from createNote activity & its result is sent back to this activity.that's why we r passing
                //REQUEST_CODE_UPDATE_NOTE to that method. here added a new note, so isnotedeleted is false as parameter
                //hee we r updating already available note from db, it may be possible that note gets deleted so as a parameter isNoteDeleted
               // we r passing value from createnoteactivity ,whether the note is deleted or not using intent data with key "isnotedeleted"
            }
        }
    }
}
