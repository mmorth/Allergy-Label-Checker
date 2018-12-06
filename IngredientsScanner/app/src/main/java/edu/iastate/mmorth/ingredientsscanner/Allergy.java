package edu.iastate.mmorth.ingredientsscanner;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * This class represents an Allergy that the user has entered that will be tracked when scanning food labels.
 * This class also represents a Room Entity that will be used to put the Allergy information into the database.
 */
@Entity(tableName = "allergy", indices = {@Index(value = {"allergy_name"},
        unique = true)})
public class Allergy {

    /**
     * The id of the allergy for database use.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The allergy name. This is used to compare against the Text Recognition detected text from the
     * food ingredients label.
     */
    @ColumnInfo(name = "allergy_name")
    private String allergyName;

    /**
     * Constructs an empty Allergy used by Room database query.
     */
    public Allergy() {
    }

    /**
     * Constructs an Allergy with the given allergyName.
     *
     * @param allergyName The name of the Allergy.
     */
    public Allergy(String allergyName) {
        this.allergyName = allergyName;
    }

    /**
     * Returns the id of the Allergy.
     *
     * @return The id of the Allergy.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the Allergy.
     *
     * @param id The id of the Allergy.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the Allergy name.
     *
     * @return The Allergy name.
     */
    public String getAllergyName() {
        return allergyName;
    }

    /**
     * Sets the Allergy name.
     *
     * @param allergyName The new allergy name.
     */
    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    @Override
    public String toString() {
        return allergyName;
    }
}
