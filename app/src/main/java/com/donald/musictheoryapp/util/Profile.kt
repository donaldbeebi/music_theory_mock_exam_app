package com.donald.musictheoryapp.util

import android.os.Parcel
import android.os.Parcelable
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.util.Profile.LangPref.Companion.tryGetProfileLangPref
import com.donald.musictheoryapp.util.Profile.Type.Companion.tryGetProfileType
import org.json.JSONException
import org.json.JSONObject

data class Profile(
    val nickname: String,
    val langPref: LangPref,
    val type: Type,
    val points: Int
) : Parcelable {
    private constructor(parcel: Parcel) : this(
        nickname = parcel.readString() ?: throw IllegalStateException(),
        langPref = LangPref.fromCode(parcel.readString() ?: throw IllegalStateException()),
        type = Type.fromOrdinal(parcel.readInt()),
        points = parcel.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest ?: return
        dest.writeString(nickname)
        dest.writeString(langPref.code)
        dest.writeInt(type.ordinal)
        dest.writeInt(points)
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("nickname", nickname)
        put("lang_pref", langPref)
        put("type", type.jsonValue)
        put("points", points)
    }

    companion object CREATOR: Parcelable.Creator<Profile?> {
        override fun createFromParcel(source: Parcel?): Profile? {
            source ?: return null
            return Profile(source)
        }
        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
        fun JSONObject.tryGetProfile(key: String): Result<Profile?, JSONException> {
            val profileJsonObject = tryGetJSONObject(key).otherwise { return Result.Error(it) } ?: return Result.Value(null)
            return with(profileJsonObject) {
                val nickname = tryGetString("nickname").otherwise { return Result.Error(it) }
                    ?: return Result.Error(JSONException("Value for key 'nickname' is null"))
                val langPref = tryGetProfileLangPref("lang_pref").otherwise { return Result.Error(it) }
                    ?: return Result.Error(JSONException("Value for key 'lang_pref' is null"))
                val type = tryGetProfileType("type").otherwise { return Result.Error(it) }
                    ?: return Result.Error(JSONException("Value for key 'type' is null"))
                val points = tryGetInt("points").otherwise { return Result.Error(it) }
                    ?: return Result.Error(JSONException("Value for key 'points' is null"))
                Result.Value(Profile(nickname, langPref, type, points))
            }
        }
    }

    enum class Type(
        val stringResource: Int,
        val jsonValue: String,
        val providerId: String
    ) {
        Google(R.string.profile_type_google, "google", "google.com"),
        Facebook(R.string.profile_type_facebook, "facebook", "facebook.com"),
        Email(R.string.profile_type_email, "email", "password");
        companion object {
            val types = values()

            fun fromOrdinal(ordinal: Int) = types[ordinal]

            fun fromJsonValue(jsonValue: String) = types.first { it.jsonValue == jsonValue }

            fun fromJsonValueOrNull(jsonValue: String) = types.firstOrNull { it.jsonValue == jsonValue }

            fun JSONObject.tryGetProfileType(key: String): Result<Type?, JSONException> {
                val jsonValue = tryGetString(key).otherwise { return Result.Error(it) }
                    ?: return Result.Value(null)
                return Result.Value(
                    fromJsonValueOrNull(jsonValue) ?: return Result.Error(JSONException("Value $jsonValue cannot be converted to Profile.Type"))
                )
            }

            fun fromProviderIdOrNull(providerId: String): Type? = types.firstOrNull { it.providerId == providerId }
        }
    }

    enum class LangPref(val code: String) {
        English("en");
        val jsonValue = code
        companion object {
            val langPrefs = values()
            fun fromCode(code: String) = langPrefs.first { it.code == code }
            fun fromJsonValue(jsonValue: String) = langPrefs.first { it.jsonValue == jsonValue }
            fun fromJsonValueOrNull(jsonValue: String) = langPrefs.firstOrNull { it.jsonValue == jsonValue }
            fun JSONObject.tryGetProfileLangPref(key: String): Result<LangPref?, JSONException> {
                val jsonValue = tryGetString(key).otherwise { return Result.Error(it) }
                    ?: return Result.Value(null)
                return Result.Value(
                    fromJsonValueOrNull(jsonValue) ?: return Result.Error(JSONException("Value $jsonValue cannot be converted to Profile.LangPref"))
                )
            }
        }
    }
}