package com.nasgrad.adapter

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nasgrad.api.model.Issue
import com.nasgrad.nasGradApp.R
import com.nasgrad.utils.Helper
import com.nasgrad.utils.Helper.Companion.USER_ID_KEY
import com.nasgrad.utils.SharedPreferencesHelper
import kotlinx.android.synthetic.main.issue_list_item.view.*

class IssueAdapter(
    private val context: Context,
    private val issues: List<Issue>,
    var listener: OnItemClickListener
) :
    RecyclerView.Adapter<IssueAdapter.IssueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.issue_list_item, parent, false)
        return IssueViewHolder(view)
    }

    override fun getItemCount(): Int {
        return issues.size
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        holder.setIssue(issues[position])
        val issue = issues[position]
        holder.bindIssue(
            issue.id,
            issue.title,
            issue.issueType,
            issue.description,
            issue.picturePreview,
            listener
        )
    }

    inner class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindIssue(
            itemId: String,
            itemTitle: String?,
            itemType: String?,
            itemDesc: String?,
            itemImage: String?,
            onItemClickListener: OnItemClickListener
        ) {
            itemView.setOnClickListener {
                onItemClickListener.onItemClicked(itemId, itemTitle, itemType, itemDesc, itemImage)
            }
        }

        fun setIssue(issue: Issue) {
            itemView.issueTitle.text = issue.title
            itemView.issueCounter.text =
                if (issue.submittedCount != null) issue.submittedCount.toString() else "0"

            itemView.issueType.text = this@IssueAdapter.context.resources.getString(
                R.string.issue_type,
                Helper.getTypeName(issue.issueType) ?: "nepoznat"
            )

            val ownerId = SharedPreferencesHelper(context).getStringValue(USER_ID_KEY, "0")

            if (issue.ownerId == ownerId) {
                itemView.list_item_container.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimaryLight
                    )
                )
            }

            if (!issue.picturePreview.isNullOrBlank()) {
                itemView.issueImage.setImageBitmap(Helper.decodePicturePreview(issue.picturePreview!!))
            }

            val categories = issue.categories

            if (categories == null) {
                itemView.issueCategory1.visibility = View.INVISIBLE
                itemView.category2.visibility = View.INVISIBLE
                itemView.issueCategory3.visibility = View.INVISIBLE
            } else {
                when {
                    categories.size == 1 -> {
                        val issueCategory1 = Helper.getCategoryForCategoryId(categories[0])
                        itemView.issueCategory1.text = issueCategory1?.name
                        itemView.issueCategory1.setBackgroundColor(
                            Color.parseColor(
                                issueCategory1?.color ?: "#FFE0B2"
                            )
                        )
                        itemView.issueCategory1.visibility = View.VISIBLE
                        itemView.category2.visibility = View.INVISIBLE
                        itemView.issueCategory3.visibility = View.INVISIBLE
                    }
                    categories.size == 2 -> {
                        val issueCategory1 = Helper.getCategoryForCategoryId(categories[0])
                        val issueCategory2 = Helper.getCategoryForCategoryId(categories[1])
                        itemView.issueCategory1.text = issueCategory1?.name
                        itemView.category2.text = issueCategory2?.name
                        itemView.issueCategory1.setBackgroundColor(
                            Color.parseColor(
                                issueCategory1?.color ?: "#FFE0B2"
                            )
                        )
                        itemView.category2.setBackgroundColor(
                            Color.parseColor(
                                issueCategory2?.color ?: "#FFE0B2"
                            )
                        )
                        itemView.issueCategory1.visibility = View.VISIBLE
                        itemView.category2.visibility = View.VISIBLE
                        itemView.issueCategory3.visibility = View.INVISIBLE
                    }
                    categories.size == 3 -> {
                        val issueCategory1 = Helper.getCategoryForCategoryId(categories[0])
                        val issueCategory2 = Helper.getCategoryForCategoryId(categories[1])
                        val issueCategory3 = Helper.getCategoryForCategoryId(categories[2])
                        itemView.issueCategory1.text = issueCategory1?.name
                        itemView.category2.text = issueCategory2?.name
                        itemView.issueCategory3.text = issueCategory3?.name

                        itemView.issueCategory1.setBackgroundColor(
                            Color.parseColor(
                                issueCategory1?.color ?: "#FFE0B2"
                            )
                        )
                        itemView.category2.setBackgroundColor(
                            Color.parseColor(
                                issueCategory2?.color ?: "#FFE0B2"
                            )
                        )
                        itemView.issueCategory3.setBackgroundColor(
                            Color.parseColor(
                                issueCategory3?.color ?: "#FFE0B2"
                            )
                        )
                        itemView.issueCategory1.visibility = View.VISIBLE
                        itemView.category2.visibility = View.VISIBLE
                        itemView.issueCategory3.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}