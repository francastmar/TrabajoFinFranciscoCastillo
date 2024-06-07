package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeneroVideojuego(
    @SerializedName("id_genero") @Expose val id_genero: Int,
    @SerializedName("nombre") @Expose val nombre: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_genero)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GeneroVideojuego> {
        override fun createFromParcel(parcel: Parcel): GeneroVideojuego {
            return GeneroVideojuego(parcel)
        }

        override fun newArray(size: Int): Array<GeneroVideojuego?> {
            return arrayOfNulls(size)
        }
    }
}