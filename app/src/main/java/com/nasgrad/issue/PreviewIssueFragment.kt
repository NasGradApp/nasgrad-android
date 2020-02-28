package com.nasgrad.issue

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nasgrad.ApiClient
import com.nasgrad.api.model.Issue
import com.nasgrad.api.model.IssueRequestBody
import com.nasgrad.api.model.NewItemRequest
import com.nasgrad.api.model.PictureInfo
import com.nasgrad.nasGradApp.R
import com.nasgrad.utils.Helper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.create_issue_bottom_navigation_layout.*
import kotlinx.android.synthetic.main.fragment_preview_issue.*
import timber.log.Timber
import java.util.*

class PreviewIssueFragment : Fragment(), View.OnClickListener {

    private lateinit var disposable: Disposable
    private lateinit var issue: Issue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preview_issue, container, false)
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.screen_title_issue_summary)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvPageIndicator.text = String.format(getString(R.string.create_issue_page_indicator), 4)

        issue = (activity as CreateIssueActivity).issue

        issue_title.text = issue.title
        titlePreview.text = resources.getString(R.string.issue_type, issue.issueType ?: "nema")

        val list = mutableListOf<String>()
        issue.categories?.forEach {
            val name = Helper.getCategoryNameForCategoryId(it)
            name?.let { list.add(name) }
        }

        categoryPreview.text = resources.getString(R.string.issue_categories, list.joinToString())
        addressPreview.text = resources.getString(R.string.issue_address, issue.address)
        descriptionPreview.text = issue.description

        Timber.d("${issue.picturePreview}")

        if (issue.picturePreview != null) imagePreview.setImageBitmap(
            Helper.decodePicturePreview(
                issue.picturePreview!!
            )
        )
        nextScreen.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            nextScreen.id -> {
                val recipient = "sonja.mijatovic@gmail.com"
                val cc = "sonja.mijatovic@gmail.com"
                val subject =
                    Uri.encode(String.format(getString(R.string.email_subject), issue.title))
                val body =
                    Uri.encode(String.format(getString(R.string.email_body), issue.title, issue.id))
                val email =
                    String.format(getString(R.string.email_template), recipient, cc, subject, body)

                // send email
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse(email)
                (activity as CreateIssueActivity).startActivity(emailIntent)
                saveIssue()
            }
        }
    }

    private fun saveIssue() {
        val client = ApiClient.create()

        val request = IssueRequestBody(
            issue.categories,
            issue.description,
            issue.id,
            issue.issueType,
            issue.location,
            issue.ownerId,
            1,
            issue.title
        )
        val pictureInfo = PictureInfo(issue.picturePreview, UUID.randomUUID().toString())
        val newItemRequest = NewItemRequest(request, pictureInfo)

        disposable = client.createNewIssue(newItemRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { t ->
                if (t.isSuccessful) {
                    val response = t.body()
                    Timber.d("Response: $response")
                } else {
                    Timber.e("Error occurred")
                }
            }.subscribe()
    }

    override fun onStop() {
        super.onStop()
        // TODO don't finish() but handle going back to the app after sending the email
        (activity as CreateIssueActivity).finish()
    }
}
