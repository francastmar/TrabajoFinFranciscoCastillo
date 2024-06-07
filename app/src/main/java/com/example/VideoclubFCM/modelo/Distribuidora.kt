package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Distribuidora(
    @SerializedName("id_distribuidora") @Expose val id_distribuidora: Int,
    @SerializedName("nombre") @Expose val nombre: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_distribuidora)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Distribuidora> {
        override fun createFromParcel(parcel: Parcel): Distribuidora {
            return Distribuidora(parcel)
        }

        override fun newArray(size: Int): Array<Distribuidora?> {
            return arrayOfNulls(size)
        }
    }
}