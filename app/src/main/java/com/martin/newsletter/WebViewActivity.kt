package com.martin.newsletter

import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.martin.newsletter.databinding.ActivityWebviewBinding

/**웹 페이지를 표시하는 WebView를 사용하는 액티비티**/

class WebViewActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val url = intent.getStringExtra("url")

        // WebView를 초기화.
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true

        if (url.isNullOrEmpty()) {
            // 전달된 URL이 없을 경우 에러 메시지를 표시하고 액티비티 종료.
            Toast.makeText(this, "잘못된 URL입니다.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // 유효한 URL이 있을 경우 해당 URL을 WebView에 로드.
            binding.webView.loadUrl(url)
        }
    }
}
