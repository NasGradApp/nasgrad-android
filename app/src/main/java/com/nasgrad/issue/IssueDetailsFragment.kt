package com.nasgrad.issue

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.nasgrad.adapter.TypesSpinnerAdapter
import com.nasgrad.api.model.IssueCategory
import com.nasgrad.api.model.IssueType
import com.nasgrad.nasGradApp.R
import com.nasgrad.utils.Helper
import kotlinx.android.synthetic.main.create_issue_bottom_navigation_layout.*
import kotlinx.android.synthetic.main.fragment_issue_details.*
import timber.log.Timber

class IssueDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        (activity as CreateIssueActivity).supportActionBar?.title = getString(R.string.issue_details_title)
        (activity as CreateIssueActivity).enableHomeButton(false)
        return inflater.inflate(R.layout.fragment_issue_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTypesSpinner()

        ibArrowLeft.visibility = View.VISIBLE
        tvPageIndicator.text = String.format(getString(R.string.create_issue_page_indicator), 2)
        val issue = (activity as CreateIssueActivity).issue
        issueTitle.setText(issue.title)
        issueDescription.setText(issue.description)

        ibArrowLeft.setOnClickListener(this)
        ibArrowRight.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            ibArrowLeft.id -> {
                (activity as CreateIssueActivity).openPreviousFragment()
            }
            ibArrowRight.id -> {
                // update issue
                val issue = (activity as CreateIssueActivity).issue
                issue.title = issueTitle.text.toString()
                issue.description = issueDescription.text.toString()

                val type = spinnerTypes.selectedItem as? IssueType
                issue.issueType = type?.name

                val typeCategories = type?.let { Helper.getCategoriesForType(type) }
                val categoryIds = mutableListOf<String>()
                typeCategories?.forEach { categoryIds.add(it.id) }
                issue.categories = categoryIds

                Timber.d("Issue: $issue")
                (activity as CreateIssueActivity).setFragment(R.id.mainContent, LocationFragment())
            }
        }
    }

    private fun initTypesSpinner() {
        val issueTypes = ArrayList(Helper.issueTypes.values)

        val adapter = TypesSpinnerAdapter((activity as CreateIssueActivity), R.layout.types_spinner_item, issueTypes)
        adapter.setDropDownViewResource(R.layout.types_spinner_item)

        spinnerTypes.adapter = adapter
        spinnerTypes.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Timber.d("onNothingSelected")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedType = parent?.getItemAtPosition(position) as IssueType
        Timber.d("onItemSelected $selectedType")

        val categories = Helper.getCategoriesForType(selectedType)
        when {
            categories.size == 1 -> {
                category1.visibility = View.VISIBLE
                category1.text = categories[0].name
                category2.visibility = View.GONE
                category3.visibility = View.GONE
            }
            categories.size == 2 -> {
                category1.visibility = View.VISIBLE
                category1.text = categories[0].name
                category2.visibility = View.VISIBLE
                category2.text = categories[1].name
                category3.visibility = View.GONE
            }
            categories.size >= 3 -> {
                category1.visibility = View.VISIBLE
                category1.text = categories[0].name
                category2.visibility = View.VISIBLE
                category2.text = categories[1].name
                category3.visibility = View.VISIBLE
                category3.text = categories[2].name
            }
            else -> {
                category1.visibility = View.GONE
                category2.visibility = View.GONE
                category3.visibility = View.GONE
            }
        }
    }
}
