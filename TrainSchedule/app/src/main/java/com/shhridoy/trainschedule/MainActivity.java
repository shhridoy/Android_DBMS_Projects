package com.shhridoy.trainschedule;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
        implements TimePickerDialog.OnTimeSetListener{

    DBHelper dbHelper;
    ListView listView;
    ListviewAdapter listviewAdapter;
    ArrayList<ListViewData> arrayList;
    EditText etOutTime;
    EditText etInTime;
    EditText gateNo;
    boolean btnout, btnin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.LV);
        arrayList = new ArrayList<>();

        btnin = false;
        btnout = false;

        retrieveData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                inputDialog(0, null, null, null, 0);
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

        if (btnout) {
            etOutTime.setText(strHrsToShow+" : "+minute+" "+am_pm);
        } else {
            etInTime.setText(strHrsToShow+" : "+minute+" "+am_pm);
        }
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
            TextView outTimeTv = view.findViewById(R.id.TVTimeOUT);
            TextView inTimeTv = view.findViewById(R.id.TVTimeIN);
            TextView gateTv = view.findViewById(R.id.TVGate);

            final ListViewData listViewData = arrayList.get(i);

            vehicleNameTV.setText(listViewData.getTrain_name());
            outTimeTv.setText(listViewData.getOut_time());
            inTimeTv.setText(listViewData.getIn_time());
            gateTv.setText(Integer.toString(listViewData.getGate_no()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, listViewData.getTrain_name(), Toast.LENGTH_SHORT).show();
                }
            });

            final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            inputDialog(listViewData.getId(),
                                    listViewData.getTrain_name(),
                                    listViewData.getOut_time(),
                                    listViewData.getIn_time(),
                                    listViewData.getGate_no());
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
                    contextMenu.setHeaderTitle(listViewData.getTrain_name());
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
            String train_name = c.getString(1);
            String out_time = c.getString(2);
            String in_time = c.getString(3);
            int gate_no = c.getInt(4);
            ListViewData listViewData = new ListViewData(id, train_name, out_time, in_time, gate_no);
            arrayList.add(listViewData);
        }
        listviewAdapter = new ListviewAdapter(this, arrayList);
        listView.setAdapter(listviewAdapter);
    }

    private void addItem(String train_name, String out_time, String in_time, int gate_no) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(train_name, out_time, in_time, gate_no);
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

    private void updateItem(int id, String train_name, String out_time, String in_time, int gate_no){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, train_name, out_time, in_time, gate_no);
        if (updated) {
            Toast.makeText(this,"Updated!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Can't updated!", Toast.LENGTH_LONG).show();
        }
    }

    private void inputDialog(final int id, String train_name, String out_time, String in_time, int gate_no){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        final EditText etName = dialog.findViewById(R.id.NameET);
        etOutTime = dialog.findViewById(R.id.ChooseDateTV);
        etInTime = dialog.findViewById(R.id.ChooseTimeET);
        gateNo = dialog.findViewById(R.id.GateET);
        Button saveBtn = dialog.findViewById(R.id.saveButton);
        Button chooseOutTimeBtn = dialog.findViewById(R.id.datePicBtn);
        Button chooseInTimeBtn = dialog.findViewById(R.id.timePicBtn);

        chooseOutTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnout = true;
                btnin = false;
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "time");
            }
        });

        chooseInTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnin = true;
                btnout = false;
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "time2");
            }
        });

        if (id == 0 && train_name == null && out_time == null && in_time == null && gate_no == 0) {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || etOutTime.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        addItem(etName.getText().toString(),
                                etOutTime.getText().toString(),
                                etInTime.getText().toString(),
                                Integer.parseInt(gateNo.getText().toString()));
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        } else {
            etName.setText(train_name);
            etOutTime.setText(out_time);
            etInTime.setText(in_time);
            gateNo.setText(Integer.toString(gate_no));
            saveBtn.setText("Update");
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0
                            || etOutTime.getText().toString().length() <= 0
                            || etInTime.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        updateItem(id, etName.getText().toString(), etOutTime.getText().toString(), etInTime.getText().toString(),
                                Integer.parseInt(gateNo.getText().toString()));
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }

        dialog.show();
    }
}