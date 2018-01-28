package com.shhridoy.moviereview;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

    DBHelper dbHelper;
    ListView listView;
    EditText etName;
    RatingBar ratingBar;
    Button addBtn;
    List<Data> list;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.ListView);
        list = new ArrayList<>();
        retrieveData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_dialog(0, null, 0.0f);
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
            input_dialog(0, null, 0.0f);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            final RatingBar ratingBar = view.findViewById(R.id.RBList);

            final Data data = dataList.get(i);

            name.setText(data.getName());
            ratingBar.setRating(data.getRate());
            ratingBar.setEnabled(false);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, data.getName()+"\nRate: "+ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                }
            });

            final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            input_dialog(data.getId(), data.getName(), data.getRate());
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

    private void retrieveData(){
        dbHelper = new DBHelper(this);
        Cursor c = dbHelper.getData();
        list.clear();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            float rate = c.getFloat(2);
            Data data = new Data(id, name, rate);
            list.add(data);
        }
        adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void addItem(String name, float rate) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(name, rate);
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

    private void updateItem(int id, String name, float rate){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, name, rate);
        if (updated) {
            Toast.makeText(this,"Item updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't updated.", Toast.LENGTH_LONG).show();
        }
    }


    private void input_dialog(final int id, String name, float rate) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);

        etName = dialog.findViewById(R.id.ETName);
        ratingBar = dialog.findViewById(R.id.RatingBar);
        addBtn = dialog.findViewById(R.id.AddBtn);

        if (id != 0 && name != null && rate != 0.0) {
            etName.setText(name);
            ratingBar.setRating(rate);
            addBtn.setText("Update");
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || ratingBar.getRating() <= 0.0f) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        updateItem(id, etName.getText().toString(), ratingBar.getRating());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        } else {
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || ratingBar.getRating() <= 0.0f) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        addItem(etName.getText().toString(), ratingBar.getRating());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }

        dialog.show();
    }
}
