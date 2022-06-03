package com.breadwallet.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.breadwallet.R
import com.breadwallet.databinding.ListItemProfileBinding

class ProfileAdapter(
    private val items: List<ProfileItem>,
    private val onClick: (ProfileOption) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProfileViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item_profile, viewGroup, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ProfileViewHolder, position: Int) {
        viewHolder.bindView(items[position])
        viewHolder.itemView.setOnClickListener { onClick(items[viewHolder.adapterPosition].option) }
    }

    override fun getItemCount() = items.size

    class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemProfileBinding.bind(view)

        fun bindView(item: ProfileItem) {
            binding.ivIcon.setImageResource(item.iconResId)
            binding.tvTitle.text = item.title
        }
    }
}
