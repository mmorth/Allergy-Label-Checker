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

package edu.iastate.mmorth.ingredientsscanner.ui.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;

import edu.iastate.mmorth.ingredientsscanner.OcrCaptureActivity;
import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;

/**
 * A view which renders a series of custom graphics to be overlaid on top of an associated preview
 * (i.e., the camera preview).  The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.<p>
 * <p>
 * Supports scaling and mirroring of the graphics relative the camera's preview properties.  The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.<p>
 * <p>
 * Associated {@link Graphic} items should use the following methods to convert to view coordinates
 * for the graphics that are drawn:
 * <ol>
 * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of the
 * supplied value from the preview scale to the view scale.</li>
 * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the coordinate
 * from the preview's coordinate system to the view coordinate system.</li>
 * </ol>
 */
public class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {
    /**
     * Object used to synchronize access to data.
     */
    private final Object lock = new Object();

    /**
     * Stores the width of the preview.
     */
    private int previewWidth;

    /**
     * Stores the width scale factor.
     */
    private float widthScaleFactor = 1.0f;

    /**
     * Stores the preview height.
     */
    private int previewHeight;

    /**
     * Stores the height scale factor.
     */
    private float heightScaleFactor = 1.0f;

    /**
     * Stores the whether the front or back facing camera is currently being used.
     */
    private int facing = CameraSource.CAMERA_FACING_BACK;

    /**
     * Stores the graphics to display.
     */
    private Set<T> graphics = new HashSet<>();

    /**
     * Stores a reference to the Vibrator object.
     */
    private Vibrator v;

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay.  Subclass
     * this and implement the {@link Graphic#draw(Canvas)} method to define the
     * graphics element.  Add instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
     */
    public static abstract class Graphic {
        /**
         * Stores a reference to the GraphicOverlay object used.
         */
        private GraphicOverlay mOverlay;

        /**
         * A Graphic constructor that sets the GraphicOverlay.
         * @param overlay The GraphicOverlay to set.
         */
        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         * <ol>
         * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of
         * the supplied value from the preview scale to the view scale.</li>
         * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.</li>
         * </ol>
         *
         * @param canvas drawing canvas
         */
        public abstract void draw(Canvas canvas);

        /**
         * Returns true if the supplied coordinates are within this graphic.
         */
        public abstract boolean contains(float x, float y);

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.widthScaleFactor;
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        public float scaleY(float vertical) {
            return vertical * mOverlay.heightScaleFactor;
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateX(float x) {
            if (mOverlay.facing == CameraSource.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateY(float y) {
            return scaleY(y);
        }

        /**
         * Returns a RectF in which the left and right parameters of the provided Rect are adjusted
         * by translateX, and the top and bottom are adjusted by translateY.
         */
        public RectF translateRect(RectF inputRect) {
            RectF returnRect = new RectF();

            returnRect.left = translateX(inputRect.left);
            returnRect.top = translateY(inputRect.top);
            returnRect.right = translateX(inputRect.right);
            returnRect.bottom = translateY(inputRect.bottom);

            return returnRect;
        }

        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize the vibration
        v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Removes all graphics from the overlay.
     */
    public void clear() {
        synchronized (lock) {
            graphics.clear();
        }
        postInvalidate();
    }

    /**
     * Adds a graphic to the overlay.
     */
    public void add(T graphic) {
        synchronized (lock) {
            graphics.add(graphic);
        }
        postInvalidate();
    }

    /**
     * Removes a graphic from the overlay.
     */
    public void remove(T graphic) {
        synchronized (lock) {
            graphics.remove(graphic);
        }
        postInvalidate();
    }

    /**
     * Returns the first graphic, if any, that exists at the provided absolute screen coordinates.
     * These coordinates will be offset by the relative screen position of this view.
     *
     * @return First graphic containing the point, or null if no text is detected.
     */
    public T getGraphicAtLocation(float rawX, float rawY) {
        synchronized (lock) {
            // Get the position of this View so the raw location can be offset relative to the view.
            int[] location = new int[2];
            this.getLocationOnScreen(location);
            for (T graphic : graphics) {
                if (graphic.contains(rawX - location[0], rawY - location[1])) {
                    return graphic;
                }
            }
            return null;
        }
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (lock) {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            this.facing = facing;
        }
        postInvalidate();
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (lock) {
            if ((previewWidth != 0) && (previewHeight != 0)) {
                widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
                heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
            }

            for (Graphic graphic : graphics) {
                graphic.draw(canvas);
            }
        }
    }

    /**
     * Called when an allergy is detected from the OcrGraphic class. This will vibrate the phone.
     */
    public void vibrate() {
        v.vibrate(100);
    }
}
