package com.example.note_teamorange_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note_teamorange_android.R;
import com.example.note_teamorange_android.database.NotesDatabase;
import com.example.note_teamorange_android.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
//commit
    private EditText inputNoteTitle , inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubTitleIndicator;

    private  String selectedNoteColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        inputNoteTitle = findViewById(R.id.inptNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubTitleIndicator = findViewById(R.id.viewSubtitleIndicator);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        selectedNoteColor = "#333333";

        initMiscellaneous();
        setSubTitleIndicator();
    }


    private void saveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note title can't be empty",Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteSubtitle.getText().toString().trim().isEmpty()
            && inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note description can't be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        //preparing note object to save in database
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);

        //room doesnot allow database operation on the main thread .that's why we'r using asyn task to save note.
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {


            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }

        new SaveNoteTask().execute();
    }
    private void initMiscellaneous(){
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMisc);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewcolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubTitleIndicator();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FDBE38";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubTitleIndicator();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubTitleIndicator();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#3A52Fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubTitleIndicator();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubTitleIndicator();
            }
        });
    }

    private  void setSubTitleIndicator(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubTitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
}