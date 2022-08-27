package com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel

import android.util.Log
import com.donald.musictheoryapp.util.practiceoptions.PracticeOptions
import com.donald.musictheoryapp.util.practiceoptions.QuestionGroupOption
import com.donald.musictheoryapp.util.practiceoptions.SectionOption

class SectionOptionViewModel(
    val number: Int,
    val identifier: String,
    val name: String,
    val questionGroupOptionViewModels: List<QuestionGroupOptionViewModel>
)

fun List<SectionOptionViewModel>.toPracticeOptions(): PracticeOptions {
    return PracticeOptions(
        sectionOptions = filter { sectionOptionViewModel ->
            sectionOptionViewModel.questionGroupOptionViewModels.any { it.count > 0 }
        }.map { sectionOptionViewModel ->
            SectionOption(
                identifier = sectionOptionViewModel.identifier,
                questionGroupOptions = sectionOptionViewModel.questionGroupOptionViewModels.filter { it.count > 0 }.map { questionGroupOptionViewModel ->
                    QuestionGroupOption(
                        identifier = questionGroupOptionViewModel.identifier,
                        count = questionGroupOptionViewModel.count
                    )
                }
            )
        }
    ).also {
        Log.d("SectionOptionViewModel", it.toJson().toString(4))
    }
}

fun List<SectionOptionViewModel>.countCost(costPerQuestion: Int): Int = sumOf { sectionOptionViewModel ->
    sectionOptionViewModel.questionGroupOptionViewModels.sumOf { questionGroupOptionViewModel ->
        questionGroupOptionViewModel.count * costPerQuestion
    }
}