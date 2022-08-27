package com.donald.musictheoryapp.util.practiceoptions

import android.os.Parcel
import android.os.Parcelable
import com.donald.musictheoryapp.util.Result
import com.donald.musictheoryapp.util.otherwise
import com.donald.musictheoryapp.util.tryGetJSONArray
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PracticeOptions(val sectionOptions: List<SectionOption>) : Iterable<SectionOption>, Parcelable {
    constructor(parcel: Parcel) : this(requireNotNull(parcel.createTypedArrayList(SectionOption.CREATOR)))

    override fun iterator() = sectionOptions.iterator()

    fun toJson(): JSONObject {
        val practiceOptions = JSONArray()
        forEach { sectionOption -> practiceOptions.put(sectionOption.toJson()) }
        return JSONObject().apply { put("sections", practiceOptions) }
    }
    fun countCost(costPerQuestion: Int) = sumOf { sectionOption ->
        sectionOption.questionGroupOptions.sumOf { it.count * costPerQuestion }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(sectionOptions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PracticeOptions> {
        override fun createFromParcel(parcel: Parcel): PracticeOptions {
            return PracticeOptions(parcel)
        }

        override fun newArray(size: Int): Array<PracticeOptions?> {
            return arrayOfNulls(size)
        }
    }
}

fun JSONObject.tryGetPracticeOptions(key: String): Result<PracticeOptions?, JSONException> {
    val array = tryGetJSONArray(key)
        .otherwise { return Result.Error(it) }
        ?: return Result.Value(null)
    return Result.Value(
        PracticeOptions(
            List(array.length()) { optionIndex ->
                array.getJSONObject(optionIndex).tryToSectionOption() otherwise { return Result.Error(it) }
            }
        )
    )
}