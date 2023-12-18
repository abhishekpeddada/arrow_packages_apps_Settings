/*
 * Copyright (C) 2020 Project-Awaken
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
package com.android.settings.arrow;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.arrow.support.preferences.SecureSettingSwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SearchIndexable
public class LockScreen extends SettingsPreferenceFragment 
            implements Preference.OnPreferenceChangeListener {

    private static final String LOCKSCREEN_DOUBLE_LINE_CLOCK = "lockscreen_double_line_clock_switch";
    private static final String CLOCK_LS = "clock_ls";
    private static final String CLOCK_PREVIEW= "lockscreen_preview";
    
    private SecureSettingSwitchPreference mDoubleLineClock, mCustomClock;
    private Preference  mPreviewClock;
    
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.arrow_misc);
        
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();

        mDoubleLineClock = (SecureSettingSwitchPreference ) findPreference(LOCKSCREEN_DOUBLE_LINE_CLOCK);
        mDoubleLineClock.setChecked((Settings.Secure.getInt(getContentResolver(),
             Settings.Secure.LOCKSCREEN_USE_DOUBLE_LINE_CLOCK, 1) != 0));
        mDoubleLineClock.setOnPreferenceChangeListener(this);
        
        mCustomClock = (SecureSettingSwitchPreference ) findPreference(CLOCK_LS);
        mCustomClock.setChecked((Settings.Secure.getInt(getContentResolver(),
             Settings.Secure.CLOCK_LS, 1) != 0));
        mCustomClock.setOnPreferenceChangeListener(this);
        if (!mCustomClock.isChecked()) {
        	mDoubleLineClock.setEnabled(true);
        } else {
        	mDoubleLineClock.setEnabled(false);
        }
        mPreviewClock = (Preference ) findPreference(CLOCK_PREVIEW);
        if (!mCustomClock.isChecked()) {
            mPreviewClock.setEnabled(false);
            getPreferenceScreen().removePreference(mPreviewClock);
        } else {
        	mPreviewClock.setEnabled(true);
            getPreferenceScreen().addPreference(mPreviewClock);
        }
    }

    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
       if (preference == mDoubleLineClock) {
          boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.LOCKSCREEN_USE_DOUBLE_LINE_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mCustomClock) {
          boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.CLOCK_LS, value ? 1 : 0);
            if (mDoubleLineClock.isChecked()) {
            	Settings.Secure.putInt(getActivity().getContentResolver(),
                        Settings.Secure.LOCKSCREEN_USE_DOUBLE_LINE_CLOCK, 0);
                mDoubleLineClock.setChecked(false);
            }
        	if (!mCustomClock.isChecked()) {
            	mDoubleLineClock.setEnabled(false);
                mPreviewClock.setEnabled(true);
                getPreferenceScreen().addPreference(mPreviewClock);
            } else {
            	mDoubleLineClock.setEnabled(true);
                mPreviewClock.setEnabled(false);
                getPreferenceScreen().removePreference(mPreviewClock);
            }
            return true;
        }
        return false;
    }  

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARROW;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.arrow_misc;
                    return Arrays.asList(sir);
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
            };
}
