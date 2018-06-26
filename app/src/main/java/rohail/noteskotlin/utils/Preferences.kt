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

package rohail.noteskotlin.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Preferences class
 */
class Preferences private constructor(private val mContext: Context) {
    /**
     * Get instance of app's Shared Preferences
     *
     * @return SharedPreference instance
     */
    val preferences: SharedPreferences

    val isLightTheme: Boolean
        get() = preferences.getBoolean(PREFS_THEME, false)

    val isCzechLanguage: Boolean
        get() = preferences.getBoolean(PREFS_LANGUAGE, false)

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    companion object {

        val PREFS_THEME = "light_theme"
        val PREFS_LANGUAGE = "language"

        private var mInstance: Preferences? = null

        fun getInstance(context: Context): Preferences {
            if (mInstance == null) {
                mInstance = Preferences(context.applicationContext)
            }

            return mInstance as Preferences
        }
    }

}
