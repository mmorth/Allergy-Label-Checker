package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

// Note: The navigation drawer code was created with help from: https://developer.android.com/training/implementing-navigation/nav-drawer

/**
 * This activity manages menu (navigation drawer) for the Ingredients Scanner app. The user has the
 * ability to navigate between the Scan Label, Allergy List, About App, and Contact windows.
 */
public class MenuActivity extends AppCompatActivity {

    /**
     * Creates a reference to the drawer layout.
     */
    private DrawerLayout drawerLayout;

    /**
     * Stores the currently displayed fragment.
     */
    String currentFragment;

    /**
     * Keys for saved instance state
     */
    private static final String KEY_CURRENT_FRAGMENT = "edu.iastate.mmorth.MenuActivity.CURRENTFRAGMENT";

    /**
     * This method inflates the layout and manages configuration changes for the currently displayed fragment.
     * This method also initializes the navigation drawer and Toolbar.
     *
     * @param savedInstanceState Contains any configuration changes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Link to the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Setup click listeners for drawer layout
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        switchFragments(menuItem.getTitle().toString());

                        return true;
                    }
                });

        currentFragment = "Allergy List";

        // Resume fragment during configuration changes
        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getString(KEY_CURRENT_FRAGMENT);
        }

        switchFragments(currentFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        switchFragments(currentFragment);
    }

    // ===================================== Intent Method =========================================

    /**
     * Create an explicit intent that launches the MenuActivity. This method is intended
     * to be called by other classes.
     *
     * @param packageContext The context used for this method.
     * @return The intent used to switch to this activity.
     */
    public static Intent newIntent(Context packageContext) {
        Intent menuIntent = new Intent(packageContext, MenuActivity.class);
        return menuIntent;
    }

    // =================================== Helper Methods  ====================================

    /**
     * Switches the displayed fragment. This method is called when a new fragment is selected from the
     * navigation drawer or if there is a configuration change.
     *
     * @param newFragment The name of the Fragment to display
     */
    public void switchFragments(String newFragment) {
        // Update the displayed right fragment based on the value of fragmentToLoad
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        Fragment updateFragment = new Fragment();

        if (newFragment.equals("Scan Label")) { // Display Camera Text Detection Activity
            Intent scanLabelIntent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
            startActivity(scanLabelIntent);
        } else if (newFragment.equals("Allergy List")) { // Display AllergyListFragment
            updateFragment = new AllergyListFragment();
            currentFragment = "Allergy List";
        } else if (newFragment.equals("About App")) { // Display AboutAppFragment
            updateFragment = new AboutAppFragment();
            currentFragment = "About App";
        } else if (newFragment.equals("Contact")) { // Open email app with given information

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:mmorth@iastate.edu"));
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.contact_email_subject);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        transaction.replace(R.id.content_frame, updateFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Store the current displayed fragment in the savedInstanceState variable
        outState.putString(KEY_CURRENT_FRAGMENT, currentFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handles options selected from the navigation drawer
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
