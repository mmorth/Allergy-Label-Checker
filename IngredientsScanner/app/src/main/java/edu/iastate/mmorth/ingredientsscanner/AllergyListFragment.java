package edu.iastate.mmorth.ingredientsscanner;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass that allows the user to create, list, and delete allergies from
 * the ListView and database. The Allergies managed in this class are used to compared against the
 * ingredients found on scanned food labels.
 */
public class AllergyListFragment extends Fragment {

    /**
     * Represents the addAllergyButton.
     */
    private Button addAllergyButton;

    /**
     * Represents the new allergy name that will be added to the ListView and database.
     */
    private EditText allergyName;

    /**
     * Represents the allergyListView that contains a list of allergies stored in the database.
     */
    private ListView allergyListView;

    /**
     * Represents the database handler for working with SQLite.
     */
    private AppDatabase database;

    /**
     * Stores the context of this activity.
     */
    private Context context;

    /**
     * Required empty public constructor.
     */
    public AllergyListFragment() {
        // Required empty public constructor
    }

    /**
     * This method will return the inflated layout. It will also set the context for this fragment and
     * create a reference to the Allergy database. It also sets up a button event listener for the Add
     * allergy button, which will add the entered Allergy from the allergyName EditText to the database if
     * it is not empty. It also sets up a long touch event listener for deleting the selected allergy from
     * the database. If an Allergy is added or removed from the database, the allergyListView will be
     * updated with the new list of allergies. The database interactions are executed in separate threads.
     *
     * @param inflater           Used to inflate the layout.
     * @param container          Used as parameter to inflate the layout.
     * @param savedInstanceState Stores any configuration changes.
     * @return The inflated view layout.
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allergy_list, container, false);

        // Create reference to fragment's context
        context = this.getContext();

        // Create a reference to the Allergy database
        database = AppDatabase.getAppDatabase(context);

        // Initialize the activity components
        addAllergyButton = view.findViewById(R.id.createAllergyButton);
        allergyName = view.findViewById(R.id.allergyInput);
        allergyListView = view.findViewById(R.id.allergyList);

        // Create a new allergyListView item and add item to database when the user taps the add button.
        addAllergyButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (allergyName.getText() != null && allergyName.getText().toString().length() != 0) {
                            // Create and store the allergy in the database
                            String allergyTitle = allergyName.getText().toString();

                            Allergy allergy = new Allergy(allergyTitle);
                            allergyName.setText("");

                            // Create new Allergy in database in separate thread
                            class InsertAllergyRunnable implements Runnable {
                                Allergy allergy;

                                InsertAllergyRunnable(Allergy allergy) {
                                    this.allergy = allergy;
                                }

                                @Override
                                public void run() {
                                    database.allergyDao().insertAllergy(allergy);

                                    List<Allergy> allergies = database.allergyDao().getAll();

                                    // Display the new allergies on screen
                                    final ArrayAdapter<Allergy> allergyAdapter = new ArrayAdapter<Allergy>(getActivity(), android.R.layout.simple_list_item_1, allergies);
                                    allergyListView.post(new Runnable() {
                                        public void run() {
                                            allergyListView.setAdapter(allergyAdapter);
                                        }
                                    });
                                }
                            }
                            Thread insertAllergyThread = new Thread(new InsertAllergyRunnable(allergy));
                            insertAllergyThread.start();

                        }
                    }
                }
        );

        // Create long click listener to provide delete functionality for Allergies in allergyListView
        allergyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                View parent = (View) view.getParent();
                TextView taskTextView = (TextView) parent.findViewById(R.id.allergyName);

                // Delete allergy from database in separate thread
                class DeleteAllergyRunnable implements Runnable {
                    int position;

                    DeleteAllergyRunnable(int position) {
                        this.position = position;
                    }

                    @Override
                    public void run() {
                        List<Allergy> allergies = database.allergyDao().getAll();

                        String allergy = allergies.get(position).getAllergyName();

                        Allergy allergyObj = database.allergyDao().findByAllergyName(allergy);
                        database.allergyDao().deleteAllergy(allergyObj);

                        allergies = database.allergyDao().getAll();

                        // Display the new allergies on screen
                        final ArrayAdapter<Allergy> allergyAdapter = new ArrayAdapter<Allergy>(getActivity(), android.R.layout.simple_list_item_1, allergies);
                        allergyListView.post(new Runnable() {
                            public void run() {
                                allergyListView.setAdapter(allergyAdapter);
                            }
                        });
                    }
                }
                Thread deleteAllergyThread = new Thread(new DeleteAllergyRunnable(position));
                deleteAllergyThread.start();

                return true;
            }
        });
        return view;
    }

    /**
     * This method calls its superclass onStart method and pulls all the existing Allergies from the
     * database in a separate thread. The returned allergies will be stored in the allergies List and
     * will be displayed in the allergyListView.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Pull existing Allergies from dataabse in separate thread
        class PullExistingAllergiesRunnable implements Runnable {
            @Override
            public void run() {
                List<Allergy> allergies = database.allergyDao().getAll();

                // Display all previously stored allergies
                final ArrayAdapter<Allergy> allergyAdapter = new ArrayAdapter<Allergy>(getActivity(), android.R.layout.simple_list_item_1, allergies);
                allergyListView.post(new Runnable() {
                    public void run() {
                        allergyListView.setAdapter(allergyAdapter);
                    }
                });
            }
        }
        Thread pullExistingAllergiesThread = new Thread(new PullExistingAllergiesRunnable());
        pullExistingAllergiesThread.start();
    }

    /**
     * This method calls its superclass onDestory method and closes the SQLite database.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Runnable to close the database
        class SQLiteCloseDatabaseRunnable implements Runnable {
            @Override
            public void run() {
                // Store Scoreboard in database
                database.close();
            }
        }
        // Close SQLite database
        Thread sqliteCloseDatabase = new Thread(new SQLiteCloseDatabaseRunnable());
        sqliteCloseDatabase.start();
    }
}
