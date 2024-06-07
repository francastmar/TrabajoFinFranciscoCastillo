package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Productos(
    @SerializedName("id_producto") @Expose val idProducto: Int,
    @SerializedName("titulo") @Expose val titulo: String,
    @SerializedName("anio") @Expose val anio: Int,
    @SerializedName("portada") @Expose val portada: String,
    @SerializedName("precio_alquiler") @Expose val precio_alquiler: Double,
    @SerializedName("precio_compra") @Expose val precio_compra: Double
   // @SerializedName("idioma") @Expose val idioma: Idioma
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
      //  parcel.readParcelable<Idioma>(Idioma::class.java.classLoader)!!
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(idProducto)
        dest.writeString(titulo)
        dest.writeInt(anio)
        dest.writeString(portada)
        dest.writeDouble(precio_alquiler)
        dest.writeDouble(precio_compra)
       // dest.writeParcelable(idioma, flags)
    }

    companion object CREATOR : Parcelable.Creator<Productos> {
        override fun createFromParcel(parcel: Parcel): Productos {
            return Productos(parcel)
        }

        override fun newArray(size: Int): Array<Productos?> {
            return arrayOfNulls(size)
        }
    }
}