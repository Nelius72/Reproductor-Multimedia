package com.example.reproductormultimedia;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText et_nombreUsuario, et_fechaNacimiento;
    private RadioButton rb_Masc, rb_Fem;
    private RadioGroup radioGroup;
    private ImageView avatar;
    static int usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_nombreUsuario = findViewById(R.id.editTextNombre);
        et_fechaNacimiento = findViewById(R.id.editTextFechaNac);
        rb_Fem = findViewById(R.id.radioButtonFem);
        rb_Masc = findViewById(R.id.radioButtonMasc);
        radioGroup = findViewById(R.id.RadioGroup);
        avatar = findViewById(R.id.imageView);
        Button registrar = findViewById(R.id.buttonReg);
        Button login = findViewById(R.id.buttonLog);

        FloatingActionButton cambiarIcono = findViewById(R.id.floatingActionButtonCambiarIcono);
        cambiarIcono.setOnClickListener(view -> elegirImagen.launch("image/*"));

        registrar.setOnClickListener(view -> RegistraUsuario());
        login.setOnClickListener(view -> Login());
        et_fechaNacimiento.setOnClickListener(view -> showDatePickerDialog());

        Integer[] fotos = {R.drawable.avatarfemenino, R.drawable.avatarmasculino};
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = radioGroup.findViewById(i);
            int index = radioGroup.indexOfChild(radioButton);
            avatar.setImageResource(fotos[index]);
        });
    }

    ActivityResultLauncher<String> elegirImagen = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            avatar.setImageURI(result);
        }
    });


    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
                et_fechaNacimiento.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //Comprobamos que el nombre cumpla el requisito y que sea mayor de 16 años
    public boolean compruebaRequisito(String nombre, String fechanac) {
        Boolean cumple = null;
        if (!nombre.matches("^[A-Za-z0-9_-]{8,}$")) {
            Toast.makeText(this, "Inserte un nombre de Usuario Válido", Toast.LENGTH_SHORT).show();
            cumple = false;
        } else if (fechanac.isEmpty()) {
            Toast.makeText(this, "Elige una fecha", Toast.LENGTH_SHORT).show();
            cumple = false;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date birthDate = format.parse(fechanac);
                Calendar now = Calendar.getInstance();
                Calendar dob = Calendar.getInstance();
                assert birthDate != null;
                dob.setTime(birthDate);
                int year1 = now.get(Calendar.YEAR);
                int year2 = dob.get(Calendar.YEAR);
                int age = year1 - year2;
                if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }
                if (age >= 16) {
                    cumple = true;
                } else {
                    Toast.makeText(this, "El usuario debe ser MAYOR de 16 años", Toast.LENGTH_SHORT).show();
                    cumple = false;
                }
            } catch (ParseException e) {
                System.out.println("No se pudo parsear la fecha de nacimiento");
            }
        }
        return Boolean.TRUE.equals(cumple);
    }

    //Metodo para registrar un usuario
    public void RegistraUsuario() {
        try {
            BaseDatos base = new BaseDatos(this);
            SQLiteDatabase BasedeDatos = base.getWritableDatabase();

            String UserName = et_nombreUsuario.getText().toString();
            String fecna = et_fechaNacimiento.getText().toString();
            String sexo;

            if (compruebaNombre(UserName)) {
                Toast.makeText(this, "El usuario \"" + UserName + "\" ya existe", Toast.LENGTH_SHORT).show();
                BasedeDatos.close();
            } else {

                if (rb_Masc.isChecked()) {
                    sexo = "Masculino";

                } else {
                    sexo = "Femenino";
                }


                ContentValues registro = new ContentValues();

                registro.put("nombre", UserName);
                registro.put("fechaNac", fecna);
                registro.put("sexo", sexo);
                Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] img = stream.toByteArray();
                registro.put("avatar", img);

                if (compruebaRequisito(UserName, fecna)) {

                    BasedeDatos.insert("USUARIOS", null, registro);
                    BasedeDatos.close();
                    Toast.makeText(this, "Usuario Registrado con Éxito", Toast.LENGTH_SHORT).show();
                    et_nombreUsuario.setText("");
                    et_fechaNacimiento.setText("");

                }
                BasedeDatos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Método para comprobar el nombre de usuario

    public boolean compruebaNombre(String nombre) {
        BaseDatos admin = new BaseDatos(this);
        SQLiteDatabase BasedeDatos = admin.getWritableDatabase();


        Cursor fila = BasedeDatos.rawQuery("SELECT * FROM USUARIOS WHERE nombre LIKE '" + nombre + "'", null);

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

    public void Login() {
        BaseDatos admin = new BaseDatos(this);
        SQLiteDatabase BasedeDatos = admin.getWritableDatabase();

        String nombre = et_nombreUsuario.getText().toString();
        Cursor fila = BasedeDatos.rawQuery("SELECT * FROM USUARIOS WHERE nombre LIKE '" + nombre + "'", null);

        if (fila.moveToFirst()) {//Si obtengo un resultado
            Intent form_reproductor = new Intent(MainActivity.this, ListaCanciones.class);
            usuario=fila.getInt(0);
            form_reproductor.putExtra("idUsuario", fila.getInt(0));
            startActivity(form_reproductor);

        } else {
            Toast.makeText(this, "Usuario Inexistente, Regístrate", Toast.LENGTH_SHORT).show();
        }

    }


    //Asignar menu layout al menu de esta ventana
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    //Asignar acciones a los items del menu
   /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSalir:
                Toast.makeText(this, "Saliendo", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

}