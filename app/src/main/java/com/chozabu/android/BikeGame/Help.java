package com.chozabu.android.BikeGame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Help extends Activity implements OnClickListener {

    private Button emailButton;
    private Button optionsButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        // Get referenc to Email Button
        this.emailButton = (Button) this.findViewById(R.id.EmailButton);
        // this.optionsButton = (Button) this.findViewById(R.id.OptionsButton);

        // Sets the Event Listener onClick
        this.emailButton.setOnClickListener(this);
        // this.optionsButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == this.emailButton) {
            StatStuff.sendMail(this);
        } else if (view == this.optionsButton) {

        }
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pEvent.getAction() != KeyEvent.ACTION_DOWN)
            return super.onKeyDown(pKeyCode, pEvent);
        if (pKeyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainMenuIntent = new Intent(Help.this, MainActivity.class);
            startActivity(mainMenuIntent);
            this.finish();
            //System.exit(0);
            return true;
        }
        return super.onKeyUp(pKeyCode, pEvent);
    }

}