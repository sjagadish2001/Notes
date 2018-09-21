package com.sai.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    static String EMPTY_STRING = "";
    ArrayList<String> notesList;
    static String sharedPrefKey = "notesListString";
    SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         switch (item.getItemId()){
             case R.id.addnewnote:
                 Log.i("info", item.getTitle().toString());
                 Intent intent = new Intent(getApplicationContext(),EditNoteActivity.class);
                 intent.putExtra("listIndex", notesList.size());
                 intent.putExtra("AddNew", true);
                 startActivity(intent);
                 return true;
             default:
                 return false;
         }
    }

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.noteList);
        populateList();
    }


    @Override
    protected void onResume() {
        super.onResume();
        populateList();
    }

    private void populateList(){
        notesList = loadListFromSharedPreferences();
        //final ArrayList<String> notesList = new ArrayList<String>(asList("Example note"));
       final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,notesList);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("info",i +"=i Main Activity-- " + notesList.get(i));
                //Toast.makeText(getApplicationContext(), "Selected:"+notesList.get(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),EditNoteActivity.class);
                intent.putExtra("listIndex", i);
                startActivity(intent);
            }
        });



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int x, final long l) {
                //Toast.makeText(getBaseContext(), "Long Clicked---"+l, Toast.LENGTH_SHORT).show();
                //Log.i("info"+l, x+"=index; ------------ size:"+notesList.size());
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure !")
                        .setMessage("Do you want to delete the selected Note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notesList.remove(x);
                                arrayAdapter.notifyDataSetChanged();
                                listView.refreshDrawableState();

                                Gson gson = new Gson();
                                String json = gson.toJson(notesList);
                                sharedPreferences.edit().putString(sharedPrefKey, json).apply();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return true;
            }
        });
    }


    public ArrayList<String> loadListFromSharedPreferences(){

        sharedPreferences = getApplicationContext().getSharedPreferences("com.sai.notes", Context.MODE_PRIVATE);
        String noteListString = sharedPreferences.getString(sharedPrefKey,EMPTY_STRING);

        //Log.i("load list","------" + noteListString);
        if (noteListString.length() > 0 ) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(sharedPrefKey, null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
           return  notesList =  gson.fromJson(json, type);
        } else {
            //first time app runs
            notesList = new ArrayList<String>();
            notesList.add("Example note");
            return notesList;
        }
    }
}
