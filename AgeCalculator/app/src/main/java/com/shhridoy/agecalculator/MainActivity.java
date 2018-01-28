package com.shhridoy.agecalculator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    EditText etName;
    EditText etDName;
    Button btnDUpdate, btnDChooseDate;
    TextView tvAge, etDofBirth, etDDofBirth, etDAge;
    Button btnChooseDate, btnCalculate, btnSave;
    static String age = null;

    ListView listView;
    DBHelper dbHelper;

    List<Data> list;
    MyAdapter adapter;

    boolean isDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = new ArrayList<>();

        etName = findViewById(R.id.ETName);
        etDofBirth = findViewById(R.id.ETDofBirth);
        tvAge = findViewById(R.id.TVAge);
        btnChooseDate = findViewById(R.id.BtnDofBirth);
        btnCalculate = findViewById(R.id.calculateBtn);
        btnSave = findViewById(R.id.addBtn);

        isDialog = false;

        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialog = false;
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "date");
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (age != null) {
                    tvAge.setText(age);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().length() <= 0 || etDofBirth.getText().toString().length() <= 0
                        || tvAge.getText().toString().length() <= 0) {
                    Toast.makeText(MainActivity.this, "You shouldn't keep any field empty to save it to database!!", Toast.LENGTH_LONG).show();
                } else {
                    addItem(etName.getText().toString(), etDofBirth.getText().toString(),
                            Integer.parseInt(tvAge.getText().toString()));
                    etName.setText("");
                    etDofBirth.setText("");
                    tvAge.setText("");
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
            listView_dialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        age = getAge(year, month, day);
        setDate(calendar);
    }

    private void setDate(Calendar c){
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

        if (isDialog) {
            etDDofBirth.setText(dateFormat.format(c.getTime()));
            etDAge.setText(age);
        } else {
            etDofBirth.setText(dateFormat.format(c.getTime()));
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

    class MyAdapter extends BaseAdapter {

        Context context;
        List<Data> dataList;

        MyAdapter (Context context, List<Data> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view = inflater.inflate(R.layout.list_item, viewGroup, false);
            }

            TextView name = view.findViewById(R.id.TVListName);
            TextView stud_id = view.findViewById(R.id.TVListID);
            TextView cgpa = view.findViewById(R.id.TVListCgpa);

            final Data data = dataList.get(i);

            name.setText(data.getName());
            stud_id.setText("Date of Birth\n"+data.getDofbirth());
            cgpa.setText("Age\n"+Integer.toString(data.getAge()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, data.getName()+" is "+data.getAge()+" years old.", Toast.LENGTH_SHORT).show();
                }
            });

            final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            input_dialog(data.getId(), data.getName(), data.getDofbirth(), data.getAge());
                            return true;
                        case 2:
                            deleteItem(data.getId());
                            retrieveData();
                            return true;
                    }
                    return false;
                }
            };

            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.setHeaderTitle(data.getName());
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

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    private void retrieveData(){
        dbHelper = new DBHelper(this);
        Cursor c = dbHelper.getData();
        list.clear();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String dofbirth = c.getString(2);
            int age = c.getInt(3);
            Data data = new Data(id, name, dofbirth, age);
            list.add(data);
        }
        adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void addItem(String name, String dofbirth, int age) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(name, dofbirth, age);
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

    private void updateItem(int id, String name, String dofBirth, int age){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, name, dofBirth, age);
        if (updated) {
            Toast.makeText(this,"Item updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't updated.", Toast.LENGTH_LONG).show();
        }
    }

    private void listView_dialog () {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_dialog);

        listView = dialog.findViewById(R.id.LVDialog);

        retrieveData();

        dialog.show();
    }

    private void input_dialog(final int id, String name, String dofBirth, int age) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);

        etDName = dialog.findViewById(R.id.ETName);
        etDDofBirth = dialog.findViewById(R.id.ETDofB);
        etDAge = dialog.findViewById(R.id.ETAGE);
        btnDChooseDate = dialog.findViewById(R.id.BTNDofB);
        btnDUpdate = dialog.findViewById(R.id.UpdateBtn);

        etDName.setText(name);
        etDDofBirth.setText(dofBirth);
        etDAge.setText(Integer.toString(age));

        btnDChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialog = true;
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "date");
            }
        });

        btnDUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etDName.getText().toString().length() <= 0 || etDDofBirth.getText().toString().length() <= 0 ||
                        etDAge.getText().toString().length() <= 0) {
                    Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                } else {
                    updateItem(id, etDName.getText().toString(), etDDofBirth.getText().toString(),
                            Integer.parseInt(etDAge.getText().toString()));
                    dialog.dismiss();
                    retrieveData();
                }
            }
        });

        dialog.show();
    }
}
