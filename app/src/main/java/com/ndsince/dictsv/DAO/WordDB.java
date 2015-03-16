package com.ndsince.dictsv.DAO;

import android.content.Context;

import java.util.ArrayList;

/**
 * WordDB
 * Add new data to database
 */
public class WordDB {

    public static final String TAG = "WordDB";

    private Context mContext;
    private CategoryDAO categoryDAO;
    private WordDAO wordDAO;
    private ArrayList<String> categoriesArrayList = new ArrayList<>();

    //Category Data
    private String Computer_Category = "Computer";
    private String Math_Category = "Math";

    /**
     * Constructor
     * @param context
     */
    public WordDB(Context context) {
        this.mContext = context;

        //Create Data
        createCategory();
        createWord();

    }   // Constructor

    /**
     * //Getting Index of an item in an arraylist
     * @param categoryName category name
     * @return index of category id
     */
    public int getIndexByName(String categoryName) {
        for (String word : categoriesArrayList) {
            if (word.equals(categoryName)) {
                return categoriesArrayList.indexOf(word);
            }
        }
        return -1;
    }   // getIndexByName

    /**
     * Create New Category
     */
    private void createCategory() {
        categoriesArrayList.add(0, "Category");
        categoriesArrayList.add(1, Computer_Category);
        categoriesArrayList.add(2, Math_Category);

        //add new category to database
        categoryDAO = new CategoryDAO(mContext);
        categoryDAO.addCategory(new Category(categoriesArrayList.get
                (getIndexByName(Computer_Category))));
        categoryDAO.addCategory(new Category(categoriesArrayList.get
                (getIndexByName(Math_Category))));
    }   // createCategory

    /**
     * Create New word
     */
    private void createWord() {
        wordDAO = new WordDAO(mContext);
        wordDAO.addWord("A", "เอ", "เอ", getIndexByName(Computer_Category));

    }   // createWord

}   // class
