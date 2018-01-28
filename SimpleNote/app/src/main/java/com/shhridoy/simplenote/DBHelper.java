package com.shhridoy.simplenote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/14/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notepad";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "notes";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DETAILS = "details";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT NOT NULL, " +
            DETAILS + " TEXT NOT NULL);";

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

    public void insertData(String tit, String det) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, tit);
        contentValues.put(DETAILS, det);
        this.getWritableDatabase().insertOrThrow(TABLE_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(TABLE_NAME, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String tit, String det){
        ContentValues cv = new ContentValues();
        cv.put(TITLE, tit);
        cv.put(DETAILS, det);
        int reslt = this.getWritableDatabase().update(TABLE_NAME, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME,null);
        return c;
    }
}
