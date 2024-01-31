package com.example.reproductormultimedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListaCanciones extends AppCompatActivity {

    int idUsuario;
    ArrayList<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_canciones);

        Intent i = getIntent();
        idUsuario=i.getIntExtra("idUsuario",0);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        items = new ArrayList<Item>();
        items.add(new Item("Henry Mendez", "Noche de Estrellas", R.drawable.henrymendez, R.raw.nochedeestrellas));
        items.add(new Item("Avril Lavigne", "Girlfriend", R.drawable.algirlfriend, R.raw.avril_lavigne_girlfriend));
        items.add(new Item("Avril Lavigne", "What The Hell", R.drawable.althehell, R.raw.whatthehell));
        items.add(new Item("Inna", "More Than Friends", R.drawable.inna, R.raw.morethanfriends));
        items.add(new Item("Swedish House Mafia", "Greyhound", R.drawable.greyhound, R.raw.greyhound));
        items.add(new Item("Default", "Tea", R.drawable.portada2, R.raw.tea));
        items.add(new Item("Default2", "Race", R.drawable.portada3, R.raw.race));
        ////PARA INTRODUCIR NUEVAS CANCIONES METER AQUI LOS DATOS


       insertaCancion();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyAdapter adapter = new MyAdapter(getApplicationContext(),items,idUsuario);
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent ventana = new Intent(ListaCanciones.this,Reproductor.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("lista", items);
                ventana.putExtras(bundle);
                ventana.putExtra("posicion",position);
                ventana.putExtra("idUsuario",idUsuario);


                startActivity(ventana);


            }
        });

        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.iconoUsuarioMenu);
        byte[] img = obtenerImagenBBDD();
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        menuItem.setIcon(drawable);
        MenuItem mi = menu.findItem(R.id.hubCanciones);
        mi.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSalir:
                /*Intent inicio = new Intent(ListaCanciones.this, MainActivity.class);
                startActivity(inicio);*/
                AlertDialog.Builder cierreSesion= new AlertDialog.Builder(ListaCanciones.this);
                cierreSesion.setMessage("Estas a punto de cerrar sesion, ¿estas seguro?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog titulo = cierreSesion.create();
                titulo.setTitle("Cerrar Sesion");
                titulo.show();
                break;
            case R.id.itemAcerca:
                Toast.makeText(this, "Hecho por Cornelio Romero Borrero", Toast.LENGTH_SHORT).show();
                break;
            case R.id.itemPlaylist:

                Intent pl = new Intent(ListaCanciones.this, Playlist.class);
                pl.putExtra("idUsuario",idUsuario);
                startActivity(pl);
                break;
            case R.id.RepVideo:

                Intent video = new Intent(ListaCanciones.this, RepVideo.class);
                startActivity(video);

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

    public boolean compruebaCancion(String cancion) {/////////////////////////////////
        BaseDatos admin = new BaseDatos(this);
        SQLiteDatabase BasedeDatos = admin.getWritableDatabase();

        String query= "SELECT * FROM CANCIONES WHERE titulo LIKE ?";
        String[] cancionArr = {cancion};
        Cursor fila = BasedeDatos.rawQuery(query, cancionArr);

        if (fila.moveToFirst()) {
            fila.close();
            BasedeDatos.close();
            return true;

        } else {

            fila.close();
            BasedeDatos.close();
            return false;
        }
    }

   public void insertaCancion(){//////////////////////////////////////////

        BaseDatos base = new BaseDatos(this);
        SQLiteDatabase BasedeDatos = base.getWritableDatabase();

        for(int x=0;x< items.size();x++){
            String nombre_cancion = items.get(x).nombre_cancion;
            String nombre_artista= items.get(x).nombre_artista;

            if(!compruebaCancion(nombre_cancion)){

                ContentValues registro = new ContentValues();

                registro.put("titulo", nombre_cancion);
                registro.put("nombreArtista", nombre_artista);
                BasedeDatos.insert("CANCIONES", null, registro);


            }else{
                BasedeDatos.close();
            }
        }

    }
    public void onBackPressed(){
        AlertDialog.Builder cierreSesion= new AlertDialog.Builder(ListaCanciones.this);
        cierreSesion.setMessage("Estas a punto de cerrar sesion, ¿estas seguro?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog titulo = cierreSesion.create();
        titulo.setTitle("Cerrar Sesion");
        titulo.show();
    }

}