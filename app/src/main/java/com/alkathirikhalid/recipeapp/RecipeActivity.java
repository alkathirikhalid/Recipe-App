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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.alkathirikhalid.bean.RecipeType;
import com.alkathirikhalid.db.RecipeDBAdaptor;
import com.alkathirikhalid.handler.XMLPullParserHandler;

import java.io.IOException;
import java.util.List;

/**
 * <p><strong>Recipe Activity<strong/></p>
 * <p>This Class is the main entry to the application it is used to read recipe data from Database to populate a list view
 * and read data from XML to populate a spinner<p/>
 */
public class RecipeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    /**
     * Spinner for Type Selection from XML file
     */
    private Spinner spinner;
    /**
     * RecipeTypes List
     */
    private List<RecipeType> recipeTypes;
    /**
     * Listview for recipes
     */
    private ListView listView;
    /**
     * Button to filter data by type and button to clear filter
     */
    private Button buttonFilter, buttonClear;
    /**
     * Recipe Adaptor for Read operation to populate the list view
     */
    private RecipeDBAdaptor recipeDBAdaptor;
    /**
     * List Adaptor to hold the data to be populated into a listview
     */
    private ListAdapter listAdapter;
    /**
     * Identifier for Create Activities
     */
    private static final int ACTIVITY_CREATE = 0;
    /**
     * Identifier for Edit Activities
     */
    private static final int ACTIVITY_EDIT = 1;
    /**
     * First Item Identifier for menu
     */
    private static final int CREATE_ID = Menu.FIRST;
    /**
     * Second Item Identifier for menu
     */
    private static final int DELETE_ID = Menu.FIRST + 1;
    /**
     * Third Item Identifier for menu
     */
    private static final int EDIT_ID = Menu.FIRST + 2;

    /**
     * This method is called when the application is first created
     *
     * @param savedInstanceState on app pause or on app pause
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the Content view layout
        setContentView(R.layout.activity_recipe);
        // Find Views in the layout
        findViews();
        // Populate the Spinner with data from Recipe Type XML File
        populateSpinnerData();
        // Populate the List View with data from Recipe Table from Database
        populateListData();
    }

    /**
     * Find all the view in the activity_recipe layout
     */
    public void findViews() {
        spinner = (Spinner) findViewById(R.id.spinner_activity_recipe_layout);
        buttonFilter = (Button) findViewById(R.id.button_filter_activity_recipe_layout);
        // Set onClick listener to Button to filter data by type
        buttonFilter.setOnClickListener(this);
        buttonClear = (Button) findViewById(R.id.button_clear_activity_recipe_layout);
        // Set onClick listener to Button to clear filter
        buttonClear.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listview_activity_recipe_layout);
        // Set onItemClickListener to Listview to detect selection
        listView.setOnItemClickListener(this);
        // Register for context menu to detect long clicks on an item on listview
        registerForContextMenu(listView);
    }

    /**
     * Create context menu
     *
     * @param menu     obj
     * @param v        view obj
     * @param menuInfo menu information
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Add Item with First Item Identifier
        menu.add(0, CREATE_ID, 0, R.string.add_recipe);
        // Add Item with Second Item Identifier
        menu.add(0, EDIT_ID, 0, R.string.edit_recipe);
        // Add Item with Third Item Identifier
        menu.add(0, DELETE_ID, 0, R.string.delete_recipe);
    }

    /**
     * This method is called when a context item is selected
     *
     * @param item selected
     * @return item selected
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get Adaptor view info on the item id selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            // If Delete is selected
            case DELETE_ID:
                // Delete Recipe item with id
                recipeDBAdaptor.deleteRecipe(info.id);
                // Populate list item data to reflect changes
                populateListData();
                return true;
            // If Create is selected
            case CREATE_ID:
                // Create a Recipe
                createRecipe();
                return true;
            // If Edit is selected
            case EDIT_ID:
                // Pass the Key ID to Add Edit Activity for Editing
                Intent i = new Intent(this, AddEditActivity.class);
                i.putExtra(RecipeDBAdaptor.KEY_ID, info.id);
                startActivityForResult(i, ACTIVITY_EDIT);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Populate Spinner Data
     */
    public void populateSpinnerData() {
        // Initialize recipeType
        recipeTypes = null;

        try {
            // Create a new Instance of XMLPullParserHandler
            XMLPullParserHandler parser = new XMLPullParserHandler();
            // parse recipetypes from Assets to List recipeTypes
            recipeTypes = parser.parse(getAssets().open("recipetypes.xml"));
            // Set Array Adaptor layout and recipeType Data to populate to Spinner
            ArrayAdapter<RecipeType> adapter =
                    new ArrayAdapter<RecipeType>(this, R.layout.recipetype_item, recipeTypes);
            spinner.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populate List Data
     */
    public void populateListData() {
        // Create an instance of RecipeDBAdoptor
        recipeDBAdaptor = new RecipeDBAdaptor(this);
        // Open recipeDBAdaptor for Read operation
        recipeDBAdaptor.open();
        // get all Recipes from Database
        Cursor cursor = recipeDBAdaptor.fetchAllRecipes();
        // Set List Adaptor layout and recipes Title as items on List View
        listAdapter = new SimpleCursorAdapter(this, R.layout.listview_item, cursor,
                new String[]{recipeDBAdaptor.KEY_TITLE}, new int[]{
                R.id.listviewlayout_item});
        listView.setAdapter(listAdapter);
    }

    /**
     * Create Option Menu
     *
     * @param menu obj
     * @return true after creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Add Recipe as an Option in Menu
        menu.add(0, CREATE_ID, 0, R.string.add_recipe);
        return true;
    }

    /**
     * Detect Item Selected on Menu
     *
     * @param item selected
     * @return item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If item is Create
            case CREATE_ID:
                // Create Recipe
                createRecipe();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create Recipe
     */
    private void createRecipe() {
        // Start Activity Add Edit witout passing an databse ID
        Intent i = new Intent(this, AddEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    /**
     * Called when an Item is clicked on List View
     *
     * @param adapterView Adaptor view
     * @param view        obj
     * @param position    on the view
     * @param id          of the item on the view
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // Create a new Intent from this Activity to Details Activity to display selected recipe on List View
        Intent intent = new Intent(this, DetailActivity.class);
        // Pass the Key ID of the item selected on List view
        intent.putExtra(RecipeDBAdaptor.KEY_ID, id);
        // Start the Activity
        startActivityForResult(intent, ACTIVITY_EDIT);
    }

    /**
     * Called when a button is clicked
     *
     * @param view obj
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // If button clicked is filter
            case R.id.button_filter_activity_recipe_layout:
                // Get the selected text from spinner
                String filter = spinner.getSelectedItem().toString();
                // Create an Instance of RecipeDBAdator
                recipeDBAdaptor = new RecipeDBAdaptor(this);
                // Open recipeDBAdaptor for Read operations from Database
                recipeDBAdaptor.open();
                // Get recipe data from database by filter
                Cursor cursor = recipeDBAdaptor.fetchLike(filter);
                // populate the listView with he filtered data
                listAdapter = new SimpleCursorAdapter(this, R.layout.listview_item, cursor,
                        new String[]{recipeDBAdaptor.KEY_TITLE}, new int[]{
                        R.id.listviewlayout_item});
                listView.setAdapter(listAdapter);
                break;
            // If button clicked is clear
            case R.id.button_clear_activity_recipe_layout:
                // populate listview data with all Recipe data from database
                populateListData();
                break;
            default:
                break;
        }
    }
}