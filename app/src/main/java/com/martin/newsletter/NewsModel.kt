package com.martin.newsletter

data class NewsModel(
    val title: String,                // 뉴스 제목 문자열 필드
    val link: String,                 // 뉴스 링크 문자열 필드
    var imageUrl: String? = null      // 뉴스 이미지 URL 문자열 필드, 초기값 = null
)

fun List<NewsItem>.transform() : List<NewsModel> {
    // List<NewsItem>을 List<NewsModel>로 변환하는 확장 함수.

    return this.map {
        // List<NewsItem>의 각 요소에 대해 NewsModel 객체로 변환.

        NewsModel(
            title = it.title ?: "",   // NewsItem의 title 필드를 NewsModel의 title 필드로 설정.
            link = it.link ?: "",     // NewsItem의 link 필드를 NewsModel의 link 필드로 설정.
            imageUrl = null           // imageUrl의 초기값으로 null을 설정.
        )
    }
}
