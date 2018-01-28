package com.shhridoy.blooddonorinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/21/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bloodDonor";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "donor";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String BLOOD_GROUP = "blood_group";
    private static final String AGE = "age";
    private static final String CONTACT = "contact";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            BLOOD_GROUP + " TEXT NOT NULL, " +
            AGE + " INTEGER NOT NULL, " +
            CONTACT + " TEXT);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

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

    public void insertData(String name, String blood_group, int age, String contact) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(BLOOD_GROUP, blood_group);
        contentValues.put(AGE, age);
        contentValues.put(CONTACT, contact);
        this.getWritableDatabase().insertOrThrow(TABLE_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(TABLE_NAME, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String name, String blood_group, int age, String contact){
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(BLOOD_GROUP, blood_group);
        cv.put(AGE, age);
        cv.put(CONTACT, contact);
        int reslt = this.getWritableDatabase().update(TABLE_NAME, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME,null);
        return c;
    }
}