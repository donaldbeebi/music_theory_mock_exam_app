package com.donald.musictheoryapp.util.practiceoptions

import android.os.Parcel
import android.os.Parcelable
import com.donald.musictheoryapp.util.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SectionOption(
    //val number: Int,
    val identifier: String,
    //val name: String,
    val questionGroupOptions: List<QuestionGroupOption>
) : Iterable<QuestionGroupOption>, Parcelable {
    constructor(parcel: Parcel) : this(
        //parcel.readInt(),
        parcel.readString()!!,
        //parcel.readString()!!,
        parcel.createTypedArrayList(QuestionGroupOption)!!
    )

    override fun iterator() = questionGroupOptions.iterator()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //parcel.writeInt(number)
        parcel.writeString(identifier)
        //parcel.writeString(name)
        parcel.writeTypedList(questionGroupOptions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SectionOption> {
        override fun createFromParcel(parcel: Parcel): SectionOption {
            return SectionOption(parcel)
        }

        override fun newArray(size: Int): Array<SectionOption?> {
            return arrayOfNulls(size)
        }
    }

    fun toJson() = JSONObject().apply {
        put("identifier", identifier)
        put(
            "groups",
            JSONArray().apply { questionGroupOptions.forEach { put(it.toJson()) } }
        )
    }
}

fun JSONObject.tryToSectionOption(): Result<SectionOption, JSONException> {
    val identifier = tryGetString("identifier")
        .otherwise { return Result.Error(it) }
        ?: return Result.Error(JSONException("Value for key 'identifier' is null"))

    val optionArray = tryGetJSONArray("groups")
        .otherwise { return Result.Error(it) }
        ?: return Result.Error(JSONException("Value for key 'options' is null"))

    val options = List(optionArray.length()) { optionIndex ->
        val groupOptionJson = optionArray.tryGetJSONObject(optionIndex)
            .otherwise { return Result.Error(it) }
            ?: return Result.Error(JSONException("Group option at index $optionIndex is null"))
        groupOptionJson.tryToGroupOption()
            .otherwise { return Result.Error(it) }
    }

    return Result.Value(SectionOption(identifier, options))
}