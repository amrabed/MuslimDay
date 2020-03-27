package amrabed.android.release.evaluation

import android.os.Bundle
import android.os.PersistableBundle

class HelpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.help)
    }
}