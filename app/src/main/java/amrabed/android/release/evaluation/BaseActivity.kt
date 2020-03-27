package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.locale.LocaleManager
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        LocaleManager.setLocale(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this)
    }

    override fun onResume() {
        LocaleManager.setLocale(this)
        super.onResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}