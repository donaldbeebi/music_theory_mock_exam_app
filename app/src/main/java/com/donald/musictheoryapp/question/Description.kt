package com.donald.musictheoryapp.question

import com.donald.musictheoryapp.util.getDescriptionType
import org.json.JSONException
import org.json.JSONObject
import kotlin.Throws

class Description(val type: Type, val content: String) {
    override fun toString(): String {
        return "Type: " + type + " " +
                "Content: " + content
    }

    constructor(type: Int, content: String): this(Type.values()[type], content)

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put(DESCRIPTION_TYPE, type.ordinal)
            put("content", content)
        }
    }

    companion object {

        @Throws(JSONException::class)
        fun fromJson(jsonObject: JSONObject): Description {
            return Description(jsonObject.getDescriptionType(), jsonObject.getString("content"))
        }

    }

    enum class Type { TEXT, TEXT_EMPHASIZE, TEXT_SPANNABLE, IMAGE, SCORE }

}