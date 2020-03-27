package amrabed.android.release.evaluation

import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.help.*

class HelpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help)
        helpContent.webViewClient = WebViewClient()
        helpContent.loadUrl(getString(R.string.helpWebsite))
    }
}