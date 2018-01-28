package com.shhridoy.simplenote;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tvTitle, tvDetails;
    EditText etTitle, etDetails;
    ListView listView;
    Button saveBtn, deleteBtn, editBtn;
    DBHelper dbHelper;

    ArrayAdapter<String> adapter;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.LV);

        dbHelper = new DBHelper(this);
        list = new ArrayList<>();

        retrieveData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = dbHelper.getData();
                String text = (String) adapterView.getItemAtPosition(position);
                while (cursor.moveToNext()) {
                    if ( cursor.getString(1).equals(text) ) {
                        String tit = cursor.getString(1);
                        String det = cursor.getString(2);
                        read_dialog(cursor.getInt(0), tit, det);
                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            input_dialog(0, null, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void input_dialog(final int id, final String t, final String d) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        etTitle = dialog.findViewById(R.id.TitleEV);
        etDetails = dialog.findViewById(R.id.DetailET);
        saveBtn = dialog.findViewById(R.id.saveButton);

        if (id != 0 && t != null && d != null) {
            etTitle.setText(t);
            etDetails.setText(d);
            saveBtn.setText("Update");
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etTitle.getText().toString().length() <= 0 || etDetails.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        updateItem(id, etTitle.getText().toString(), etDetails.getText().toString());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        } else {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etTitle.getText().toString().length() <= 0 || etDetails.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        addItem(etTitle.getText().toString(), etDetails.getText().toString());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }




        dialog.show();
    }

    private void read_dialog(final int ID, final String title, final String details) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.detail_dialog);
        tvTitle = dialog.findViewById(R.id.TitleTV);
        tvDetails = dialog.findViewById(R.id.DetailTV);
        deleteBtn = dialog.findViewById(R.id.deleteButton);
        editBtn = dialog.findViewById(R.id.editButton);

        tvTitle.setText(title);
        tvDetails.setText(details);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(ID);
                dialog.dismiss();
                retrieveData();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                input_dialog(ID, title, details);
            }
        });

        dialog.show();
    }

    private void retrieveData(){
        Cursor c = dbHelper.getData();
        list.clear();
        while (c.moveToNext()) {
            String title = c.getString(1);
            String detail = c.getString(2);
           list.add(title);
        }
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.ListItemTV, list);
        listView.setAdapter(adapter);
    }

    private void addItem(String tit, String det) {
        try{
            dbHelper.insertData(tit, det);
            Toast.makeText(this, "Data inserted", Toast.LENGTH_LONG).show();
        } catch (SQLiteException e){
            Toast.makeText(this, "Data doesn't inserted!", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteItem(int id){
        boolean deleted = dbHelper.deleteData(id);
        if (deleted) {
            Toast.makeText(this,"Item deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't deleted.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateItem(int id, String tit, String det){
        boolean updated = dbHelper.updateData(id, tit, det);
        if (updated) {
            Toast.makeText(this,"Item updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't updated.", Toast.LENGTH_LONG).show();
        }
    }

}
