package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Desarrolladora(
    @SerializedName("id_desarrolladora") @Expose val id_desarrolladora: Int,
    @SerializedName("nombre") @Expose val nombre: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_desarrolladora)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Desarrolladora> {
        override fun createFromParcel(parcel: Parcel): Desarrolladora {
            return Desarrolladora(parcel)
        }

        override fun newArray(size: Int): Array<Desarrolladora?> {
            return arrayOfNulls(size)
        }
    }
}