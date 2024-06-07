package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Plataforma(
    @SerializedName("id_plataforma") @Expose val id_plataforma: Int,
    @SerializedName("nombre") @Expose val nombre: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_plataforma)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Plataforma> {
        override fun createFromParcel(parcel: Parcel): Plataforma {
            return Plataforma(parcel)
        }

        override fun newArray(size: Int): Array<Plataforma?> {
            return arrayOfNulls(size)
        }
    }
}