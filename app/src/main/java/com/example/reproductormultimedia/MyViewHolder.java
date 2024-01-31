package com.example.reproductormultimedia;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView, fav;
    TextView artistaView, cancionView;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        artistaView = itemView.findViewById(R.id.nombre_artista);
        cancionView = itemView.findViewById(R.id.nombre_cancion);
        fav = itemView.findViewById(R.id.imageViewFav);
    }
}
