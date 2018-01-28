package com.shhridoy.employeeinformation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/24/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "employeeInfo.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "employee";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMP_ID = "emp_id";
    private static final String DESIGNATION = "designation";
    private static final String SALARY = "salary";
    private static final String JOINING_DATE = "joining_date";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            EMP_ID + " TEXT NOT NULL, " +
            DESIGNATION + " TEXT, " +
            SALARY + " REAL, " +
            JOINING_DATE + " TEXT);";

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

    public void insertData(String name, String emp_id, String designation, float salary, String joining_date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(EMP_ID, emp_id);
        contentValues.put(DESIGNATION, designation);
        contentValues.put(SALARY, salary);
        contentValues.put(JOINING_DATE, joining_date);
        this.getWritableDatabase().insertOrThrow(TABLE_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(TABLE_NAME, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String name, String emp_id, String designation, float salary, String joining_date){
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(EMP_ID, emp_id);
        cv.put(DESIGNATION, designation);
        cv.put(SALARY, salary);
        cv.put(JOINING_DATE, joining_date);
        int reslt = this.getWritableDatabase().update(TABLE_NAME, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME,null);
        return c;
    }
}
