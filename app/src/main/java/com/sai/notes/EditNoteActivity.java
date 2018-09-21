package com.sai.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditNoteActivity extends AppCompatActivity {

    static String EMPTY_STRING = "";
    ArrayList<String> notesList;
    static String sharedPrefKey = "notesListString";
    int index;
    EditText noteEditText;
    boolean addNew;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.sai.notes", Context.MODE_PRIVATE);

        noteEditText = findViewById(R.id.noteeditText);

        Intent intent = getIntent();
        index = intent.getIntExtra("listIndex",0);
        addNew = intent.getBooleanExtra("AddNew", false);
        //Log.i("index value ", "-----------edit activity--"+index);

        //retrieve the note from shared preferences
        notesList = loadListFromSharedPreferences();
        //Toast.makeText(this, "listIndex" + index+":"+notesList.get(index), Toast.LENGTH_SHORT).show();
        if (addNew){
            noteEditText.setText(EMPTY_STRING);
        } else {
            noteEditText.setText(notesList.get(index));
        }
    }


    @Override
    public void finish() {
        super.finish();
        //Log.i("info",addNew + " IN FINISH Back button clicked " + index);
        //Save the note to shared preferences
        if (addNew){
            notesList.add(noteEditText.getText().toString());
        } else {
            notesList.set(index, noteEditText.getText().toString());
        }
        Gson gson = new Gson();
        String json = gson.toJson(notesList);
        sharedPreferences.edit().putString(sharedPrefKey, json).apply();
    }

    public ArrayList<String> loadListFromSharedPreferences(){

        String noteListString = sharedPreferences.getString(sharedPrefKey,EMPTY_STRING);

        if (noteListString.length() > 0 ) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(sharedPrefKey, null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            return  notesList =  gson.fromJson(json, type);
        } else {
            //first time app runs
            notesList = new ArrayList<>();
            notesList.add("Example note");
            return notesList;
        }
    }
}
