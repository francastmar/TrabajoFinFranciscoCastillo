package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Trabajador(
    @SerializedName("id_trabajador") @Expose val idTrabajador: Int,
    @SerializedName("id_persona") @Expose val idPersona: Int,
    @SerializedName("fecha_alta") @Expose val fechaAlta: String,
    @SerializedName("personas") @Expose val personas: Persona
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readParcelable(Persona::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idTrabajador)
        parcel.writeInt(idPersona)
        parcel.writeString(fechaAlta)
        parcel.writeParcelable(personas, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trabajador> {
        override fun createFromParcel(parcel: Parcel): Trabajador {
            return Trabajador(parcel)
        }

        override fun newArray(size: Int): Array<Trabajador?> {
            return arrayOfNulls(size)
        }
    }
}