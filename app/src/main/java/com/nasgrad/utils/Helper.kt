package com.nasgrad.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.nasgrad.api.model.IssueCategory
import com.nasgrad.api.model.IssueType
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap


class Helper {
    companion object {

        var issueTypes: HashMap<String, IssueType> = HashMap()

        var issueCategories: HashMap<String, IssueCategory> = HashMap()

        fun getTypeName(typeId: String?) : String? {
            val issueType = issueTypes[typeId]
            return issueType?.name
        }

        fun getCategoriesForType(issueType: IssueType) : MutableList<IssueCategory> {
            val categoriesToReturn : MutableList<IssueCategory> = mutableListOf()
            val issueCats = issueType.categories

            for (cat in issueCats) {
                if (issueCategories.containsKey(cat)) {
                    val temp : IssueCategory = issueCategories[cat]!!
                    categoriesToReturn.add(temp)
                }
            }
            return categoriesToReturn
        }

        const val USER_ID_KEY = "user_id"

        fun randomGUID(): String {
            return UUID.randomUUID().toString()
        }

        fun decodePicturePreview(picturePreview: String): Bitmap {
            val decodedString = Base64.decode(picturePreview, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        fun encodePicturePreview(picturePreview: Bitmap): String {
            val stream = ByteArrayOutputStream()
            picturePreview.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            return stream.toByteArray().toString()
        }
    }
}