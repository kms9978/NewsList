package com.martin.newsletter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.martin.newsletter.databinding.ItemNewsBinding

class NewsAdapter(private val onClick: (String) -> Unit): ListAdapter<NewsModel, NewsAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root) {

        // ViewHolder 클래스 내부에 뷰 바인딩을 위한 private 변수인 binding을 선언.
        // 해당 변수는 뉴스 아이템에 대한 뷰 요소들과 바인딩된 ItemNewsBinding 객체.

        fun bind(item: NewsModel) {
            //NewsModel 객체의 데이터를 뷰에 바인딩하는 역할.
            // 뉴스 제목 설정.
            binding.titleTextViews.text = item.title

            // item을 클릭할 때 onClick 함수를 호출하여 해당 뉴스의 링크를 전달.
            binding.root.setOnClickListener {
                onClick(item.link)
            }

            // Glide를 사용하여 뉴스 이미지를 ImageView에 로드하고 표시.
            Glide.with(binding.thumbnailImageView)
                .load(item.imageUrl)
                .into(binding.thumbnailImageView)
        }
    }

    // onCreateViewHolder 메서드는 ViewHolder 객체를 생성하고 뷰 바인딩을 수행.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // onBindViewHolder 메서드는 ViewHolder를 통해 데이터를 바인딩.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        // diffUtil 객체는 리스트 갱신을 위한 DiffUtil.ItemCallback을 구현한 콜백 객체.
        val diffUtil = object: DiffUtil.ItemCallback<NewsModel>() {
            override fun areItemsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
