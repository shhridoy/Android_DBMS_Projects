package com.shhridoy.employeeinformation;

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

import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;

    TextView tvName, tvPrice, tvRevenue, tvIncome, tvEstDate;
    EditText etName, etPrice, etRevenue, etIncome, etEstDate;
    Button saveBtn, deleteBtn, updateBtn;

    ArrayAdapter<String> adapter;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.ListView);

        list = new ArrayList<>();

        retrieveData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dbHelper = new DBHelper(MainActivity.this);
                Cursor cursor = dbHelper.getData();
                String text = (String) adapterView.getItemAtPosition(i);
                while (cursor.moveToNext()) {
                    if ( cursor.getString(1).equals(text) ) {
                        int id = cursor.getInt(0);
                        String name = cursor.getString(1);
                        String emp_id = cursor.getString(2);
                        String designation = cursor.getString(3);
                        float salary = cursor.getFloat(4);
                        String joining_date = cursor.getString(5);
                        read_dialog(id, name, emp_id, designation, salary, joining_date);
                        break;
                    }
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_dialog(0, null, null, null, 0.0f, null);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void retrieveData(){
        dbHelper = new DBHelper(this);
        Cursor c = dbHelper.getData();
        list.clear();
        while (c.moveToNext()) {
            String name = c.getString(1);
            list.add(name);
        }
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.TVList, list);
        listView.setAdapter(adapter);
    }

    private void addItem(String name, String emp_id, String designation, float salary, String joining_date) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(name, emp_id, designation, salary, joining_date);
            Toast.makeText(this, "Data inserted", Toast.LENGTH_LONG).show();
        } catch (SQLiteException e){
            Toast.makeText(this, "Data doesn't inserted!", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteItem(int id){
        dbHelper = new DBHelper(this);
        boolean deleted = dbHelper.deleteData(id);
        if (deleted) {
            Toast.makeText(this,"Item deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't deleted.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateItem(int id,String name, String emp_id, String designation, float salary, String joining_date){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, name, emp_id, designation, salary, joining_date);
        if (updated) {
            Toast.makeText(this,"Item updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't updated.", Toast.LENGTH_LONG).show();
        }
    }

    private void input_dialog(final int id,String name, String emp_id, String designation, float salary, String joining_date) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        etName = dialog.findViewById(R.id.ETName);
        etPrice = dialog.findViewById(R.id.ETPrice);
        etRevenue = dialog.findViewById(R.id.ETRevenue);
        etIncome = dialog.findViewById(R.id.ETYearlyIncome);
        etEstDate = dialog.findViewById(R.id.ETEstbDate);
        saveBtn = dialog.findViewById(R.id.SaveBtn);

        if (id != 0 && name != null && emp_id != null && designation != null && salary != 0.0f && joining_date != null) {
            etName.setText(name);
            etPrice.setText(emp_id);
            etIncome.setText(Float.toString(salary));
            etRevenue.setText(designation);
            etEstDate.setText(joining_date);
            saveBtn.setText("Update");
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || etPrice.getText().toString().length() <= 0 ||
                            etIncome.getText().toString().length() <= 0 || etRevenue.getText().toString().length() <= 0 ||
                            etEstDate.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        updateItem(
                                id, etName.getText().toString(), etPrice.getText().toString(),
                                etRevenue.getText().toString(), Float.parseFloat(etIncome.getText().toString()),
                                etEstDate.getText().toString()
                        );
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        } else {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || etPrice.getText().toString().length() <= 0 ||
                            etIncome.getText().toString().length() <= 0 || etRevenue.getText().toString().length() <= 0 ||
                            etEstDate.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        addItem(
                                etName.getText().toString(), etPrice.getText().toString(),
                                etRevenue.getText().toString(), Float.parseFloat(etIncome.getText().toString()),
                                etEstDate.getText().toString()
                        );
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }

        dialog.show();
    }

    private void read_dialog(final int ID, final String name, final String emp_id, final String designation,
                             final float salary, final String joining_date) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.detail_dialog);
        tvName = dialog.findViewById(R.id.TVName);
        tvPrice = dialog.findViewById(R.id.TVPrice);
        tvIncome = dialog.findViewById(R.id.TVIncome);
        tvRevenue = dialog.findViewById(R.id.TVRevenue);
        tvEstDate = dialog.findViewById(R.id.TVEstbYear);
        deleteBtn = dialog.findViewById(R.id.DeleteBtn);
        updateBtn = dialog.findViewById(R.id.UpdateBtn);

        tvName.setText(name);
        tvPrice.setText(emp_id);
        tvRevenue.setText(designation);
        tvIncome.setText(Float.toString(salary));
        tvEstDate.setText(joining_date);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(ID);
                dialog.dismiss();
                retrieveData();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                input_dialog(ID, name, emp_id, designation, salary, joining_date);
            }
        });

        dialog.show();
    }
}