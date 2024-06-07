package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Persona(
    @SerializedName("id_persona") @Expose val idPersona: Int,
    @SerializedName("nombre") @Expose val nombre: String,
    @SerializedName("apellido1") @Expose val apellido1: String,
    @SerializedName("apellido2") @Expose val apellido2: String,
    @SerializedName("direccion") @Expose val direccion: String,
    @SerializedName("mail") @Expose val mail: String,
    @SerializedName("telefono") @Expose val telefono: String,
    @SerializedName("fecha_nacimiento") @Expose val nacimiento: String,
    @SerializedName("username") @Expose val username: String,
    @SerializedName("contraseña") @Expose val password: String,
    @SerializedName("id_rol") @Expose val idRol: Int

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idPersona)
        parcel.writeString(nombre)
        parcel.writeString(apellido1)
        parcel.writeString(apellido2)
        parcel.writeString(direccion)
        parcel.writeString(mail)
        parcel.writeString(telefono)
        parcel.writeString(nacimiento)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeInt(idRol)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Persona> {
        override fun createFromParcel(parcel: Parcel): Persona {
            return Persona(parcel)
        }

        override fun newArray(size: Int): Array<Persona?> {
            return arrayOfNulls(size)
        }
    }
}