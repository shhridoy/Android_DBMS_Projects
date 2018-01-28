package com.shhridoy.blooddonorinfo;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    List<Data> list;
    ListView listView;
    EditText etName, etAge, etContact, etBlood;
    Button saveBtn, deleteBtn, updateBtn;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = new ArrayList<>();
        listView = findViewById(R.id.ListView);

        retrieveData();
    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private List<Data> Datalist;

        MyAdapter (Context context, List<Data> Datalist) {
            this.context = context;
            this.Datalist = Datalist;
        }

        @Override
        public int getCount() {
            return Datalist.size();
        }

        @Override
        public Object getItem(int i) {
            return Datalist.get(i);
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
            TextView blood = view.findViewById(R.id.TVListBlood);
            TextView age = view.findViewById(R.id.TVListAge);
            TextView contact = view.findViewById(R.id.TVListContact);

            final Data data = Datalist.get(i);

            name.setText(data.getName());
            blood.setText("Blood Group\n"+data.getGroup());
            age.setText("Age\n"+data.getAge());
            contact.setText("Contact\n"+data.getContact());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode(data.getContact().trim())));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                }
            });

            final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            input_dialog(data.getId(), data.getName(), data.getGroup(), data.getAge(), data.getContact());
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
            input_dialog(0, null, null, 0, null);
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
            String blood = c.getString(2);
            int age = c.getInt(3);
            String contact = c.getString(4);
            Data data = new Data(id, name, blood, age, contact);
            list.add(data);
        }
        adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void addItem(String name, String blood, int age, String contact) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(name, blood, age, contact);
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

    private void updateItem(int id, String name, String blood, int age, String contact){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, name, blood, age, contact);
        if (updated) {
            Toast.makeText(this,"Item updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Item doesn't updated.", Toast.LENGTH_LONG).show();
        }
    }

    private void input_dialog(final int id, String name, String blood, int age, String contact) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);

        etName = dialog.findViewById(R.id.ETName);
        etBlood = dialog.findViewById(R.id.ETBlood);
        etAge = dialog.findViewById(R.id.ETAge);
        etContact = dialog.findViewById(R.id.ETContact);

        saveBtn = dialog.findViewById(R.id.SaveBtn);

        if (id != 0 && name != null && blood != null && age != 0 && contact != null) {
            etName.setText(name);
            etBlood.setText(blood);
            etAge.setText(Integer.toString(age));
            etContact.setText(contact);
            saveBtn.setText("Update");
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || etAge.getText().toString().length() <= 0 ||
                            etBlood.getText().toString().length() <= 0 || etContact.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        updateItem(
                                id, etName.getText().toString(), etBlood.getText().toString(),
                                Integer.parseInt(etAge.getText().toString()), etContact.getText().toString()
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
                    if (etName.getText().toString().length() <= 0 || etBlood.getText().toString().length() <= 0 ||
                            etAge.getText().toString().length() <= 0 || etContact.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        addItem(
                                etName.getText().toString(), etBlood.getText().toString(),
                                Integer.parseInt(etAge.getText().toString()), etContact.getText().toString()
                        );
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }

        dialog.show();
    }
}
