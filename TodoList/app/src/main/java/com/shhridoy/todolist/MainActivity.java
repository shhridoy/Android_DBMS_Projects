package com.shhridoy.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    DBHelper dbHelper;
    ListView listView;
    ListviewAdapter listviewAdapter;
    ArrayList<ListViewData> arrayList;
    EditText etDate;
    EditText etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.LV);
        arrayList = new ArrayList<>();

        retrieveData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                inputDialog(0, null, null, null);
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        setDate(calendar);
    }

    private void setDate(Calendar c){
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        etDate.setText(dateFormat.format(c.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String am_pm = "";

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hour);
        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : Integer.toString( datetime.get(Calendar.HOUR) );
        etTime.setText(strHrsToShow+" : "+minute+" "+am_pm);
    }

    class ListviewAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<ListViewData> arrayList;

        public ListviewAdapter (Context context, ArrayList<ListViewData> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view = inflater.inflate(R.layout.list_item_model, viewGroup, false);
            }

            TextView vehicleNameTV = view.findViewById(R.id.nameTV);
            TextView dateTV = view.findViewById(R.id.dateTV);

            final ListViewData listViewData = arrayList.get(i);

            vehicleNameTV.setText(listViewData.getVehicle_name());
            dateTV.setText(listViewData.getDate()+"\n"+listViewData.getTime());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Clicked "+listViewData.getVehicle_name(), Toast.LENGTH_SHORT).show();
                }
            });

            final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            inputDialog(listViewData.getId(), listViewData.getVehicle_name(), listViewData.getDate(), listViewData.getTime());
                            return true;
                        case 2:
                            deleteItem(listViewData.getId());
                            retrieveData();
                            return true;
                    }
                    return false;
                }
            };

            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.setHeaderTitle(listViewData.getVehicle_name());
                    MenuItem update = contextMenu.add(Menu.NONE, 1, 1, "UPDATE");
                    MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "DELETE");
                    //groupId, itemId, order, title
                    update.setOnMenuItemClickListener(onChange);
                    delete.setOnMenuItemClickListener(onChange);
                }
            });

            notifyDataSetChanged();
            return view;
        }
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

    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int am_pm = calendar.get(Calendar.AM_PM);
            return new TimePickerDialog(getActivity(),
                    (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, false
            );
        }
    }

    private void retrieveData(){
        dbHelper = new DBHelper(this);
        Cursor c = dbHelper.getData();
        arrayList.clear();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String todo = c.getString(1);
            String date = c.getString(2);
            String time = c.getString(3);
            ListViewData listViewData = new ListViewData(id, todo, date, time);
            arrayList.add(listViewData);
        }
        listviewAdapter = new ListviewAdapter(this, arrayList);
        listView.setAdapter(listviewAdapter);
    }

    private void addItem(String todo, String date, String time) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(todo, date, time);
            Toast.makeText(this, "Added!", Toast.LENGTH_LONG).show();
        } catch (SQLiteException e){
            Toast.makeText(this, "Can't added!", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteItem(int id){
        dbHelper = new DBHelper(this);
        boolean deleted = dbHelper.deleteData(id);
        if (deleted) {
            Toast.makeText(this,"Deleted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Can't Deleted!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateItem(int id, String todo, String date, String time){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, todo, date, time);
        if (updated) {
            Toast.makeText(this,"Updated!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Can't updated!", Toast.LENGTH_LONG).show();
        }
    }

    private void inputDialog(final int id, String name, String date, String time){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        final EditText etName = dialog.findViewById(R.id.NameET);
        etDate = dialog.findViewById(R.id.ChooseDateTV);
        etTime = dialog.findViewById(R.id.ChooseTimeET);
        Button saveBtn = dialog.findViewById(R.id.saveButton);
        Button chooseDateBtn = dialog.findViewById(R.id.datePicBtn);
        Button chooseTimeBtn = dialog.findViewById(R.id.timePicBtn);

        chooseDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "date");
            }
        });

        chooseTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "time");
            }
        });

        if (id == 0 && name == null && date == null && time == null) {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || etDate.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        addItem(etName.getText().toString(), etDate.getText().toString(), etTime.getText().toString());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        } else {
            etName.setText(name);
            etDate.setText(date);
            etTime.setText(time);
            saveBtn.setText("Update");
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0
                            || etDate.getText().toString().length() <= 0
                            || etTime.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        updateItem(id, etName.getText().toString(), etDate.getText().toString(), etTime.getText().toString());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }

        dialog.show();
    }
}