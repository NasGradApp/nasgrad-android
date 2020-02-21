package com.nasgrad.issue

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.nasgrad.api.model.Issue
import com.nasgrad.nasGradApp.R
import kotlinx.android.synthetic.main.create_issue_bottom_navigation_layout.*
import kotlinx.android.synthetic.main.fragment_issue_details.*
import timber.log.Timber

class IssueDetailsFragment : Fragment(), View.OnClickListener {

    private lateinit var issue: Issue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as CreateIssueActivity).supportActionBar?.title =
            getString(R.string.issue_details_title)
        (activity as CreateIssueActivity).enableHomeButton(false)
        return inflater.inflate(R.layout.fragment_issue_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPageIndicator.text = String.format(getString(R.string.create_issue_page_indicator), 3)

        issue = (activity as CreateIssueActivity).issue
        issueTitle.setText(issue.title)
        issueDescription.setText(issue.description)

        // for ime_action_done to work this has to be set programmatically
        issueDescription.imeOptions = EditorInfo.IME_ACTION_DONE
        issueDescription.setRawInputType(InputType.TYPE_CLASS_TEXT)

        previousScreen.setOnClickListener(this)
        previousScreen.visibility = View.VISIBLE

        nextScreen.isEnabled = false
        nextScreen.setOnClickListener(this)

        issueTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                text: CharSequence, start: Int, before: Int, count: Int
            ) {
                nextScreen.isEnabled = text.toString().trim().isNotEmpty()
            }
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            previousScreen.id -> {
                (activity as CreateIssueActivity).openPreviousFragment()
            }
            nextScreen.id -> {
                // update issue
                issue.title = issueTitle.text.toString()
                issue.description = issueDescription.text.toString()

                (activity as CreateIssueActivity).setFragment(
                    R.id.mainContent,
                    PreviewIssueFragment()
                )
            }
        }
    }
}
