package edu.iastate.mmorth.ingredientsscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * This class is the Launcher Activity for the Ingredients Scanner app. It displays a warning message for using this app.
 */
public class WarningPageActivity extends AppCompatActivity {

    /**
     * Represents the accept button.
     */
    private Button acceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_page);

        acceptButton = findViewById(R.id.warningAcceptButton);

        // Display the MenuActivity when the accept button is tapped
        acceptButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        // Render the allergy info activity page
                        Intent allergyInfoIntent = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(allergyInfoIntent);
                    }
                }
        );
    }
}
