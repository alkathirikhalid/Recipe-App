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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alkathirikhalid.bean.RecipeType;
import com.alkathirikhalid.db.RecipeDBAdaptor;
import com.alkathirikhalid.handler.XMLPullParserHandler;

import java.io.IOException;
import java.util.List;

/**
 * <p><strong>Add Edit Activity<strong/></p>
 * <p>This Class is used to Add and Edit Recipe data to Database<p/>
 */
public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Edit Text for Recipe Title, Ingredients and Steps
     */
    private EditText title, ingredients, steps;
    /**
     * Recipe Database Adaptor for CRUD Operations
     */
    private RecipeDBAdaptor recipeDBAdaptor;
    /**
     * RecipeTypes List
     */
    private List<RecipeType> recipeTypes;
    /**
     * Spinner for Recipe Type
     */
    private Spinner type;
    /**
     * Button to Save Recipe data to Database
     */
    private Button button;
    /**
     * Recipe Database ID
     */
    private Long rowId;
    /**
     * Recipe type name from Database
     */
    String dbType;

    /**
     * This method is called when the application is first created
     *
     * @param savedInstanceState on app pause or stop
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a RecipeDBAdaptor instance
        recipeDBAdaptor = new RecipeDBAdaptor(this);
        // Open Database
        recipeDBAdaptor.open();
        // Set the xml layout view
        setContentView(R.layout.activity_add_edit);
        // Set the layout title
        setTitle(R.string.page_title_editor);
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
        // Initialize Recipe type name from Database
        dbType = "";
        // Populate EditTexts Data from Database
        populateData();
        // Populate Spinner Data from Database
        populateSpinnerData();

    }

    /**
     * Find Views by ID from layout
     */
    public void findViews() {
        title = (EditText) findViewById(R.id.activity_add_edit_title);
        ingredients = (EditText) findViewById(R.id.activity_add_edit_ingredients);
        steps = (EditText) findViewById(R.id.activity_add_edit_steps);
        type = (Spinner) findViewById(R.id.spinner_activity_add_edit_layout);
        button = (Button) findViewById(R.id.button_save_activity_add_edit_layout);
        // Set OnClickListener
        button.setOnClickListener(this);

    }

    /**
     * populate EditTexts Data from Database
     */
    public void populateData() {
        // Check if row id is not null then populate data else editText are empty
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
            // Get Type Data from Database
            dbType = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeDBAdaptor.KEY_TYPE));
        }

    }

    /**
     * Populate Spinner Data
     */
    public void populateSpinnerData() {
        // Initialize List recipeTypes
        recipeTypes = null;
        // Extract Recipe Types from XML
        try {
            XMLPullParserHandler parser = new XMLPullParserHandler();
            recipeTypes = parser.parse(getAssets().open("recipetypes.xml"));
            ArrayAdapter<RecipeType> adapter =
                    new ArrayAdapter<RecipeType>(this, R.layout.recipetype_item, recipeTypes);
            type.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* If Recipe Type Data from Database is null or empty set Spinner position to index 0
        Else set Spinner position to index of Recipe Type Data */
        if (!dbType.equals(null) || !"".equals(dbType)) {
            int viewPosition;
            switch (dbType) {
                case "Vegetarian":
                    viewPosition = 0;
                    break;
                case "Fast Food":
                    viewPosition = 1;
                    break;
                case "Healthy":
                    viewPosition = 2;
                    break;
                case "No-Cook":
                    viewPosition = 3;
                    break;
                case "Make Ahead":
                    viewPosition = 4;
                    break;

                default:
                    viewPosition = 0;
            }

            type.setSelection(viewPosition);
        }

    }

    /**
     * When save button is clicked
     *
     * @param view obj from layout
     */
    @Override
    public void onClick(View view) {
        // Return data back to parent
        setResult(RESULT_OK);
        // Kill Add Edit Activity
        finish();
    }

    /**
     * Called before placing the activity in background state
     *
     * @param outState Bundle on pause or stop
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save State by Adding or Updating Recipe to Database
        saveState();
        // Save rowId into bundle to be used onCreate or onRestoreInstanceState
        outState.putSerializable(RecipeDBAdaptor.KEY_ID, rowId);
    }

    /**
     * called when the application is on pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Save State by Adding or Updating Recipe to Database
        saveState();
    }

    /**
     * Called when the application is on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        // populate EditTexts Data from Database
        populateData();
    }

    /**
     * To preserve UI and Data State of Add Edit Activity
     */
    private void saveState() {
        // Get Title
        String titleString = title.getText().toString();
        // Get Ingredients
        String ingredientString = ingredients.getText().toString();
        // Get Steps
        String stepsString = steps.getText().toString();
        // Get Type
        String typeString = type.getSelectedItem().toString();
        // If any of that data is empty then there is nothing to save into database
        if (titleString.equals(null) || "".equals(title) || ingredientString.equals(null) || "".equals(ingredientString) || stepsString.equals(null) || "".equals(stepsString) || typeString.equals(null) || "".equals(typeString)) {
            // Notify User Data must be complete to be saved into databse
            Toast.makeText(this, R.string.toast_notification, Toast.LENGTH_SHORT).show();
        } else {
            // If row Id is null and data is not null nor empty then save new recipe entry to Database
            if (rowId == null) {
                long id = recipeDBAdaptor.createRecipe(titleString, ingredientString, stepsString, typeString);
                if (id > 0) {
                    rowId = id;
                }
              // Else update recipe into Database
            } else {
                recipeDBAdaptor.updateRecipe(rowId, titleString, ingredientString, stepsString, typeString);
            }
        }
    }

}
