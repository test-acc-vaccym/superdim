package com.wouterhabets.superdim;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActivityMain extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private static final String PREF_ENABLED = "enabled";
    private static final String PREF_LEVEL = "level";

    private TextView textViewLevel;
    private SeekBar seekBar;
    private String levelBaseString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        textViewLevel = (TextView) findViewById(R.id.textViewLevel);
        levelBaseString = getResources().getString(R.string.dim_level);

        int previousLevel = getPreferences(Context.MODE_PRIVATE).getInt(PREF_LEVEL, 0);
        seekBar = (SeekBar) findViewById(R.id.seekBarDim);
        seekBar.setProgress(previousLevel);
        updateLevel(previousLevel);
        seekBar.setOnSeekBarChangeListener(this);

        changeState(getPreferences(Context.MODE_PRIVATE).getBoolean(PREF_ENABLED, false));

        SwitchCompat masterSwitch = (SwitchCompat) findViewById(R.id.switchMaster);
        masterSwitch.setOnCheckedChangeListener(this);
    }

    private void changeState(boolean isChecked) {
        if (isChecked) {
            SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
            editor.putBoolean(PREF_ENABLED, true);
            editor.apply();
            textViewLevel.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
        } else {
            SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
            editor.putBoolean(PREF_ENABLED, false);
            editor.apply();
            textViewLevel.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
        }
    }

    private void updateLevel(int level) {
        textViewLevel.setText(String.format(levelBaseString, level));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        changeState(isChecked);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateLevel(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(PREF_LEVEL, seekBar.getProgress());
        editor.apply();
    }
}
