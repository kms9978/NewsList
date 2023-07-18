package com.martin.newsletter

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**  XML 데이터를 파싱하기 위한 데이터 클래스 **/

@Xml(name = "rss")
data class NewsRss(
    @Element(name = "channel")
    val channel: RssChannel
)

@Xml(name = "channel")
data class RssChannel(
    @PropertyElement(name = "title")
    val title: String,                   // 채널의 제목을 나타내는 문자열 필드

    @Element(name = "item")
    val items: List<NewsItem>? = null,   // 채널에 포함된 뉴스 아이템 목록을 나타내는 리스트 필드, 초기값은 null
)

@Xml(name = "item")
data class NewsItem(
    @PropertyElement(name = "title")
    val title: String? = null,           // 뉴스 제목을 나타내는 문자열 필드, 초기값은 null

    @PropertyElement(name = "link")
    val link: String? = null,            // 뉴스 링크를 나타내는 문자열 필드, 초기값은 null
)
