package com.donald.musictheoryapp.util

class SectionOption(
    val identifier: String,
    val sectionName: String,
    val groupOptions: List<GroupOption>
) : Iterable<GroupOption> {

    override fun iterator(): Iterator<GroupOption> = groupOptions.iterator()

}