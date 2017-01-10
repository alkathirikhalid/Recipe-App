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
package com.alkathirikhalid.recipeapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alkathirikhalid.db.RecipeDBAdaptor;

/**
 * <p><strong>Detail Activity<strong/></p>
 * <p>This Class is used to read recipe data from Database<p/>
 */
public class DetailActivity extends AppCompatActivity {
    /**
     * Text View for Recipe Title, Ingredients, Steps and Type
     */
    private TextView title, ingredients, steps, type;
    /**
     * Recipe Database Adaptor for CRUD Operations
     */
    private RecipeDBAdaptor recipeDBAdaptor;
    /**
     * Recipe Database ID
     */
    private Long rowId;

    /**
     * This method is called when the application is first created
     *
     * @param savedInstanceState on pause or on stop
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a RecipeDBAdaptor instance
        recipeDBAdaptor = new RecipeDBAdaptor(this);
        // Open Database
        recipeDBAdaptor.open();
        // Set the xml layout view
        setContentView(R.layout.activity_detail);
        // Set the layout title
        setTitle(R.string.page_title_recipe);
        // Find views from layout
        findViews();
        // Get rowID from SavedInstanceState
        rowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(RecipeDBAdaptor.KEY_ID);
        // If rowId is still null get rowID from Bundle else rowID is null
        if (rowId == null) {
            Bundle extras = getIntent().getExtras();
            rowId = extras != null ? extras.getLong(RecipeDBAdaptor.KEY_ID)
                    : null;
        }
        // Populate EditTexts Data from Database
        populateData();
    }

    /**
     * Find Views by ID from layout
     */
    public void findViews() {
        title = (TextView) findViewById(R.id.activity_detail_title);
        ingredients = (TextView) findViewById(R.id.activity_detail_ingredients);
        steps = (TextView) findViewById(R.id.activity_detail_steps);
        type = (TextView) findViewById(R.id.activity_detail_type);
    }

    /**
     * populate TextViews Data from Database
     */
    public void populateData() {
        /* Check if row id is not null then populate data
        * Else TextView are empty is never reached as this class is activated onItemClickListener
        */
        if (rowId != null) {
            // Get Recipe data from database with Id
            Cursor cursor = recipeDBAdaptor.fetchRecipe(rowId);
            startManagingCursor(cursor);
            // Set Title Data
            title.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeDBAdaptor.KEY_TITLE)));
            // Set Ingredients Data
            ingredients.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeDBAdaptor.KEY_INGREDIENTS)));
            // Set Steps Data
            steps.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeDBAdaptor.KEY_STEPS)));
            // Set Type Data
            type.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeDBAdaptor.KEY_TYPE)));
        }
    }
}
