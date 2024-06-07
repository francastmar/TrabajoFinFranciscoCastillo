package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Rol(
    @SerializedName("id_rol") @Expose val id_rol: Int,
    @SerializedName("rol") @Expose val rol: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_rol)
        parcel.writeString(rol)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Rol> {
        override fun createFromParcel(parcel: Parcel): Rol {
            return Rol(parcel)
        }

        override fun newArray(size: Int): Array<Rol?> {
            return arrayOfNulls(size)
        }
    }
}