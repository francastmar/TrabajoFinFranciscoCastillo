package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Clientes(
    @SerializedName("id_cliente") @Expose val id_cliente: Int,
    @SerializedName("id_persona") @Expose val id_persona: Int,
    @SerializedName("metodo_pago") @Expose val metodo_pago: String,
    @SerializedName("personas") @Expose val personas: Persona
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readParcelable(Persona::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_cliente)
        parcel.writeInt(id_persona)
        parcel.writeString(metodo_pago)
        parcel.writeParcelable(personas, flags)
    }
    
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Clientes> {
        override fun createFromParcel(parcel: Parcel): Clientes {
            return Clientes(parcel)
        }

        override fun newArray(size: Int): Array<Clientes?> {
            return arrayOfNulls(size)
        }
    }
}