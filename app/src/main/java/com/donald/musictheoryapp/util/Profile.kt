package com.donald.musictheoryapp.util

import android.os.Parcel
import android.os.Parcelable

data class Profile(val nickname: String) : Parcelable {

    private constructor(parcel: Parcel) : this(parcel.readString() ?: throw IllegalStateException())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest ?: return
        dest.writeString(nickname)
    }

    companion object CREATOR: Parcelable.Creator<Profile?> {

        override fun createFromParcel(source: Parcel?): Profile? {
            source ?: return null
            return Profile(source)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }

    }

}