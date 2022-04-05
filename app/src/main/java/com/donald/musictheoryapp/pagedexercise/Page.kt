package com.donald.musictheoryapp.pagedexercise

import com.donald.musictheoryapp.question.Description
import com.donald.musictheoryapp.question.Question

data class Page(
    val sectionNumberString: String,
    val sectionName: String,
    val questionNumberString: String,
    val descriptions: List<Description>,
    val question: Question?
) {

    constructor(
        sectionNumber: String,
        sectionName: String,
        questionString: String,
        descriptions: Array<Description>,
        question: Question?
    ) : this(sectionNumber, sectionName, questionString, descriptions.toList(), question)

}