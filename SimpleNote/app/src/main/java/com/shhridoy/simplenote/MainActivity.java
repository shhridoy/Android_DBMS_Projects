package com.shhridoy.simplenote;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextView tvTitle, tvDetails, tvDate;
    EditText etTitle, etDetails;
    ListView listView;
    Button saveBtn, deleteBtn, editBtn, dateBtn;
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
                        String d = cursor.getString(3);
                        read_dialog(cursor.getInt(0), tit, det, d);
                        break;
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
            input_dialog(0, null, null, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void input_dialog(final int id, final String t, final String d, String date) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        etTitle = dialog.findViewById(R.id.TitleEV);
        etDetails = dialog.findViewById(R.id.DetailET);
        saveBtn = dialog.findViewById(R.id.saveButton);
        dateBtn = dialog.findViewById(R.id.DateBtn);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar c = new GregorianCalendar(year, month, day);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        dateBtn.setText(dateFormat.format(c.getTime()));

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
                        updateItem(id, etTitle.getText().toString(), etDetails.getText().toString(), dateBtn.getText().toString());
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
                        addItem(etTitle.getText().toString(), etDetails.getText().toString(), dateBtn.getText().toString());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "date");
            }
        });

        dialog.show();
    }

    private void read_dialog(final int ID, final String title, final String details, final String date) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.detail_dialog);
        tvTitle = dialog.findViewById(R.id.TitleTV);
        tvDate = dialog.findViewById(R.id.DateTV);
        tvDetails = dialog.findViewById(R.id.DetailTV);
        deleteBtn = dialog.findViewById(R.id.deleteButton);
        editBtn = dialog.findViewById(R.id.editButton);

        tvTitle.setText(title);
        tvDate.setText(date);
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
                input_dialog(ID, title, details, date);
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

    private void addItem(String tit, String det, String date) {
        try{
            dbHelper.insertData(tit, det, date);
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

    private void updateItem(int id, String tit, String det, String date){
        boolean updated = dbHelper.updateData(id, tit, det, date);
        if (updated) {
            Toast.makeText(this,"Item updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't updated.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        setDate(calendar);
    }

    private void setDate(Calendar c){
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        dateBtn.setText(dateFormat.format(c.getTime()));
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day
            );
        }
    }
}
