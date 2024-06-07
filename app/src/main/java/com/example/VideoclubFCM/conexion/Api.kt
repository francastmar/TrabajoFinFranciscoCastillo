package com.example.VideoclubFCM.conexion

import com.example.VideoclubFCM.modelo.Alquiler
import com.example.VideoclubFCM.modelo.Clientes
import com.example.VideoclubFCM.modelo.Desarrolladora
import com.example.VideoclubFCM.modelo.Director
import com.example.VideoclubFCM.modelo.Distribuidora
import com.example.VideoclubFCM.modelo.Genero
import com.example.VideoclubFCM.modelo.GeneroVideojuego
import com.example.VideoclubFCM.modelo.Pais
import com.example.VideoclubFCM.modelo.Pelicula
import com.example.VideoclubFCM.modelo.Plataforma
import com.example.VideoclubFCM.modelo.Productos
import com.example.VideoclubFCM.modelo.Videojuego
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface Api {

    @GET("peliculas")
    fun obtenerPeliculas(): Call<ArrayList<Pelicula>>
    @GET("videojuegos")
    fun obtenerJuegos(): Call<ArrayList<Videojuego>>

    @GET("generos")
    fun obtenerGenerosPelicula(): Call<ArrayList<Genero>>

    @GET("directores")
    fun obtenerDirectores(): Call<ArrayList<Director>>

    @GET("clientes")
    fun obtenerClientes(): Call<ArrayList<Clientes>>

    @GET("paises")
    fun obtenerPaises(): Call<ArrayList<Pais>>

    @FormUrlEncoded
    @POST("autenticar")
    fun login(
        @Field("username") username: String,
        @Field("contraseña") contraseña: String
    ): Call<Clientes>

    @GET("alquileresporcliente/{cliente}")
    fun obtenerAlquileresPorCliente(@Path("cliente") cliente: String): Call<ArrayList<Alquiler>>

    @GET("peliculaspordirector/{director}")
    fun obtenerPeliculasPorDirector(@Path("director") director: String): Call<ArrayList<Pelicula>>
    @GET("peliculasporgenero/{genero}")
    fun obtenerPeliculasPorGenero(@Path("genero") genero: String): Call<ArrayList<Pelicula>>
    @GET("peliculasporanio/{anio}")
    fun obtenerPeliculasPorAnio(@Path("anio") anio: String): Call<ArrayList<Pelicula>>

    @GET("videojuegospordesarrolladora/{desarrolladora}")
    fun obtenerJuegosDesarrolladora(@Path("desarrolladora") desarrolladora: String): Call<ArrayList<Videojuego>>
    @GET("videojuegosporplataforma/{plataforma}")
    fun obtenerJuegosPlataforma(@Path("plataforma") plataforma: String): Call<ArrayList<Videojuego>>
    @GET("videojuegosmultijugador")
    fun obtenerJuegosMltijugador(): Call<ArrayList<Videojuego>>

    @GET("desarrolladoras")
    fun obtenerDesarroladoras(): Call<ArrayList<Desarrolladora>>
    @GET("plataformas")
    fun obtenerPlataformas(): Call<ArrayList<Plataforma>>
    @GET("distribuidoras")
    fun obtenerDistribuidoras(): Call<ArrayList<Distribuidora>>
    @GET("generosvideojuego")
    fun obtenerGenerosVideojuego(): Call<ArrayList<GeneroVideojuego>>

    @FormUrlEncoded
    @POST("alquileres")
    fun guardarAlquiler(
        @Field("id_alquiler") id_alquiler: Int?,
        @Field("id_producto") id_producto: Int?,
        @Field("id_trabajador") id_cliente: Int?,
        @Field("id_cliente") id_trabajador: Int?,
        @Field("fecha_alquiler") fecha_alquiler: String?,
        @Field("fecha_prev_devolucion") fecha_prev_devolucion: String?,
        @Field("fecha_devolucion") fecha_devolucion: String?,
        @Field("disponible") disponible: Boolean?,
        @Field("productos") producto: Productos?
    ): Call<Alquiler>

    @Multipart
    @Headers("Accept: application/json")
    @POST("peliculas")
    fun insertarPelicula(@Part("titulo") titulo: RequestBody,
                         @Part("anio") anio: RequestBody,
                         @Part portada: MultipartBody.Part,
                         @Part("precio_alquiler") precio_alquiler: RequestBody,
                         @Part("precio_compra") precio_compra: RequestBody,
                         @Part("titulo_original") titulo_original: RequestBody,
                         @Part("duracion") duracion: RequestBody,
                         @Part("sinopsis") sinopsis: RequestBody,
                         @Part("directores") directores: RequestBody,
                         @Part("paises") paises: RequestBody,
                         @Part("id_pais") idPais: RequestBody,
                         @Part("id_genero") idGenero: RequestBody,
                         @Part("id_director") idDirector: RequestBody
    ): Call<Pelicula>

    @Multipart
    @Headers("Accept: application/json")
    @POST("videojuegos")
    fun insertarJuego(@Part("titulo") titulo: RequestBody,
                         @Part("anio") anio: RequestBody,
                         @Part portada: MultipartBody.Part,
                         @Part("precio_alquiler") precio_alquiler: RequestBody,
                         @Part("precio_compra") precio_compra: RequestBody,
                         @Part("doblado") doblado: RequestBody,
                         @Part("multijugador") multijugador: RequestBody,
                         @Part("id_plataforma") id_plataforma: RequestBody,
                         @Part("id_distribuidora") id_distribuidora: RequestBody,
                         @Part("id_desarrolladora") id_desarrolladora: RequestBody,
                         @Part("id_genero_videojuego") id_genero_videojuego: RequestBody
    ): Call<Videojuego>

    @FormUrlEncoded
    @POST("peliculasgeneros")
    fun anyadirGeneroAPelicula(
        @Field("id_pelicula") idPelicula: Int,
        @Field("id_genero") idGenero: Int
    ): Call<Pelicula>
    // Actualizar la pelicula para añadir los generos y actores
    @PUT("peliculas/{id}")
    fun actualizaPelicula(
        @Path("id") id: Int, @Body pelicula: Pelicula
    ): Call<Pelicula>

    @FormUrlEncoded
    @POST("clientes")
    fun guardarCliente(
        @Field("nombre") nombre: String,
        @Field("apellido1") apellido1: String,
        @Field("apellido2") apellido2: String,
        @Field("direccion") direccion: String,
        @Field("mail") mail: String,
        @Field("telefono") telefono: String,
        @Field("fecha_nacimiento") fechaNacimiento: String,
        @Field("username") username: String,
        @Field("contraseña") contraseña: String,
        @Field("id_rol") idRol: Int,
        @Field("metodo_pago") metodoPago: String
    ): Call<Clientes>
}