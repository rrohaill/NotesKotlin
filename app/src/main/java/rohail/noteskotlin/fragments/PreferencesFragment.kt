/*
 * Copyright 2016 Phil Shadlyn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rohail.noteskotlin.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment

import rohail.noteskotlin.R
import rohail.noteskotlin.activities.MainActivity
import rohail.noteskotlin.utils.LocaleHelper
import rohail.noteskotlin.utils.Preferences
import java.util.*


/**
 * Fragment to display settings screen
 */
class PreferencesFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

    }

    override fun onResume() {
        super.onResume()
        Preferences.getInstance(activity).preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        Preferences.getInstance(activity).preferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        val data = Intent()
        activity.setResult(Activity.RESULT_OK, data)

        if (key == Preferences.PREFS_THEME) {
            // Theme change, restart all open activities and reload with new theme
            activity.finish()
        } else if (key == Preferences.PREFS_LANGUAGE) {
            // Theme change, restart all open activities and reload with new theme
            if (Preferences.getInstance(activity).isCzechLanguage) {

                LocaleHelper.setLocale(activity, "cs")
                activity.finish()

            }else{
                LocaleHelper.setLocale(activity, "en")
                activity.finish()
            }

            //It is required to recreate the activity to reflect the change in UI.
//            activity.recreate()
        }
    }

    companion object {

        fun newInstance(): PreferencesFragment {
            return PreferencesFragment()
        }
    }
}
