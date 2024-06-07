package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Videojuego(
    @SerializedName("doblado") @Expose val doblado: Int,
    @SerializedName("multijugador") @Expose val multijugador: Int,
    @SerializedName("plataformas") @Expose val plataformas: Plataforma,
    @SerializedName("distribuidoras") @Expose val distribuidoras: Distribuidora,
    @SerializedName("desarrolladoras") @Expose val desarrolladoras: Desarrolladora,
    @SerializedName("productos") @Expose val productos: Productos,
    @SerializedName("generos_videojuegos") @Expose val generos_videojuegos: GeneroVideojuego
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(Plataforma::class.java.classLoader)!!,
        parcel.readParcelable(Distribuidora::class.java.classLoader)!!,
        parcel.readParcelable(Desarrolladora::class.java.classLoader)!!,
        parcel.readParcelable(Productos::class.java.classLoader)!!,
        parcel.readParcelable(GeneroVideojuego::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(doblado)
        parcel.writeInt(multijugador)
        parcel.writeParcelable(plataformas, flags)
        parcel.writeParcelable(distribuidoras, flags)
        parcel.writeParcelable(desarrolladoras, flags)
        parcel.writeParcelable(productos, flags)
        parcel.writeParcelable(generos_videojuegos, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Videojuego> {
        override fun createFromParcel(parcel: Parcel): Videojuego {
            return Videojuego(parcel)
        }

        override fun newArray(size: Int): Array<Videojuego?> {
            return arrayOfNulls(size)
        }
    }
}