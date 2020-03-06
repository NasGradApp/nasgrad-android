package com.nasgrad.issue

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esafirm.imagepicker.features.ImagePicker
import com.nasgrad.nasGradApp.R
import com.nasgrad.utils.Helper
import kotlinx.android.synthetic.main.create_issue_bottom_navigation_layout.*
import kotlinx.android.synthetic.main.fragment_add_photo.*
import android.graphics.drawable.BitmapDrawable
import com.nasgrad.api.model.Issue

class AddPhotoFragment : Fragment(), View.OnClickListener {

    private lateinit var images: List<com.esafirm.imagepicker.model.Image>
    private lateinit var issue: Issue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_photo, container, false)
        (activity as CreateIssueActivity).setActionBarTitle(getString(R.string.issue_add_photo_title))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        issue = (activity as CreateIssueActivity).issue
        tvPageIndicator.text = String.format(getString(R.string.create_issue_page_indicator), 2)

        nextScreen.setOnClickListener(this)
        nextScreen.isEnabled = false

        if (issue.picturePreview != null) {
            photoPreview.setImageBitmap(Helper.decodePicturePreview(issue.picturePreview!!))
            nextScreen.isEnabled = true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        openCameraButton.setOnClickListener(this)
        openGalleryButton.setOnClickListener(this)
        deletePicture.setOnClickListener(this)
    }

    private fun deletePicture() {
        photoPreview.setImageDrawable(activity?.getDrawable(R.drawable.ic_image))
        deletePicture.visibility = View.GONE
        openGalleryButton.visibility = View.VISIBLE
        openCameraButton.visibility = View.VISIBLE
    }

    private fun openCameraMode() {
        ImagePicker.cameraOnly().start(this)
    }

    private fun openGalleryMode() {
        ImagePicker.create(this).theme(R.style.AppTheme).single().start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images = ImagePicker.getImages(data) as ArrayList<com.esafirm.imagepicker.model.Image>
            loadImage()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadImage() {
        photoPreview.setImageBitmap(BitmapFactory.decodeFile(images[0].path))
//        deletePicture.visibility = View.VISIBLE
        nextScreen.isEnabled = true
    }

    override fun onClick(view: View) {
        when (view.id) {
            nextScreen.id -> {
                // update issue
                val bitmap = (photoPreview.drawable as BitmapDrawable).bitmap
                issue.picturePreview = Helper.encodePicturePreview(bitmap)
                (activity as CreateIssueActivity).setFragment(R.id.mainContent, DescriptionFragment())
            }
            openCameraButton.id -> openCameraMode()
            openGalleryButton.id -> openGalleryMode()
            deletePicture.id -> deletePicture()
        }
    }
}