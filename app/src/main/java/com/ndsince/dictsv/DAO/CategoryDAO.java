package com.ndsince.dictsv.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.ndsince.dictsv.LogCheck;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Category data access object
 *
 * @see Category
 */
public class CategoryDAO {

    public static final String TAG = "CategoryDAO";

    //Database field
    private SQLiteDatabase mDb;
    private DBHelper mDbHelper;
    private String[] mAllColumns = {DBHelper.COLUMN_CATEGORY_ID,
            DBHelper.COLUMN_CATEGORY_NAME};

    private Context mContext;
    private WordDAO wordDAO;
    private Category category;
    private Word word;

    /**
     * Constructor
     *
     * @param Context
     */
    public CategoryDAO(Context Context) {
        this.mContext = Context;
        mDbHelper = new DBHelper(Context);

        //open database
        try {
            open();
        } catch (SQLException e) {
            //Log.e(TAG, "SQLException on opening database" + e.getMessage());
        }
    }   // Constructor

    /**
     * Open database connection
     */
    private void open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
    }   // Open Database

    //----------------------------------------------------------------------------------------------

    /**
     * Add new cartegory to database
     *
     * @param category Category object
     * @see Category
     */
    public void addCategory(Category category) {
        //Add Content value and insert row
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CATEGORY_NAME, category.getmName());

        long insertID = mDb.insert(DBHelper.TABLE_CATEGORY, null, values);

        if(insertID>0) Toast.makeText(mContext, "Add Word Successful", Toast.LENGTH_SHORT).show();
        LogCheck.d(TAG, "addCategory", String.valueOf(insertID) + " - " + category.getmName());
    }

    /**
     * update category by ID
     * @param category
     */
    public void updateCategory(Category category) {

        //adding content
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CATEGORY_NAME, category.getmName());

        //update
        //UPDATE category SET category_name ="New_name" WHERE category_name = "name"
        String[] whereArgs = new String[]{String.valueOf(category.getmId())};
        int updateID = mDb.update(DBHelper.TABLE_CATEGORY, values,
                DBHelper.COLUMN_CATEGORY_ID + "=?", whereArgs);

        if(updateID>0) Toast.makeText(mContext, "Update category Successful", Toast.LENGTH_SHORT).show();
        LogCheck.d(TAG, "addCategory", String.valueOf(updateID) + " - " + category.getmName());
    }

    /**
     * delete Category by ID
     * @param category
     */
    public void deleteCategory(Category category, boolean chkWordsEmpty) {
        wordDAO = new WordDAO(mContext);

        if(!chkWordsEmpty) {
            word = new Word();

            word.setmCategory(category);
            wordDAO.deleteWordByCategoryID(word);
        }

        String[] whereArgs = new String[]{String.valueOf(category.getmId())};
        int deleteID = mDb.delete(DBHelper.TABLE_CATEGORY,
                DBHelper.COLUMN_CATEGORY_ID + "=?", whereArgs);

        if (deleteID > 0) LogCheck.d(TAG, "deleteWordByCategoryID" ,
                "ลบหมวด " + category.getmName() + " แล้ว");
        else LogCheck.d(TAG, "deleteWordByCategoryID" ,
                "ไม่พบหมวด " + category.getmName());
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Get all category list from category table
     *
     * @return Category arrayList
     */
    public ArrayList<String> getAllStringListCategory() {

        ArrayList<String> listCategories = new ArrayList<>();
        category = new Category();

        StringBuffer buffer = new StringBuffer();

        Cursor cursor = mDb.query(DBHelper.TABLE_CATEGORY, mAllColumns,
                null, null, null, null, null);

        //add cursor to arrayList
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                category = cursorToCategory(cursor);
                listCategories.add(category.getmName());

                cursor.moveToNext();
            }
        }
        return listCategories;
    }

    /**
     * getCategoryObjByName
     * @param categoryName
     * @return
     */
    public Category getCategoryObjByName(String categoryName) {
        category = new Category();

        String[] whereArgs = new String[]{String.valueOf(categoryName)};
        Cursor cursor = mDb.query(DBHelper.TABLE_CATEGORY, mAllColumns,
                DBHelper.COLUMN_CATEGORY_NAME + "=?", whereArgs, null, null, null, null);

        //cursor management
        if (cursor != null) {
            cursor.moveToFirst();
            category = cursorToCategory(cursor);
        }
        return category;
    }

    /**
     * Check same category row
     * @param categoryName
     * @return
     */
    public boolean chkRepetitionCategory(String categoryName) {
        boolean chkRepetitionCategory = true;

        //SELECT * FROM Words WHERE category_id = 0 AND word = wordName
        //Query insensitive case
        Cursor cursor = mDb.query(DBHelper.TABLE_CATEGORY, mAllColumns,
                "lower(" + DBHelper.COLUMN_CATEGORY_NAME + ")=" +
                "lower('" + categoryName + "')" ,
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.isAfterLast()) chkRepetitionCategory = false;
            else chkRepetitionCategory = true;
        }

        if (cursor != null) cursor.close();
        return chkRepetitionCategory;
    }

    /**
     * Get cursor data to category object
     *
     * @param cursor
     * @return Category Object
     */
    private Category cursorToCategory(Cursor cursor) {
        category = new Category();

        category.setmId(cursor.getInt(
                cursor.getColumnIndex(DBHelper.COLUMN_CATEGORY_ID)));
        category.setmName(cursor.getString(
                cursor.getColumnIndex(DBHelper.COLUMN_CATEGORY_NAME)));

        return category;
    }
}
