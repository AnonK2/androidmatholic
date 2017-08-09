package com.utilfreedom.brainmath.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static com.utilfreedom.brainmath.model.Constant.DATABASE_NAME;
import static com.utilfreedom.brainmath.model.Constant.SCORES_COLUMN_DIFFICULTY;
import static com.utilfreedom.brainmath.model.Constant.SCORES_COLUMN_ID;
import static com.utilfreedom.brainmath.model.Constant.SCORES_COLUMN_NAME;
import static com.utilfreedom.brainmath.model.Constant.SCORES_COLUMN_SCORE;
import static com.utilfreedom.brainmath.model.Constant.SCORES_TABLE_NAME;

/**
 * Created by kennywang on 6/20/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE scores (" + SCORES_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                                          SCORES_COLUMN_NAME + " TEXT, " +
                                          SCORES_COLUMN_SCORE + " INTEGER, " +
                                          SCORES_COLUMN_DIFFICULTY + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + SCORES_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertScore (String name, int score, String difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put("name", name);
        mContentValues.put("score", score);
        mContentValues.put("difficulty", difficulty);
        db.insert("scores", null, mContentValues); // <- what is this null?

        return true;
    }

    public Cursor getBestScore(String difficulty) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor =  db.rawQuery( "SELECT score FROM " + SCORES_TABLE_NAME + " WHERE difficulty=\"" + difficulty + "\" ORDER BY score DESC LIMIT 1", null );
        return mCursor;
    }
//
//    public int numberOfRows(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, SCORES_TABLE_NAME);
//        return numRows;
//    }
//
//    public Integer deleteScore (Integer id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(SCORES_TABLE_NAME,
//                "id = ? ",
//                new String[] { Integer.toString(id) });
//    }

    public ArrayList<String[]> getAllScore(String difficulty) {
        ArrayList<String[]> scoreArrayList = new ArrayList<String[]>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor =  db.rawQuery( "SELECT * FROM " + SCORES_TABLE_NAME + " WHERE "
                                    + SCORES_COLUMN_DIFFICULTY + "=\"" + difficulty + "\" ORDER BY score DESC LIMIT 10", null );
        mCursor.moveToFirst();

        while(mCursor.isAfterLast() == false){
            scoreArrayList.add(new String[] {
                    mCursor.getString(mCursor.getColumnIndex(SCORES_COLUMN_NAME)) ,
                    mCursor.getString(mCursor.getColumnIndex(SCORES_COLUMN_SCORE))
            });
            mCursor.moveToNext();
        }

        return scoreArrayList;
    }
}
