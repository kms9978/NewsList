package com.martin.newsletter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.martin.newsletter.databinding.ActivityMainBinding
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding //타입의 지연 초기화. 액티비티의 레이아웃과 해당 레이아웃 내의 뷰 요소들과의 바인딩
    private lateinit var newsAdapter: NewsAdapter //타입의 지연 초기화. 뉴스 목록을 표시하기 위한 어댑터.

    private val retrofit = Retrofit.Builder()  // 웹 서비스와 통신
        .baseUrl("https://news.google.com/")
        .addConverterFactory( //XML 데이터를 파싱하기 위한 컨버터 팩토리.
            TikXmlConverterFactory.create(
                TikXml.Builder()
                    .exceptionOnUnreadXml(false)
                    .build()
            )
        ).build()

    override fun onCreate(savedInstanceState: Bundle?) { //액티비티가 생성될 때 호출되는 메서드, 액티비티의 초기화 작업을 수행
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) //ActivityMainBinding 클래스의 inflate() 메서드를 호출하여 액티비티의 레이아웃과 바인딩
        setContentView(binding.root) //액티비티의 화면에 바인딩된 레이아웃을 표시, binding.root는 최상위 뷰
        supportActionBar?.hide()

        // 기본적으로 한 줄로 수직으로 배열되도록 설정
        setLayoutManager(isTwoColumns = false)

        // 버튼 클릭 이벤트 처리
        binding.toggleButton.setOnClickListener {
            val isTwoColumns = binding.toggleButton.isChecked
            setLayoutManager(isTwoColumns)
        }

        // 뉴스 어댑터 초기화
        newsAdapter = NewsAdapter { url ->
            startActivity(
                Intent(this, WebViewActivity::class.java).apply {
                    putExtra("url", url)
                }
            )
        }
        val newsService = retrofit.create(NewsService::class.java)

        binding.rcNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }

        // 피드 칩 클릭 이벤트 처리
        binding.feedChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.feedChip.isChecked = true

            newsService.mainFeed().submitList()
        }

        // 정치 칩 클릭 이벤트 처리
        binding.politicsChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.politicsChip.isChecked = true

            newsService.politicsNews().submitList()
        }

        // 경제 칩 클릭 이벤트 처리
        binding.economyChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.economyChip.isChecked = true

            newsService.economyNews().submitList()
        }

        // 사회 칩 클릭 이벤트 처리
        binding.societyChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.societyChip.isChecked = true

            newsService.societyNews().submitList()
        }

        // IT 칩 클릭 이벤트 처리
        binding.itChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.itChip.isChecked = true

            newsService.itNews().submitList()
        }

        // 스포츠 칩 클릭 이벤트 처리
        binding.sportChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.sportChip.isChecked = true

            newsService.sportNews().submitList()
        }

        // 검색 버튼 클릭 이벤트 처리
        binding.edtSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 검색 버튼이 눌렸을 때의 동작을 처리.

                binding.chipGroup.clearCheck() // 칩 그룹에서 선택된 칩을 해제.

                binding.edtSearch.clearFocus() // 검색 입력란에서 포커스를 제거.
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0) // 키보드 숨기기.

                // 입력된 검색어를 사용해 뉴스 검색을 수행하고 목록을 업데이트.
                newsService.search(binding.edtSearch.text.toString()).submitList()

                return@setOnEditorActionListener true // 이벤트 처리 완료를 나타내는 true를 반환.
            }
            return@setOnEditorActionListener false // 이벤트 처리가 필요하지 않을 때는 false를 반환.
        }

        binding.feedChip.isChecked = true
        newsService.mainFeed().submitList()
    }

    private fun setLayoutManager(isTwoColumns: Boolean) {
        val layoutManager = if (isTwoColumns) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }
        binding.rcNews.layoutManager = layoutManager
    }



    // Call 객체의 확장 함수 submitList() 정의
    private fun Call<NewsRss>.submitList() {
        enqueue(object : Callback<NewsRss> {
            override fun onResponse(call: Call<NewsRss>, response: Response<NewsRss>) {
                val list = response.body()?.channel?.items.orEmpty().transform()
                newsAdapter.submitList(list)

                binding.notExist.isVisible = list.isEmpty()

                list.forEachIndexed { index, news ->
                    Thread {
                        try {
                            // 뉴스 링크로부터 og:image 메타 태그 정보를 가져와 imageUrl 설정
                            val jsoup = Jsoup.connect(news.link).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get()
                            val elements = jsoup.select("meta[property^=og:]")
                            val ogImageNode = elements.find { node ->
                                node.attr("property") == "og:image"
                            }

                            news.imageUrl = ogImageNode?.attr("content")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        runOnUiThread {
                            newsAdapter.notifyItemChanged(index)
                        }
                    }.start()
                }

            }

            override fun onFailure(call: Call<NewsRss>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}