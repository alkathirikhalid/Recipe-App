/*
 * Copyright (c) 2017 Al-Kathiri Khalid www.alkathirikhalid.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.alkathirikhalid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <p><strong>Recipe Database Adaptor<strong/></p>
 * <p>This Class is used to define the Recipe Database name, table, version, columns and create statement<p/>
 */
public class RecipeDBAdaptor {
    /**
     * Database Name
     */
    private static final String DATABASE_NAME = "recipedb";
    /**
     * Database Table
     */
    private static final String DATABASE_TABLE = "recipe";
    /**
     * Database Version
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * Table Column ID
     */
    public static final String KEY_ID = "_id";
    /**
     * Table Column Title
     */
    public static final String KEY_TITLE = "title";
    /**
     * Table Column Ingredients
     */
    public static final String KEY_INGREDIENTS = "ingredients";
    /**
     * Table Column Steps
     */
    public static final String KEY_STEPS = "steps";
    /**
     * Table Column Type
     */
    public static final String KEY_TYPE = "type";
    /**
     * Create Table Statement
     */
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE + " TEXT NOT NULL, " + KEY_INGREDIENTS + " TEXT NOT NULL, " + KEY_STEPS + " TEXT NOT NULL, " + KEY_TYPE + " TEXT NOT NULL)";
    /**
     * Drop Table Statement
     */
    private static String DROP_TABLE = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
    /**
     * Application Context
     */
    private final Context mContext;
    /**
     * Subclass
     */
    private DBHelper mDBHelper;
    /**
     * SQLiteDatabase
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Class Constructor
     *
     * @param context of the class instantiating RecipeDBAdaptor
     */
    public RecipeDBAdaptor(Context context) {
        this.mContext = context;
    }

    /**
     * This Subclass is used to execute CRUD database statements
     */
    private static class DBHelper extends SQLiteOpenHelper {
        /**
         * Subclass Constructor
         *
         * @param context of the class instantiating RecipeDBAdaptor super class
         */
        public DBHelper(Context context) {
            /**
             * SQLiteOpenHelper Constructor
             */
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * When the Database is first created
         *
         * @param sqLiteDatabase Obj for CRUD operations
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            // Create Table
            sqLiteDatabase.execSQL(CREATE_TABLE);
            // Dummy Data 1
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_TITLE, "Chocolate Cake");
            contentValues.put(KEY_INGREDIENTS, "- Chocolate\n- Flour\n- Milk\n- Sugar\n- Eggs");
            contentValues.put(KEY_STEPS, "1. Mix Eggs with Milk\n2. Mix Flour with Chocolate and Sugar\n3. Mix all together\n4. Bake for 40min");
            contentValues.put(KEY_TYPE, "Make Ahead");
            sqLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
            // Dummy Data 2
            contentValues.put(KEY_TITLE, "Vanilla Cake");
            contentValues.put(KEY_INGREDIENTS, "- Vanilla\n- Flour\n- Milk\n- Sugar\n- Eggs");
            contentValues.put(KEY_STEPS, "1. Mix Eggs with Milk\n2. Mix Flour with Vanilla and Sugar\n3. Mix all together\n4. Bake for 40min");
            contentValues.put(KEY_TYPE, "No-Cook");
            sqLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
            // Dummy Data 3
            contentValues.put(KEY_TITLE, "Strawberry Cake");
            contentValues.put(KEY_INGREDIENTS, "- Strawberry\n- Flour\n- Milk\n- Sugar\n- Eggs");
            contentValues.put(KEY_STEPS, "1. Mix Eggs with Milk\n2. Mix Flour with Strawberry and Sugar\n3. Mix all together\n4. Bake for 40min");
            contentValues.put(KEY_TYPE, "Healthy");
            sqLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
        }

        /**
         * When the Database is upgraded
         *
         * @param sqLiteDatabase Obj for CRUD operations
         * @param newVersion int new version value
         * @param oldVersion int old version value
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // Drop Table
            sqLiteDatabase.execSQL(DROP_TABLE);
            // Recreate Table
            onCreate(sqLiteDatabase);

        }
    }

    /**
     * Open Database
     *
     * @return context of the open RecipeDBAdaptor
     * @throws SQLException throw SQL Exception
     */
    public RecipeDBAdaptor open() throws SQLException {
        // Get an instant of DBHelper Subclass and pass in the constructor
        mDBHelper = new DBHelper(mContext);
        // Get Writable Database
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close Database
     */
    public void close() {
        // Close the instants of Database Helper Subclass
        mDBHelper.close();
    }

    /**
     * Add a Recipe to Database
     *
     * @param title of recipe
     * @param ingredients of recipe
     * @param steps of recipe
     * @param type of recipe
     */
    public long createRecipe(String title, String ingredients, String steps, String type) {
        // Add the Recipe data to database
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_INGREDIENTS, ingredients);
        contentValues.put(KEY_STEPS, steps);
        contentValues.put(KEY_TYPE, type);
        return mSQLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
    }

    /**
     * Delete Recipe by Id from Database
     *
     * @param keyID of recipe row
     * @return boolean true if deletion is successful
     */
    public boolean deleteRecipe(long keyID) {
        return mSQLiteDatabase.delete(DATABASE_TABLE, KEY_ID + "=" + keyID, null) > 0;
    }

    /**
     * Get all Recipes from Database
     *
     * @return Cursor with recipes
     */
    public Cursor fetchAllRecipes() {
        return mSQLiteDatabase.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TITLE,
                KEY_INGREDIENTS, KEY_STEPS, KEY_TYPE}, null, null, null, null, null);
    }

    /**
     * Get a Recipe matching an exact filter value
     *
     * @param filter data by selection
     * @return Cursor with selection recipes matching filter
     */
    public Cursor fetchLike(String filter) {
        return mSQLiteDatabase.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                        KEY_TITLE, KEY_INGREDIENTS, KEY_STEPS, KEY_TYPE}, KEY_TYPE + " LIKE ?",
                new String[]{"%" + filter + "%"}, null, null, null,
                null, null);
    }

    /**
     * Get a Recipe by Id from Database
     *
     * @param keyID row id of Recipe
     * @return Cursor of recipe data
     * @throws SQLException throw SQL Exception
     */
    public Cursor fetchRecipe(long keyID) throws SQLException {

        Cursor mCursor = mSQLiteDatabase.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                        KEY_TITLE, KEY_INGREDIENTS, KEY_STEPS, KEY_TYPE}, KEY_ID + "=" + keyID, null,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update a Recipe values in Database
     *
     * @param keyID of recipe row
     * @param title of recipe
     * @param ingredients of recipe
     * @param steps of recipe
     * @param type of recipe
     * @return boolean true if update recipe is successful
     */
    public boolean updateRecipe(long keyID, String title, String ingredients, String steps, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_INGREDIENTS, ingredients);
        contentValues.put(KEY_STEPS, steps);
        contentValues.put(KEY_TYPE, type);

        return mSQLiteDatabase.update(DATABASE_TABLE, contentValues, KEY_ID + "=" + keyID, null) > 0;
    }
}