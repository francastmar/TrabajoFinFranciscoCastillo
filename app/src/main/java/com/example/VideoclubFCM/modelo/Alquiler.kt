package com.example.VideoclubFCM.modelo

import java.util.Date

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Alquiler(
    @SerializedName("id_alquiler") @Expose val id_alquiler: Int?,
    @SerializedName("id_producto") @Expose val id_producto: Int?,
    @SerializedName("id_trabajador") @Expose val id_trabajador: Int?,
    @SerializedName("id_cliente") @Expose val id_cliente: Int?,
    @SerializedName("fecha_alquiler") @Expose val fecha_alquiler: String?,
    @SerializedName("fecha_prev_devolucion") @Expose val fecha_prev_devolucion: String?,
    @SerializedName("fecha_devolucion") @Expose val fecha_devolucion: String?,
    @SerializedName("disponible") @Expose val disponible: Boolean?,
    @SerializedName("productos") @Expose val producto: Productos?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Productos::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_alquiler!!)
        parcel.writeInt(id_producto!!)
        parcel.writeInt(id_cliente!!)
        parcel.writeInt(id_trabajador!!)
        parcel.writeString(fecha_alquiler)
        parcel.writeString(fecha_prev_devolucion)
        parcel.writeString(fecha_devolucion)
        parcel.writeByte(if (disponible == true) 1 else 0)
        parcel.writeParcelable(producto, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alquiler> {
        override fun createFromParcel(parcel: Parcel): Alquiler {
            return Alquiler(parcel)
        }

        override fun newArray(size: Int): Array<Alquiler?> {
            return arrayOfNulls(size)
        }
    }
}