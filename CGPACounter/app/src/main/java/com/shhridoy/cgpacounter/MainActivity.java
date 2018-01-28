package com.shhridoy.cgpacounter;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText etName, etID, etSem1, etSem2, etSem3, etSem4, etSem5, etSem6, etSem7, etSem8;
    EditText etDName, etDID, etDCgpa;
    TextView tvCGPA;
    Button btnCalculate, btnUpdate, btnAdd;
    DBHelper dbHelper;

    List<Data> list;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        list = new ArrayList<>();

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float sem1 = etSem1.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem1.getText().toString());
                float sem2 = etSem2.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem2.getText().toString());
                float sem3 = etSem3.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem3.getText().toString());
                float sem4 = etSem4.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem4.getText().toString());
                float sem5 = etSem5.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem5.getText().toString());
                float sem6 = etSem6.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem6.getText().toString());
                float sem7 = etSem7.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem7.getText().toString());
                float sem8 = etSem8.getText().length() <= 0 ? 0.0f : Float.parseFloat(etSem8.getText().toString());

                List<Float> list = new ArrayList<>();
                list.add(sem1);
                list.add(sem2);
                list.add(sem3);
                list.add(sem4);
                list.add(sem5);
                list.add(sem6);
                list.add(sem7);
                list.add(sem8);

                List<Float> floatList = new ArrayList<>();

                for (Float l : list) {
                    if(l != 0.0f){
                        floatList.add(l);
                    }
                }

                float sum = 0;
                for(Float f : floatList) {
                    sum += f;
                }

                float result = sum/floatList.size();

                tvCGPA.setText(String.format("%.2f", result));
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().length() <= 0 || etID.getText().toString().length() <= 0
                        || tvCGPA.getText().toString().length() <= 0) {
                    Toast.makeText(MainActivity.this, "You shouldn't keep Name, ID and CGPA field empty to save it to database!!", Toast.LENGTH_LONG).show();
                } else {
                    addItem(etName.getText().toString(), etID.getText().toString(), Float.parseFloat(tvCGPA.getText().toString()));
                    etName.setText("");
                    etID.setText("");
                    etSem1.setText("");
                    etSem2.setText("");
                    etSem3.setText("");
                    etSem4.setText("");
                    etSem5.setText("");
                    etSem6.setText("");
                    etSem7.setText("");
                    etSem8.setText("");
                    tvCGPA.setText("");
                }
            }
        });
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
            stud_id.setText(data.getStud_id());
            cgpa.setText(Float.toString(data.getCgpa()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, data.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            input_dialog(data.getId(), data.getName(), data.getStud_id(), data.getCgpa());
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

    private void retrieveData(){
        dbHelper = new DBHelper(this);
        Cursor c = dbHelper.getData();
        list.clear();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String stud_id = c.getString(2);
            float cgpa = c.getFloat(3);
            Data data = new Data(id, name, stud_id, cgpa);
            list.add(data);
        }
        adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void addItem(String name, String stud_id, float cgpa) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(name, stud_id, cgpa);
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

    private void updateItem(int id, String name, String stud_id, float cgpa){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, name, stud_id, cgpa);
        if (updated) {
            Toast.makeText(this,"Item updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't updated.", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews(){
        etName = findViewById(R.id.ETName);
        etID = findViewById(R.id.ETID);
        etSem1 = findViewById(R.id.ETSem1);
        etSem2 = findViewById(R.id.ETSem2);
        etSem3 = findViewById(R.id.ETSem3);
        etSem4 = findViewById(R.id.ETSem4);
        etSem5 = findViewById(R.id.ETSem5);
        etSem6 = findViewById(R.id.ETSem6);
        etSem7 = findViewById(R.id.ETSem7);
        etSem8 = findViewById(R.id.ETSem8);
        tvCGPA = findViewById(R.id.TVCGPA);
        btnCalculate = findViewById(R.id.calculateBtn);
        btnAdd = findViewById(R.id.addBtn);
    }

    private void listView_dialog () {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_dialog);

        listView = dialog.findViewById(R.id.LVDialog);

        retrieveData();

        dialog.show();
    }

    private void input_dialog(final int id, String name, String stud_id, float cgpa) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);

        etDName = dialog.findViewById(R.id.ETName);
        etDID = dialog.findViewById(R.id.ETID);
        etDCgpa = dialog.findViewById(R.id.ETCGPA);
        btnUpdate = dialog.findViewById(R.id.UpdateBtn);

        etDName.setText(name);
        etDID.setText(stud_id);
        etDCgpa.setText(Float.toString(cgpa));
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etDName.getText().toString().length() <= 0 || etDID.getText().toString().length() <= 0 ||
                        etDCgpa.getText().toString().length() <= 0) {
                    Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                } else {
                    updateItem(id, etDName.getText().toString(), etDID.getText().toString(), Float.parseFloat(etDCgpa.getText().toString()));
                    dialog.dismiss();
                    retrieveData();
                }
            }
        });

        dialog.show();
    }
}
