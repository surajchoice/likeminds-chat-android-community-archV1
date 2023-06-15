package com.likeminds.chatmm.utils.membertagging.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.chatmm.databinding.ItemMemberBinding
import com.likeminds.chatmm.utils.membertagging.model.TagViewData

internal class MemberAdapter(
    private val darkMode: Boolean,
    private val memberAdapterClickListener: MemberAdapterClickListener
) : RecyclerView.Adapter<MemberViewHolder>() {

    private val members = ArrayList<TagViewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding, darkMode, memberAdapterClickListener)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    /**
     * Updates the member list in the recyclerview adapter
     */
    @JvmSynthetic
    internal fun setMembers(users: List<TagViewData>) {
        this.members.clear()
        this.members.addAll(users)
        notifyDataSetChanged()
    }

    @JvmSynthetic
    internal fun allMembers(users: List<TagViewData>) {
        this.members.addAll(users)
        notifyDataSetChanged()
    }

    @JvmSynthetic
    internal fun clear() {
        this.members.clear()
        notifyDataSetChanged()
    }

}