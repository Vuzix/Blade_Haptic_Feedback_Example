package com.vuzix.blade.devkit.haptics_sample;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.vuzix.hud.actionmenu.ActionMenuActivity;
import com.vuzix.hud.resources.Utils;
import com.vuzix.system.resources.haptics.Haptics;

/**
 * HapticsSampleActivity extending Vuzix Action Menu Activity to utilize the available features
 * for the Vuzix Blade
 *
 */
public class HapticsSampleActivity extends ActionMenuActivity{

    private TextView mainText;

    private Vibrator mLeftVibe;
    private Vibrator mRightVibe;
    private Vibrator mBothVibe;

    private MediaPlayer tone;

    /** Setup your Vuzix Haptics class variable */
    private Haptics hapticsManager = new Haptics(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.haptics_sample_activity);
    }

    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);
        /** Setup individual controls for the left and right feedback motors */
        mLeftVibe = hapticsManager.getLeftVibrator();
        mRightVibe = hapticsManager.getRightVibrator();
        /** Setup standard control for both sides together */
        mBothVibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        /** Setup MediaPlayer for devices without vibrators */
        tone = MediaPlayer.create(HapticsSampleActivity.this, R.raw.tone);

        /** If running on a device known to have vibrators and vibrators are correctly instantiated
         *  expose the vibrator interaction. Else, show the action menu with instruciton to exit.
         */
        if((Utils.getModelNumber() == Utils.DEVICE_MODEL_BLADE) && (mLeftVibe != null) && (mRightVibe != null)) {
            getMenuInflater().inflate(R.menu.vibration_menu, menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.no_vibration, menu);
        }
        mainText = (TextView) findViewById(R.id.mainTextView);

        return true;
    }

    @Override
    protected boolean alwaysShowActionMenu() {
        return true;
    }

    /**
     * Action Menu Click events
     */

    /** Displays how to vibrate the left haptic only */
    public void vibLeft(MenuItem item){
        /** The isVibratorEnabled method determines if vibrations are turned off system-wide */
        if(hapticsManager.isVibratorEnabled(getContentResolver()))
        {
            mainText.setText("Sent Left");
            mLeftVibe.vibrate(250);
        }
        else
        {
            mainText.setText("Enable Vibrations!!");
        }

    }

    /** Displays how to vibrate both haptics simultaneously */
    public void vibBoth(MenuItem item){
        if((Utils.getModelNumber() == Utils.DEVICE_MODEL_BLADE) && (hapticsManager.isVibratorEnabled(getContentResolver())))
        {
            /** Start without a delay
            * Each element then alternates between vibrate, sleep, vibrate, sleep...
            */
            long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};

            mainText.setText("Sent Both");
            /**
             * Use your designed pattern, repeat as many times as desired
             * NOTE: Values above within bounds of the pattern array will repeat
             * indefinitely starting at the index specified.
             */
            mBothVibe.vibrate(pattern, -1);
        }
        else
        {
            /**
             * Running on a device not equipped with vibrators. Sending audio instead.
             */
            mainText.setText("Vibrator not available, sending audio to both speakers");
            tone.setVolume(.25f, .25f);
            tone.start();
        }

    }

    /** Displays how to vibrate the right haptic only */
    public void vibRight(MenuItem item){
        if(hapticsManager.isVibratorEnabled(getContentResolver()))
        {
            mainText.setText("Sent Right");
            mRightVibe.vibrate(250);
        }
        else
        {
            mainText.setText("Enable Vibrations!!");
        }

    }

}
