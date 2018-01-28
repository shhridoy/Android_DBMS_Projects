package com.shhridoy.techinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/21/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "techInfo";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "tech";
    private static final String ID = "id";
    private static final String NAME = "title";
    private static final String PRICE = "price";
    private static final String REVENUE = "revenue";
    private static final String YEARLY_INCOME = "income";
    private static final String ESTB_DATE = "estb_date";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            PRICE + " TEXT NOT NULL, " +
            REVENUE + " TEXT, " +
            YEARLY_INCOME + " TEXT, " +
            ESTB_DATE + " TEXT);";

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

    public void insertData(String name, String price, String revenue, String yearly_income, String estb_date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(PRICE, price);
        contentValues.put(REVENUE, revenue);
        contentValues.put(YEARLY_INCOME, yearly_income);
        contentValues.put(ESTB_DATE, estb_date);
        this.getWritableDatabase().insertOrThrow(TABLE_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(TABLE_NAME, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String name, String price, String revenue, String yearly_income, String estb_date){
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(PRICE, price);
        cv.put(REVENUE, revenue);
        cv.put(YEARLY_INCOME, yearly_income);
        cv.put(ESTB_DATE, estb_date);
        int reslt = this.getWritableDatabase().update(TABLE_NAME, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME,null);
        return c;
    }
}