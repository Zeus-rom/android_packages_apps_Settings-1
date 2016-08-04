/*
 * Copyright (C) 2015 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.zeus;

import java.util.ArrayList;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.zeus.SeekBarPreference;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.ListPreference;
import android.provider.Settings;

public class PulseSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = PulseSettings.class.getSimpleName();
    private static final String CUSTOM_DIMEN = "pulse_custom_dimen";
    private static final String CUSTOM_DIV = "pulse_custom_div";
    private static final String PULSE_BLOCK = "pulse_filled_block_size";
    private static final String EMPTY_BLOCK = "pulse_empty_block_size";
    private static final String FUDGE_FACOR = "pulse_custom_fudge_factor";
    private static final int RENDER_STYLE_FADING_BARS = 0;
    private static final int RENDER_STYLE_SOLID_LINES = 1;
    private static final String SOLID_FUDGE = "pulse_solid_fudge_factor";
    private static final String SOLID_LAVAMP_SPEED = "lavamp_solid_speed";
    private static final String FADING_LAVAMP_SPEED = "fling_pulse_lavalamp_speed";

    SwitchPreference mShowPulse;
    ListPreference mRenderMode;
    SwitchPreference mLavaLampEnabled;
    ColorPickerPreference mPulseColor;
    SeekBarPreference mCustomDimen;
    SeekBarPreference mCustomDiv;
    SeekBarPreference mFilled;
    SeekBarPreference mEmpty;
    SeekBarPreference mFudge;
    SeekBarPreference mSolidFudge;
    SeekBarPreference mSolidSpeed;
    SeekBarPreference mFadingSpeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pulse_settings);

        ActionBar bar = getActivity().getActionBar();
        if (bar != null) {
            bar.setTitle(R.string.pulse_settings);
        }

        mShowPulse = (SwitchPreference) findPreference("eos_fling_show_pulse");
        mShowPulse.setChecked(Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.FLING_PULSE_ENABLED, 0) == 1);
        mShowPulse.setOnPreferenceChangeListener(this);

        int renderMode = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.PULSE_RENDER_STYLE_URI, RENDER_STYLE_SOLID_LINES, UserHandle.USER_CURRENT);
        mRenderMode = (ListPreference) findPreference("pulse_render_mode");
        mRenderMode.setValue(String.valueOf(renderMode));
        mRenderMode.setOnPreferenceChangeListener(this);

        int pulseColor = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.FLING_PULSE_COLOR, Color.WHITE, UserHandle.USER_CURRENT);
        mPulseColor = (ColorPickerPreference) findPreference("eos_fling_pulse_color");
        mPulseColor.setNewPreviewColor(pulseColor);
        mPulseColor.setOnPreferenceChangeListener(this);

        mLavaLampEnabled = (SwitchPreference) findPreference("eos_fling_lavalamp");
        mLavaLampEnabled.setChecked(Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.FLING_PULSE_LAVALAMP_ENABLED, 1) == 1);
        mLavaLampEnabled.setOnPreferenceChangeListener(this);

        mCustomDimen = (SeekBarPreference) findPreference(CUSTOM_DIMEN);
        int customdimen = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.PULSE_CUSTOM_DIMEN, 14,
                UserHandle.USER_CURRENT);
        mCustomDimen.setValue(customdimen / 1);
        mCustomDimen.setOnPreferenceChangeListener(this);

        mCustomDiv = (SeekBarPreference) findPreference(CUSTOM_DIV);
        int customdiv = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.PULSE_CUSTOM_DIV, 2,
                    UserHandle.USER_CURRENT);
        mCustomDiv.setValue(customdiv / 1);
        mCustomDiv.setOnPreferenceChangeListener(this);

        mFilled = (SeekBarPreference) findPreference(PULSE_BLOCK);
        int filled = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.PULSE_FILLED_BLOCK_SIZE, 0,
                    UserHandle.USER_CURRENT);
        mFilled.setValue(filled / 1);
        mFilled.setOnPreferenceChangeListener(this);

        mEmpty = (SeekBarPreference) findPreference(EMPTY_BLOCK);
        int empty = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.PULSE_EMPTY_BLOCK_SIZE, 0,
                    UserHandle.USER_CURRENT);
        mEmpty.setValue(empty / 1);
        mEmpty.setOnPreferenceChangeListener(this);

        mFudge = (SeekBarPreference) findPreference(FUDGE_FACOR);
        int fudge = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.PULSE_CUSTOM_FUDGE_FACTOR, 0,
                    UserHandle.USER_CURRENT);
        mFudge.setValue(fudge / 1);
        mFudge.setOnPreferenceChangeListener(this);

        mSolidFudge = (SeekBarPreference) findPreference(SOLID_FUDGE);
        int solidfudge = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.PULSE_SOLID_FUDGE_FACTOR, 0,
                    UserHandle.USER_CURRENT);
        mSolidFudge.setValue(solidfudge / 1);
        mSolidFudge.setOnPreferenceChangeListener(this);

        mSolidSpeed =
                    (SeekBarPreference) findPreference(SOLID_LAVAMP_SPEED);
        int speed = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.LAVAMP_SOLID_SPEED, 10000);
        mSolidSpeed.setValue(speed / 1);
        mSolidSpeed.setOnPreferenceChangeListener(this);

        mFadingSpeed =
                    (SeekBarPreference) findPreference(FADING_LAVAMP_SPEED);
        int fspeed = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.FLING_PULSE_LAVALAMP_SPEED, 10000);
        mFadingSpeed.setValue(fspeed / 1);
        mFadingSpeed.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver mResolver = getActivity().getContentResolver();
        if (preference.equals(mRenderMode)) {
            int mode = Integer.valueOf((String) newValue);
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.PULSE_RENDER_STYLE_URI, mode, UserHandle.USER_CURRENT);
            PreferenceCategory fadingBarsCat = (PreferenceCategory)findPreference("pulse_1");
            fadingBarsCat.setEnabled(mode == RENDER_STYLE_FADING_BARS);
            return true;
        } else if (preference.equals(mShowPulse)) {
            boolean enabled = ((Boolean) newValue).booleanValue();
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.FLING_PULSE_ENABLED, enabled ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mPulseColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.FLING_PULSE_COLOR, color, UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mLavaLampEnabled)) {
            boolean enabled = ((Boolean) newValue).booleanValue();
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.FLING_PULSE_LAVALAMP_ENABLED, enabled ? 1 : 0,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mCustomDimen) {
            int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.PULSE_CUSTOM_DIMEN, val * 1);
            return true;
        }  else if (preference == mCustomDiv) {
            int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.PULSE_CUSTOM_DIV, val * 1);
            return true;
        } else if (preference == mFilled) {
            int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.PULSE_FILLED_BLOCK_SIZE, val * 1);
            return true;
        }  else if (preference == mEmpty) {
            int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.PULSE_EMPTY_BLOCK_SIZE, val * 1);
            return true;
        } else if (preference == mFudge) {
            int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.PULSE_CUSTOM_FUDGE_FACTOR, val * 1);
           return true;

    }  else if (preference == mSolidFudge) {
                int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.PULSE_SOLID_FUDGE_FACTOR, val * 1);
                return true;
    } else if (preference == mSolidSpeed) {
                int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.LAVAMP_SOLID_SPEED, val * 1);
                return true;
        } else if (preference == mFadingSpeed) {
                int val = (Integer) newValue;
                Settings.Secure.putInt(mResolver,
                        Settings.Secure.FLING_PULSE_LAVALAMP_SPEED, val * 1);
                return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
