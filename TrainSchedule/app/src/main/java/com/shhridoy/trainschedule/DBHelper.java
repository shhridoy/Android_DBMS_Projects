package com.shhridoy.trainschedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/18/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "trainschedule.db";
    private static final int DB_VERSION = 1;

    private static final String SCHEDULE_TABLE = "train_schedule";
    private static final String ID = "id";
    private static final String TRAIN_NAME = "train_name";
    private static final String OUT_TIME = "out_time";
    private static final String IN_TIME = "in_time";
    private static final String GATE_NO = "gate_no";

    private static final String CREATE_TABLE = "CREATE TABLE " + SCHEDULE_TABLE +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TRAIN_NAME + " TEXT NOT NULL, " +
            OUT_TIME + " TEXT NOT NULL, " +
            IN_TIME + " TEXT, " +
            GATE_NO + " INTEGER);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + SCHEDULE_TABLE;

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

    public void insertData(String train_name, String out_time, String in_time, int gate_no) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRAIN_NAME, train_name);
        contentValues.put(OUT_TIME, out_time);
        contentValues.put(IN_TIME, in_time);
        contentValues.put(GATE_NO, gate_no);
        this.getWritableDatabase().insertOrThrow(SCHEDULE_TABLE, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(SCHEDULE_TABLE, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String train_name, String out_time, String in_time, int gate_no){
        ContentValues cv = new ContentValues();
        cv.put(TRAIN_NAME, train_name);
        cv.put(OUT_TIME, out_time);
        cv.put(IN_TIME, in_time);
        cv.put(GATE_NO, gate_no);
        int reslt = this.getWritableDatabase().update(SCHEDULE_TABLE, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ SCHEDULE_TABLE,null);
        return c;
    }
}