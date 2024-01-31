package com.example.reproductormultimedia;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

public class Playlist extends AppCompatActivity {

    int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_canciones);

        Intent i = getIntent();
        idUsuario = i.getIntExtra("idUsuario", 0);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new Item("Henry Mendez", "Noche de Estrellas", R.drawable.henrymendez, R.raw.nochedeestrellas));
        items.add(new Item("Avril Lavigne", "Girlfriend", R.drawable.algirlfriend, R.raw.avril_lavigne_girlfriend));
        items.add(new Item("Avril Lavigne", "What The Hell", R.drawable.althehell, R.raw.whatthehell));
        items.add(new Item("Inna", "More Than Friends", R.drawable.inna, R.raw.morethanfriends));
        items.add(new Item("Swedish House Mafia", "Greyhound", R.drawable.greyhound, R.raw.greyhound));
        items.add(new Item("Default", "Tea", R.drawable.portada2, R.raw.tea));
        items.add(new Item("Default2", "Race", R.drawable.portada3, R.raw.race));
////PARA INTRODUCIR NUEVAS CANCIONES METER AQUI LOS DATOS





        ArrayList<Item> copyList = new ArrayList<Item>(items);
        for (Item item : copyList) {
            if (!esfav(item)) {
                items.remove(item);
            }
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyAdapter adapter = new MyAdapter(getApplicationContext(), items, idUsuario);
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent ventana = new Intent(Playlist.this, Reproductor.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("lista", items);
                ventana.putExtras(bundle);
                ventana.putExtra("posicion", position);
                ventana.putExtra("idUsuario", idUsuario);


                startActivity(ventana);


            }
        });

        recyclerView.setAdapter(adapter);


    }

    public int obtenerID(Item song) {
        BaseDatos admin = new BaseDatos(this);
        SQLiteDatabase bbdd = admin.getReadableDatabase();
        String sql = "Select id from canciones where titulo like ?";
        String [] args = {song.getNombre_cancion()};
        Cursor fila = bbdd.rawQuery(sql, args);
        int n=0;
        if(fila.moveToFirst()){
            n = fila.getInt(0);
            fila.close();
            bbdd.close();

        }else{

            fila.close();
            bbdd.close();
        }
        fila.moveToFirst();
        return n;
    }

    public boolean esfav(Item song) {
        BaseDatos admin = new BaseDatos(this);
        SQLiteDatabase bbdd = admin.getReadableDatabase();
        String sql = "Select id_cancion from usuario_cancion where id_usuario like " + idUsuario + " and id_cancion like " + obtenerID(song);
        Cursor fila = bbdd.rawQuery(sql, null);
        if (!fila.moveToFirst()) {
            fila.close();
            bbdd.close();
            return false;
        } else {
            fila.close();
            bbdd.close();
            return true;
        }
    }

    public boolean subselect(String titulo) {

        BaseDatos admin = new BaseDatos(this);
        SQLiteDatabase BasedeDatos = admin.getWritableDatabase();

        String query = "SELECT titulo FROM CANCIONES WHERE id LIKE (SELECT id_cancion FROM USUARIO_CANCION WHERE id_usuario LIKE " + idUsuario + ")";
        Cursor fila = BasedeDatos.rawQuery(query, null);
        if (fila.moveToFirst()) {
            if (fila.getString(0).equals(titulo)) {
                fila.close();
                BasedeDatos.close();
                return true;
            }else{
                fila.close();
                BasedeDatos.close();
                return false;
            }


        }else{
            fila.close();
            BasedeDatos.close();
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.iconoUsuarioMenu);
        byte[] img = obtenerImagenBBDD();
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        menuItem.setIcon(drawable);
        MenuItem mi = menu.findItem(R.id.itemPlaylist);
        mi.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSalir:

                finish();
                break;
            case R.id.itemAcerca:
                Toast.makeText(this, "Hecho por Cornelio Romero Borrero", Toast.LENGTH_SHORT).show();
                break;
            case R.id.hubCanciones:
                /*Intent hub = new Intent(Playlist.this, ListaCanciones.class);
                startActivity(hub);*/
                finish();
                break;
            case R.id.RepVideo:

                Intent video = new Intent(Playlist.this, RepVideo.class);
                startActivity(video);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public byte[] obtenerImagenBBDD() {
        BaseDatos admin = new BaseDatos(this);
        SQLiteDatabase BasedeDatos = admin.getWritableDatabase();


        Cursor fila = BasedeDatos.rawQuery("SELECT avatar FROM USUARIOS WHERE id LIKE " + idUsuario, null);
        fila.moveToFirst();
        byte[] imagen = fila.getBlob(0);
        fila.close();
        BasedeDatos.close();
        return imagen;

    }


}