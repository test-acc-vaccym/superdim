package com.wouterhabets.superdim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActivityMain extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener {

    public static final String PREF_NAME = "prefs";
    public static final String PREF_ENABLED = "enabled";
    public static final String PREF_LEVEL = "level";
    public static final String PREF_SCHEDULING = "scheduling";

    private TextView textViewLevel;
    private SeekBar seekBar;
    private String levelBaseString;

    private View viewTimePickers;
    private Button buttonStart;
    private Button buttonEnd;

    private boolean previousState;
    private boolean previousScheduling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        textViewLevel = (TextView) findViewById(R.id.textViewLevel);
        levelBaseString = getResources().getString(R.string.dim_level);

        SharedPreferences sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        int previousLevel = sharedPrefs.getInt(PREF_LEVEL, 0);
        seekBar = (SeekBar) findViewById(R.id.seekBarDim);
        seekBar.setProgress(previousLevel);
        updateLevel(previousLevel);
        seekBar.setOnSeekBarChangeListener(this);

        previousState = sharedPrefs.getBoolean(PREF_ENABLED, false);
        previousScheduling = sharedPrefs.getBoolean(PREF_SCHEDULING, false);
        changeState(previousState);
        changeSchedule(previousScheduling);

        SwitchCompat masterSwitch = (SwitchCompat) findViewById(R.id.switchMaster);
        masterSwitch.setChecked(previousState);
        masterSwitch.setOnCheckedChangeListener(this);

        viewTimePickers = findViewById(R.id.viewTimePickers);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(this);
        buttonEnd = (Button) findViewById(R.id.buttonEnd);
        buttonStart.setOnClickListener(this);

        SwitchCompat schedulingSwitch = (SwitchCompat) findViewById(R.id.switchSchedule);
        schedulingSwitch.setChecked(previousScheduling);
        schedulingSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void changeState(boolean isChecked) {
        previousState = isChecked;
        if (isChecked) {
            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(PREF_ENABLED, true);
            editor.apply();
            textViewLevel.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            startService(new Intent(this, DimService.class));
        } else {
            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(PREF_ENABLED, false);
            editor.apply();
            textViewLevel.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
            stopService(new Intent(this, DimService.class));
        }
    }

    private void changeSchedule(boolean isChecked) {
        previousScheduling = isChecked;
        if (isChecked) {
            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(PREF_SCHEDULING, true);
            editor.apply();
        } else {
            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(PREF_SCHEDULING, false);
            editor.apply();
        }
    }

    private void applySchedule(boolean enable) {

    }

    private void applySchedule(boolean enable, String timeStart, String timeEnd) {

    }

    private void updateLevel(int level) {
        textViewLevel.setText(String.format(levelBaseString, level));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchMaster:
                changeState(isChecked);
                break;
            case R.id.switchSchedule:
                changeSchedule(isChecked);
                break;
        }
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
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(PREF_LEVEL, seekBar.getProgress());
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_ENABLED)) {
            boolean currentState = sharedPreferences.getBoolean(PREF_ENABLED, false);
            if (currentState != previousState) {
                changeState(currentState);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart:
                //TODO Change start time
                break;
            case R.id.buttonEnd:
                //TODO Change stop time
                break;
        }
    }
}
