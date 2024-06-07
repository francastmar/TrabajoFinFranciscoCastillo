package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Director(
    @SerializedName("id_director") @Expose val id_director: Int,
    @SerializedName("nombre") @Expose val nombre: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_director)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Director> {
        override fun createFromParcel(parcel: Parcel): Director {
            return Director(parcel)
        }

        override fun newArray(size: Int): Array<Director?> {
            return arrayOfNulls(size)
        }
    }
}