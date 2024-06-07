package com.example.VideoclubFCM.modelo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pelicula(
    @SerializedName("id_pelicula") @Expose val id_pelicula: Int,
    @SerializedName("titulo_original") @Expose val titulo_original: String,
    @SerializedName("duracion") @Expose val duracion: Int,
    @SerializedName("sinopsis") @Expose val sinopsis: String,
    @SerializedName("actores") @Expose  val actores: List<Actor>,
    @SerializedName("directores") @Expose val directores: Director,
    @SerializedName("paises") @Expose val paises: Pais,
    @SerializedName("productos") @Expose val productos: Productos,
    @SerializedName("generos") @Expose val generos: List<Genero>,
    @SerializedName("id_pais") @Expose val id_pais: Int,
    @SerializedName("id_genero") @Expose val id_genero: Int,
    @SerializedName("id_director") @Expose val id_director: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        mutableListOf<Actor>().apply {
            parcel.readList(this as List<*>, Actor::class.java.classLoader)
        },
        parcel.readParcelable(Director::class.java.classLoader)!!,
        parcel.readParcelable(Pais::class.java.classLoader)!!,
        parcel.readParcelable(Productos::class.java.classLoader)!!,
        mutableListOf<Genero>().apply {
            parcel.readList(this as List<*>, Genero::class.java.classLoader)
        },
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_pelicula)
        parcel.writeString(titulo_original)
        parcel.writeInt(duracion)
        parcel.writeString(sinopsis)
        parcel.writeList(actores)
        parcel.writeParcelable(directores, flags)
        parcel.writeParcelable(paises, flags)
        parcel.writeParcelable(productos, flags)
        parcel.writeList(generos)
        parcel.writeInt(id_pais)
        parcel.writeInt(id_genero)
        parcel.writeInt(id_director)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pelicula> {
        override fun createFromParcel(parcel: Parcel): Pelicula {
            return Pelicula(parcel)
        }

        override fun newArray(size: Int): Array<Pelicula?> {
            return arrayOfNulls(size)
        }
    }
}