package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Idioma(
    @SerializedName("id_idioma") @Expose val id_idioma: Int,
    @SerializedName("nombre") @Expose val nombre: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_idioma)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Idioma> {
        override fun createFromParcel(parcel: Parcel): Idioma {
            return Idioma(parcel)
        }

        override fun newArray(size: Int): Array<Idioma?> {
            return arrayOfNulls(size)
        }
    }
}