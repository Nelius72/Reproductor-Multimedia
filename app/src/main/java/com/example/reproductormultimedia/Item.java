package com.example.reproductormultimedia;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    String nombre_artista, nombre_cancion;
    int image,raw;


    public Item(String nombre_artista, String nombre_cancion, int image, int raw) {

        this.nombre_artista = nombre_artista;
        this.nombre_cancion = nombre_cancion;
        this.image = image;
        this.raw = raw;
    }

    protected Item(Parcel in) {
        nombre_artista = in.readString();
        nombre_cancion = in.readString();
        image = in.readInt();
        raw = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNombre_artista() {
        return nombre_artista;
    }

    public void setNombre_artista(String nombre_artista) {
        this.nombre_artista = nombre_artista;
    }

    public String getNombre_cancion() {
        return nombre_cancion;
    }

    public void setNombre_cancion(String nombre_cancion) {
        this.nombre_cancion = nombre_cancion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre_artista);
        parcel.writeString(nombre_cancion);
        parcel.writeInt(image);
        parcel.writeInt(raw);
    }
}
