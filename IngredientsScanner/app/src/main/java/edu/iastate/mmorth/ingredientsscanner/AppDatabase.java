package edu.iastate.mmorth.ingredientsscanner;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * This class represents the Allergy database.
 */
@Database(entities = {Allergy.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Refers to instance of Allergy database.
     */
    private static AppDatabase INSTANCE;

    /**
     * Creates reference to the Allergy DAO.
     *
     * @return The Allergy DAO.
     */
    public abstract AllergyDao allergyDao();

    /**
     * Returns the Allergy app database.
     *
     * @param context The context of the database.
     * @return The Allergy app database.
     */
    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    /**
     * Destroys the instance of the Allergy app database.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
