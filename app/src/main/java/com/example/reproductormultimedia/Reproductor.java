package com.example.reproductormultimedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import kotlin.Metadata;

public class Reproductor extends AppCompatActivity {

    public int id, repetir = 2, posicion = 0;
    Button play_pause, boton_repetir,aleatorio;
    MediaPlayer mediaplayer;
    ImageView iv;
    String titulo;
    ArrayList<Item> lista,original;
    TextView tv;
    boolean shuffle=false;


    MediaPlayer vectormp[]; ///////AQUI METEMOS EL NUMERO DE CANCIONES QUE VAMOS A TENER


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        Intent cogerId = getIntent();
        id = cogerId.getIntExtra("idUsuario", 0);
        posicion = cogerId.getIntExtra("posicion", 0);
        play_pause = (Button) findViewById(R.id.buttonPlay);
        boton_repetir = (Button) findViewById(R.id.buttonRep);
        aleatorio = (Button) findViewById(R.id.buttonAleatorio);
        tv = (TextView) findViewById(R.id.textViewCancion);


        Bundle bundle = getIntent().getExtras();
        lista = bundle.getParcelableArrayList("lista");
        original = new ArrayList<>(lista);
        iv = (ImageView) findViewById(R.id.imageView2);
        iv.setImageResource(lista.get(posicion).getImage());
        tv.setText(lista.get(posicion).getNombre_cancion());

        vectormp = new MediaPlayer[lista.size()];
        cargarLista();


    }

    public void cargarLista() {

        for (int i = 0; i < vectormp.length; i++) {
            int n = lista.get(i).raw;
            vectormp[i] = MediaPlayer.create(this, n);
        }
    }

    //Asignar menu layout al menu de esta ventana
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        /*MenuItem mi = menu.findItem(R.id.itemPlaylist);///////////////////////////////////
        mi.setVisible(false);*/
        return super.onCreateOptionsMenu(menu);
    }

    //Asignar acciones a los items del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSalir:
                vectormp[posicion].stop();
                /*Intent inicio = new Intent(Reproductor.this, MainActivity.class);
                startActivity(inicio);*/
                finish();
                break;
            case R.id.itemAcerca:
                Toast.makeText(this, "Hecho por Cornelio Romero Borrero", Toast.LENGTH_SHORT).show();
                break;
            case R.id.hubCanciones://Abrimos recycled view con las canciones
                vectormp[posicion].stop();
                finish();
                Intent hubCanciones = new Intent(Reproductor.this, ListaCanciones.class);
                hubCanciones.putExtra("idUsuario",id);////////////////////////////////////
                startActivity(hubCanciones);
                break;
            case R.id.itemPlaylist:
                vectormp[posicion].stop();
                Intent pl = new Intent(Reproductor.this, Playlist.class);
                pl.putExtra("idUsuario",id);//////////////////////////////////////
                startActivity(pl);
                finish();
                break;
            case R.id.RepVideo:
                vectormp[posicion].stop();
                Intent video = new Intent(Reproductor.this, RepVideo.class);
                startActivity(video);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Método para el botón PlayPause
    public void PlayPause(View view) {

        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause();
            play_pause.setBackgroundResource(R.drawable.play);
            Toast.makeText(this, "Pausa", Toast.LENGTH_SHORT).show();
        } else {
            vectormp[posicion].start();
            play_pause.setBackgroundResource(R.drawable.pause);
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();

        }
    }

    //Método para el botón Stop
    public void Stop(View view) {
        if (vectormp[posicion] != null) {
            vectormp[posicion].stop();

            cargarLista();

            posicion = 0;
            play_pause.setBackgroundResource(R.drawable.play);
            iv.setImageResource(lista.get(posicion).getImage());
            tv.setText(lista.get(posicion).getNombre_cancion());

            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();

        }
    }

    //Método para el botón Stop
    public void Inicio(View view) {
        if (vectormp[posicion] != null) {
            vectormp[posicion].stop();

            cargarLista();

            posicion = 0;
            play_pause.setBackgroundResource(R.drawable.play);
            iv.setImageResource(lista.get(posicion).getImage());
            tv.setText(lista.get(posicion).getNombre_cancion());

            Toast.makeText(this, "Comienzo de Lista", Toast.LENGTH_SHORT).show();

        }
    }

    //Método para el botón Final
    public void Final(View view) {
        if (vectormp[posicion] != null) {
            vectormp[posicion].stop();

            cargarLista();

            posicion = vectormp.length - 1;
            play_pause.setBackgroundResource(R.drawable.play);
            iv.setImageResource(lista.get(posicion).getImage());
            tv.setText(lista.get(posicion).getNombre_cancion());

            Toast.makeText(this, "Final de Lista", Toast.LENGTH_SHORT).show();

        }
    }


    //Método para repetir una pista
    public void Repetir(View view) {
        if (repetir == 1) {
            boton_repetir.setBackgroundResource(R.drawable.repetir);
            Toast.makeText(this, "No Repetir", Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(false);
            repetir = 2;

        } else {
            boton_repetir.setBackgroundResource(R.drawable.no_repetir);
            Toast.makeText(this, "Repetir", Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(true);
            repetir = 1;
        }
    }

    //Método para saltar a la siguiente canción
    public void Siguiente(View view) {
        if (posicion < vectormp.length - 1) {

            if (vectormp[posicion].isPlaying()) {
                vectormp[posicion].stop(); //Paramos la canción que estamos reproduciendo antes de saltar a la siguiente
                posicion++;
                vectormp[posicion].start();


                iv.setImageResource(lista.get(posicion).getImage());
                tv.setText(lista.get(posicion).getNombre_cancion());

            } else {
                posicion++;

                iv.setImageResource(lista.get(posicion).getImage());
                tv.setText(lista.get(posicion).getNombre_cancion());

            }
        } else {
            Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para volver a la canción anterior
    public void Anterior(View view) {
        if (posicion >= 1) {

            if (vectormp[posicion].isPlaying()) {
                vectormp[posicion].stop();

                cargarLista();

                posicion--;


                iv.setImageResource(lista.get(posicion).getImage());
                tv.setText(lista.get(posicion).getNombre_cancion());


                vectormp[posicion].start();

            } else {
                posicion--;


                iv.setImageResource(lista.get(posicion).getImage());
                tv.setText(lista.get(posicion).getNombre_cancion());

            }

        } else {
            Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para aleatorio
    public void Aleatorio(View view) {

    if(shuffle == false){
        vectormp[posicion].stop();
        Collections.shuffle(lista);
        play_pause.setBackgroundResource(R.drawable.play);
        cargarLista();
        aleatorio.setBackgroundResource(R.drawable.aleatorio_on);
        Toast.makeText(this, "Aleatorio Activado", Toast.LENGTH_SHORT).show();
        iv.setImageResource(lista.get(posicion).getImage());
        tv.setText(lista.get(posicion).getNombre_cancion());
        shuffle=true;

    }else{
        vectormp[posicion].stop();
        play_pause.setBackgroundResource(R.drawable.play);
        lista.clear();
        lista.addAll(original);
        cargarLista();
        shuffle=false;
        aleatorio.setBackgroundResource(R.drawable.aleatorio);
        iv.setImageResource(lista.get(posicion).getImage());
        tv.setText(lista.get(posicion).getNombre_cancion());
        Toast.makeText(this, "Aleatorio Desactivado", Toast.LENGTH_SHORT).show();
    }

    }

}