package com.nasgrad

import android.content.Intent

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.nasgrad.MainActivity.Companion.ITEM_ID
import com.nasgrad.api.model.Issue
import com.nasgrad.nasGradApp.R
import com.nasgrad.utils.Helper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class DetailActivity : AppCompatActivity(), OnClickListener {

    private lateinit var displayedIssue: Issue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(detailActivityToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        detailActivityToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))

        val itemItemId = intent.getStringExtra(ITEM_ID)
        showDetailIssue(itemItemId)

        reportIssue.setOnClickListener(this)
        share_btn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            reportIssue.id -> openEmailClint()
            share_btn.id -> shareTwitter(resources.getString(R.string.tweetText, displayedIssue.title, displayedIssue.id))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun shareTwitter(message: String) {
        val tweetIntent = Intent(Intent.ACTION_SEND)
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message)
        tweetIntent.type = resources.getString(R.string.tweet_text_type)

        val resolvedInfoList = packageManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY)

        var resolved = false
        for (resolveInfo in resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith(resources.getString(R.string.twitter_app_package_name))) {
                tweetIntent.setClassName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                resolved = true
                break
            }
        }
        if (resolved) {
            startActivity(tweetIntent)
        } else {
            val intent = Intent()
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(resources.getString(R.string.send_tweet_base_url) + urlEncode(message))
            startActivity(intent)
            Toast.makeText(this, resources.getString(R.string.twitter_app_not_found), Toast.LENGTH_LONG).show()
        }
    }

    private fun urlEncode(s: String): String {
        return try {
            URLEncoder.encode(s, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Timber.e("UTF-8 should always be supported $e")
            ""
        }
    }

    private var disposable: Disposable? = null

    private val client by lazy {
        ApiClient.create()
    }

    private fun openEmailClint() {
        val recipient = resources.getString(R.string.email_address)
        val cc = resources.getString(R.string.nas_grad_email_address)
        val subject = Uri.encode(String.format(getString(R.string.email_subject), displayedIssue.title))
        val body = Uri.encode(String.format(getString(R.string.email_body), displayedIssue.title, displayedIssue.id))
        val email = String.format(getString(R.string.email_template), recipient, cc, subject, body)

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse(email)
        this.startActivity(emailIntent)
    }

    private fun showDetailIssue(itemId: String) {
        disposable = client.getIssueItemById(itemId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                if (result != null) {
                    displayedIssue = result
                    setUiDetailsScreen(result)
                }
            }
    }

    private fun setUiDetailsScreen(issue: Issue?) {
        titleDetails.text = issue?.title
        if (issue?.picturePreview != null) {
            issuePicture.setImageBitmap(Helper.decodePicturePreview(issue.picturePreview!!))
        }
        issueDescription.text = issue?.description
        typeFromPredefinedList.text = resources.getString(R.string.issue_type, Helper.getTypeName(issue?.issueType) ?: "nepoznat")
        issueAddress.text = resources.getString(R.string.issue_address, issue?.address ?: "nepoznata")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
