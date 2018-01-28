package com.shhridoy.simplecontactdirectory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/17/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contactDirectory";
    private static final int DB_VERSION = 1;

    private static final String CONTACTS_TABLE = "contacts";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String NUMBER = "number";

    private static final String CREATE_TABLE = "CREATE TABLE " + CONTACTS_TABLE +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            NUMBER + " TEXT NOT NULL);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + CONTACTS_TABLE;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void insertData(String name, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(NUMBER, number);
        this.getWritableDatabase().insertOrThrow(CONTACTS_TABLE, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(CONTACTS_TABLE, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String name, String number){
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(NUMBER, number);
        int reslt = this.getWritableDatabase().update(CONTACTS_TABLE, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ CONTACTS_TABLE,null);
        return c;
    }
}