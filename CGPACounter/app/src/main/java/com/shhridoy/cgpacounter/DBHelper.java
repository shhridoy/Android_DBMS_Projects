package com.shhridoy.cgpacounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/22/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cgpaCounter";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "cgpatable";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String STUD_ID = "stud_id";
    private static final String CGPA = "cgpa";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            STUD_ID + " TEXT NOT NULL, " +
            CGPA + " REAL NOT NULL);";

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

    public void insertData(String name, String stud_id, float cgpa) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(STUD_ID, stud_id);
        contentValues.put(CGPA, cgpa);
        this.getWritableDatabase().insertOrThrow(TABLE_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(TABLE_NAME, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String name, String stud_id, float cgpa){
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(STUD_ID, stud_id);
        cv.put(CGPA, cgpa);
        int reslt = this.getWritableDatabase().update(TABLE_NAME, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME,null);
        return c;
    }
}
