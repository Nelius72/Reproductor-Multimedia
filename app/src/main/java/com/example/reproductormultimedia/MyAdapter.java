package com.example.reproductormultimedia;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<Item> items;
    int idUsuario;

    public MyAdapter(Context context, List<Item> items, int idUsuario) {
        this.context = context;
        this.items = items;
        this.idUsuario = idUsuario;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Item cancion = items.get(position);
        holder.artistaView.setText(items.get(position).getNombre_artista());
        holder.cancionView.setText(items.get(position).getNombre_cancion());
        holder.imageView.setImageResource(items.get(position).getImage());
        holder.fav.setImageResource(R.drawable.pokeball);
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registraFav(cogerIdCancion(cancion));////////////////////
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    public int cogerIdCancion(Item cancion){

        BaseDatos admin = new BaseDatos(context);
        SQLiteDatabase BasedeDatos = admin.getWritableDatabase();

        String query= "SELECT id FROM CANCIONES WHERE titulo LIKE ?";
        String[] cancionArr = {cancion.getNombre_cancion()};
        Cursor fila = BasedeDatos.rawQuery(query, cancionArr);
        fila.moveToFirst();
        int n = fila.getInt(0);

        fila.close();
        BasedeDatos.close();

        return n;
    }

    public void registraFav(int idCancion){

        BaseDatos admin = new BaseDatos(context);
        SQLiteDatabase BasedeDatos = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        registro.put("id_usuario", idUsuario);
        registro.put("id_cancion", idCancion);

        BasedeDatos.insert("USUARIO_CANCION", null, registro);
        Toast.makeText(context, "CANCION INTRODUCIDA", Toast.LENGTH_SHORT).show();
        BasedeDatos.close();

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
