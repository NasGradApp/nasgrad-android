package com.nasgrad

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nasgrad.adapter.OnItemClickListener
import com.nasgrad.helpers.bitmapDescriptorFromVector
import com.nasgrad.issue.CreateIssueActivity
import com.nasgrad.nasGradApp.R
import com.nasgrad.utils.Helper
import com.nasgrad.utils.SharedPreferencesHelper
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnItemClickListener {

    companion object {
        const val ITEM_ID = "ITEM_ID"
    }
//    private val client by lazy {
//        ApiClient.create()
//    }

    private var disposable: Disposable? = null
    private var map: GoogleMap? = null
    private var marker: MarkerOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        Timber.plant(Timber.DebugTree())

        reportIssue.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateIssueActivity::class.java))
        }
        createUserId()
//        setupAdapter()
        showIssues()
        marker =
            MarkerOptions().icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_pin))
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Timber.d("onMapReady")
        this.map = googleMap
        val noviSad = LatLng(45.246216, 19.801637)
        marker?.position(noviSad)
        googleMap?.addMarker(marker)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(noviSad))
    }

    override fun onItemClicked(
        itemId: String,
        itemTitle: String?,
        itemType: String?,
        itemDesc: String?,
        itemImage: String?
    ) {

        val detailsActivityIntent: Intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(ITEM_ID, itemId)
        }
        startActivity(detailsActivityIntent)
    }

    private fun showIssues() {
//        disposable = client.getAllIssues()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { result ->
//                if (result != null)
//                    setDataToAdapter(result)
//            }
    }

//    private fun setupAdapter() {
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        issueList.layoutManager = layoutManager
//    }
//
//    private fun setDataToAdapter(issues: List<Issue>) {
//        issueList.adapter = IssueAdapter(this, issues, this)
//    }

    private fun createUserId() {
        val sharedPreferences = SharedPreferencesHelper(this)
        sharedPreferences.setStringValue(Helper.USER_ID_KEY, Helper.randomGUID())
    }
}
