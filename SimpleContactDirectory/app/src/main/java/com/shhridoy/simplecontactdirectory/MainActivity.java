package com.shhridoy.simplecontactdirectory;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ListView listView;
    ListviewAdapter listviewAdapter;
    ArrayList<ListViewData> arrayList;
    private static final int CALL_PERMISSION_CODE = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.LV);
        arrayList = new ArrayList<>();

        callPermission();

        retrieveData();
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
            inputDialog(0, null, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                view = inflater.inflate(R.layout.list_item, viewGroup, false);
            }

            TextView nameTV = view.findViewById(R.id.ListItemTV);
            ImageButton callIB = view.findViewById(R.id.CallImageButton);

            final ListViewData listViewData = arrayList.get(i);

            nameTV.setText(listViewData.getName());

            nameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    read_dialog(listViewData.getId(), listViewData.getName(), listViewData.getNumber());
                }
            });

            callIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode(listViewData.getNumber().trim())));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                }
            });

            notifyDataSetChanged();
            return view;
        }
    }

    private void retrieveData(){
        dbHelper = new DBHelper(this);
        Cursor c = dbHelper.getData();
        arrayList.clear();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String number = c.getString(2);
            ListViewData listViewData = new ListViewData(id, name, number);
            arrayList.add(listViewData);
        }
        listviewAdapter = new ListviewAdapter(this, arrayList);
        listView.setAdapter(listviewAdapter);
    }

    private void addItem(String name, String number) {
        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(name, number);
            Toast.makeText(this, "Contact added!", Toast.LENGTH_LONG).show();
        } catch (SQLiteException e){
            Toast.makeText(this, "Contact can't be added!", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteItem(int id){
        dbHelper = new DBHelper(this);
        boolean deleted = dbHelper.deleteData(id);
        if (deleted) {
            Toast.makeText(this,"Contact is deleted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Contact can't be deleted!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateItem(int id, String name, String number){
        dbHelper = new DBHelper(this);
        boolean updated = dbHelper.updateData(id, name, number);
        if (updated) {
            Toast.makeText(this,"Contact is updated!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Contact can't be updated!", Toast.LENGTH_LONG).show();
        }
    }

    private void inputDialog(final int id, String name, String number){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        final EditText etName = dialog.findViewById(R.id.NameET);
        final EditText etNumber = dialog.findViewById(R.id.NumberET);
        Button saveBtn = dialog.findViewById(R.id.saveButton);

        if (id != 0 && name != null && number != null) {
            etName.setText(name);
            etNumber.setText(number);
            saveBtn.setText("Update");
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || etNumber.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        updateItem(id, etName.getText().toString(), etNumber.getText().toString());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        } else {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etName.getText().toString().length() <= 0 || etNumber.getText().toString().length() <= 0) {
                        Toast.makeText(MainActivity.this, "You shouldn't keep any field blank!", Toast.LENGTH_LONG).show();
                    } else {
                        addItem(etName.getText().toString(), etNumber.getText().toString());
                        dialog.dismiss();
                        retrieveData();
                    }
                }
            });
        }

        dialog.show();
    }

    private void read_dialog(final int ID, final String name, final String number) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.read_dialog);
        TextView tvName = dialog.findViewById(R.id.NameTV);
        TextView tvNumber = dialog.findViewById(R.id.NumberTV);
        Button deleteBtn = dialog.findViewById(R.id.deleteButton);
        Button editBtn = dialog.findViewById(R.id.editButton);

        tvName.setText(name);
        tvNumber.setText(number);

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
                inputDialog(ID, name, number);
            }
        });

        dialog.show();
    }

    private void callPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Call phone permission is necessary to make a call!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access of making call is granted.", Toast.LENGTH_SHORT).show();
                } else {
                    //code for deny
                    Toast.makeText(this, "Access of making call is denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
