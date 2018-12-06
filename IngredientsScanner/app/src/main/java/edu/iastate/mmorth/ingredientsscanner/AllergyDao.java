package edu.iastate.mmorth.ingredientsscanner;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * The Allergy database Room DAO. This class contains available queries for the Allergy database.
 */
@Dao
public interface AllergyDao {

    /**
     * Returns all Allergy objects from the database.
     *
     * @return All Allergy objects from the database.
     */
    @Query("SELECT * FROM allergy")
    List<Allergy> getAll();

    /**
     * Returns the Allergy with the given name from the database.
     *
     * @param allergyName The name of the Allergy to obtain.
     * @return The Allergy object with the given name.
     */
    @Query("SELECT * FROM allergy WHERE allergy_name = :allergyName ")
    Allergy findByAllergyName(String allergyName);

    /**
     * Stores a new Allergy in the database.
     *
     * @param allergy The new Allergy to store in the database.
     */
    @Insert
    void insertAllergy(Allergy allergy);

    /**
     * Deletes an Allergy from the database.
     *
     * @param allergy The Allergy to delete from the database.
     */
    @Delete
    void deleteAllergy(Allergy allergy);
}
