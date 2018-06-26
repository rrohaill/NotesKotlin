package rohail.noteskotlin.activities

import android.os.Bundle
import rohail.noteskotlin.R
import rohail.noteskotlin.fragments.PreferencesFragment

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PreferencesFragment.newInstance())
                    .commit()
        }

    }
}
