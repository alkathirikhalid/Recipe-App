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
package com.alkathirikhalid.handler;

import com.alkathirikhalid.bean.RecipeType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><strong>XML Pull Parser Handler<strong/></p>
 * <p>This Class is used to extract Recipe Types from an XML file<p/>
 */
public class XMLPullParserHandler {
    /**
     * List of type Recipe
     */
    private List<RecipeType> recipeTypes;
    /**
     * Recipe Type
     */
    private RecipeType recipeType;
    /**
     * Recipe Type value name
     */
    private String text;

    /**
     * Constructor
     */
    public XMLPullParserHandler() {
        recipeTypes = new ArrayList<RecipeType>();
    }

    /**
     * Get the Recipe Types
     *
     * @return List recipeTypes
     */
    public List<RecipeType> getRecipeTypes() {
        return recipeTypes;
    }

    /**
     * Parse XML Data
     *
     * @param inputStream xml data
     * @return List recipeTypes
     */
    public List<RecipeType> parse(InputStream inputStream) {
        // Declare XMLPullParserFactory
        XmlPullParserFactory factory = null;
        // Declare XMLParser
        XmlPullParser parser = null;
        try {
            // Get an Instance of XMLPULLFacotry
            factory = XmlPullParserFactory.newInstance();
            // Set NamespaceAware
            factory.setNamespaceAware(true);
            // Create an instance of PullParser
            parser = factory.newPullParser();
            // Set parser input
            parser.setInput(inputStream, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("recipetype")) {
                            // Create an Instantce of RecipeType
                            recipeType = new RecipeType();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("recipetype")) {
                            // Add recipeType obj to List recipeTypes
                            recipeTypes.add(recipeType);
                        } else if (tagname.equalsIgnoreCase("name")) {
                            recipeType.setName(text);
                        }
                        break;
                    default:
                        break;
                }
                // Process Event until the event is END_DOCUMENT
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return List recipeTypes
        return recipeTypes;
    }
}