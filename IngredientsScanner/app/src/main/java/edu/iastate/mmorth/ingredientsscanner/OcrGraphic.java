/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// NOTE: This file has been modified.

/**
 * NOTICE: This file has been modified from its original state.
 */

package edu.iastate.mmorth.ingredientsscanner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

import edu.iastate.mmorth.ingredientsscanner.ui.camera.GraphicOverlay;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view. This class is used to display the detected ingredients on the CameraSourcePreview.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    /**
     * The id of this OcrGraphic
     */
    private int id;

    /**
     * Create a reference to the safe (green) text color.
     */
    private static final int SAFE_TEXT_COLOR = Color.GREEN;

    /**
     * Create a reference to the unsafe (red) text color for an allergy.
     */
    private static final int AVOID_TEXT_COLOR = Color.RED;

    /**
     * Holds information about how to overlay the rectangle.
     */
    private static Paint rectPaint;

    /**
     * Holds information about how to overlay the text.
     */
    private static Paint textPaint;

    /**
     * Contains the text to overlay.
     */
    private final TextBlock textBlock;

    // MY ADDED CODE STARTS HERE
    /**
     * Stores a reference to the Allergy database.
     */
    private AppDatabase database;

    /**
     * Stores a list of allergies stored in the Allergy database.
     */
    List<Allergy> returnedAllergies;

    GraphicOverlay graphicOverlay;
    // MY ADDED CODE ENDS HERE

    /**
     * Constructs a new OcrGraphic object.
     *
     * @param overlay The GraphicOverlay to use.
     * @param text    The TextBlock that contains the text to overlay on the screen.
     */
    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        graphicOverlay = overlay;

        textBlock = text;

        if (rectPaint == null) {
            rectPaint = new Paint();
            rectPaint.setColor(SAFE_TEXT_COLOR);
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setStrokeWidth(4.0f);
        }

        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setColor(SAFE_TEXT_COLOR);
            textPaint.setTextSize(12.0f);
        }

        // Redraw the overlay, as this graphic has been added.
        postInvalidate();

        // MY ADDED CODE STARTS HERE
        database = AppDatabase.getAppDatabase(OcrCaptureActivity.context);

        // Get the list of allergeies
        returnedAllergies = database.allergyDao().getAll();
        // MY ADDED CODE ENDS HERE
    }

    /**
     * Returns the id of this OcrGraphic.
     *
     * @return The id of this OcrGraphic.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for this OcrGraphic.
     *
     * @param id The new id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the TextBlock associated with this OcrGraphic.
     *
     * @return The TextBlock associated with this OcrGraphic.
     */
    public TextBlock getTextBlock() {
        return textBlock;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     *
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        if (textBlock == null) {
            return false;
        }
        RectF rect = new RectF(textBlock.getBoundingBox());
        rect = translateRect(rect);
        return rect.contains(x, y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     * This is where the user-defined allergies are compared against the detected ingredients on the
     * food label. If a match is found, the text is shown in red. If no match is found, the text is
     * shown in green.
     */
    @Override
    public void draw(Canvas canvas) {
        if (textBlock == null) {
            return;
        }

        // MY ADDED CODE STARTS HERE:
        // Get all lines from the scanned text
        List<? extends Text> lines = textBlock.getComponents();

        // Loop through each detected line
        for (Text currentLine : lines) {

            // Get the individual ingredients from the current line
            List<? extends Text> ingredients = currentLine.getComponents();

            // Loop through each ingredient in the line
            for (Text currentIngredient : ingredients) {

                // Get a String representation of the current ingredient with all non-letters, numbers, or - taken off
                String ingredient = currentIngredient.getValue().replaceAll("[^-a-zA-Z ]", "").toLowerCase();

                // Variable that stores whether the current word is a potential allergy ingredient
                boolean allergyDetected = false;

                // Loop through each user-input allergy
                for (Allergy currentAllergy : returnedAllergies) {

                    // Get the current allergy name
                    String allergy = currentAllergy.getAllergyName();

                    // Convert the current allergy to lower caase
                    allergy = allergy.toLowerCase();

                    // Split the allergy up into multiple words
                    String[] allergySplit = allergy.split(" ");

                    // Loop through each allergy word
                    for (String allergyWord : allergySplit) {

                        // Check if the ingredient matches the allergy word
                        if (ingredient.equals(allergyWord)) {

                            // Draw the ingredient as red on the overlay
                            textPaint.setColor(AVOID_TEXT_COLOR);
                            textPaint.setTextSize(20.0f);
                            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            float left = translateX(currentIngredient.getBoundingBox().left);
                            float bottom = translateY(currentIngredient.getBoundingBox().bottom);
                            canvas.drawText(currentIngredient.getValue(), left, bottom, textPaint);

                            // Send vibration when allergy is detected
                            graphicOverlay.vibrate();

                            // Signal that an allergy has been detected and break out of the loop
                            allergyDetected = true;
                            break;

                        }

                    }

                    // Break out of the allergy loop if an allergy match has been detected
                    if (allergyDetected) {
                        break;
                    }

                }

                // Print as green text on the overlay if no allergies are detected for the word
                if (!allergyDetected) {
                    // Draw the ingredient as green on the overlay
                    textPaint.setColor(SAFE_TEXT_COLOR);
                    textPaint.setTextSize(8.0f);
                    textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    float left = translateX(currentIngredient.getBoundingBox().left);
                    float bottom = translateY(currentIngredient.getBoundingBox().bottom);
                    canvas.drawText(currentIngredient.getValue(), left, bottom, textPaint);
                }
            }
        }
        // MY ADDED CODE ENDS HERE
    }
}
