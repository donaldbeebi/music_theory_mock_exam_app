package com.donald.musictheoryapp.util.practiceoptions

import com.donald.musictheoryapp.util.Result
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.donald.musictheoryapp.util.otherwise
import com.donald.musictheoryapp.util.tryGetInt
import com.donald.musictheoryapp.util.tryGetString
import org.json.JSONException
import org.json.JSONObject

class QuestionGroupOption(
    //val number: Int,
    val identifier: String,
    //val name: String,
    val count: Int
) : Parcelable {
    //var count by mutableStateOf(count)

    constructor(parcel: Parcel) : this(
        //parcel.readInt(),
        parcel.readString()!!,
        //parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //parcel.writeInt(number)
        parcel.writeString(identifier)
        //parcel.writeString(name)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionGroupOption> {
        override fun createFromParcel(parcel: Parcel): QuestionGroupOption {
            return QuestionGroupOption(parcel)
        }

        override fun newArray(size: Int): Array<QuestionGroupOption?> {
            return arrayOfNulls(size)
        }
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("identifier", identifier)
        put("count", count)
    }
}

fun JSONObject.tryToGroupOption(): Result<QuestionGroupOption, JSONException> {
    val identifier = tryGetString("identifier")
        .otherwise { return Result.Error(it) }
        ?: return Result.Error(JSONException("Value for key 'identifier' is null"))
    val count = tryGetInt("count")
        .otherwise { return Result.Error(it) }
        ?: return Result.Error(JSONException("Value for key 'count' is null"))
    return Result.Value(QuestionGroupOption(identifier, count))
}