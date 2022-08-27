package com.donald.musictheoryapp.pagedexercise

import com.donald.musictheoryapp.question.Description
import com.donald.musictheoryapp.question.ChildQuestion

// TODO: OPTIMIZE FOR JETPACK COMPOSE
data class Page(
    //val sectionNumber: String,
    val sectionString: String,
    val pageToPeekIndex: Int?,
    val questionString: String,
    val descriptions: List<Description>,
    val question: ChildQuestion?,
    val images: List<String>
) {
    val isSectionPage: Boolean
        get() = question == null
}