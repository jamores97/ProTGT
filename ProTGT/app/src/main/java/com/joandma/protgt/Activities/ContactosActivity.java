package com.joandma.protgt.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joandma.protgt.API.InterfaceRequestApi;
import com.joandma.protgt.API.ServiceGenerator;
import com.joandma.protgt.Constant.PreferenceKeys;
import com.joandma.protgt.Models.ModelsApiProTGT.ContactoConfianza;
import com.joandma.protgt.Models.ModelsApiProTGT.UserRegister;
import com.joandma.protgt.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactosActivity extends AppCompatActivity {

    static final int REQUEST_SELECT_PHONE_NUMBER = 1;
    TextView tvNombreContacto, tvTelefonoContacto;
    Button buttonTerminarContacto;
    ImageView imageViewAgregarContacto;

    String nombre, telefono, nombreContactoResult, telefonoContactoResult;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        buttonTerminarContacto = findViewById(R.id.buttonTerminarContacto);
        tvNombreContacto = findViewById(R.id.tvNombreContacto);
        tvTelefonoContacto = findViewById(R.id.tvTelefonoContacto);

        imageViewAgregarContacto = findViewById(R.id.ivAgregarContacto);

        imageViewAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        buttonTerminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactoConfianza contactoConfianza = new ContactoConfianza();

                contactoConfianza.setNombre(nombre);
                contactoConfianza.setTelefono(telefono);


                //Progess Dialog
                pd = new ProgressDialog(ContactosActivity.this);
                pd.setIndeterminate(true);
                pd.setCancelable(false);

                //Estilo del dialog de spinner
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                Drawable drawable = new ProgressBar(ContactosActivity.this).getIndeterminateDrawable().mutate();
                drawable.setColorFilter(ContextCompat.getColor(ContactosActivity.this, R.color.colorBar), PorterDuff.Mode.SRC_IN);
                pd.setIndeterminateDrawable(drawable);


                //Titulo y mensaje de spinner
                pd.setTitle(getString(R.string.registrando));
                pd.setMessage(getString(R.string.cargando));

                //Muestra el dialog
                pd.show();

                final InterfaceRequestApi api = ServiceGenerator.createService(InterfaceRequestApi.class);

                Call<ContactoConfianza> call = api.addContactoConfianza(contactoConfianza);

                call.enqueue(new Callback<ContactoConfianza>() {
                    @Override
                    public void onResponse(Call<ContactoConfianza> call, Response<ContactoConfianza> response) {

                        pd.dismiss();

                        if (response.isSuccessful()){
                            ContactoConfianza result = response.body();



                            nombreContactoResult = result.getNombre();
                            telefonoContactoResult = result.getTelefono();

                            final String contacto_id = result.get_id();

                            prefs = ContactosActivity.this.getSharedPreferences("datos", Context.MODE_PRIVATE);

                            String nombre = prefs.getString(PreferenceKeys.USER_NAME, null);
                            String apellidos = prefs.getString(PreferenceKeys.USER_SURNAME, null);
                            String email = prefs.getString(PreferenceKeys.USER_EMAIL, null);
                            String password = prefs.getString(PreferenceKeys.USER_PASSWORD, null);
                            String pais = prefs.getString(PreferenceKeys.USER_PAIS, null);
                            String telefono = prefs.getString(PreferenceKeys.USER_TELEFONO, null);

                            String address_id = prefs.getString(PreferenceKeys.ADDRESS_ID, null);

                            UserRegister userRegister = new UserRegister(nombre, apellidos, email, password, pais, telefono);

                            userRegister.getAddress_id().add(address_id);
                            userRegister.getContacto_id().add(contacto_id);

                            Call<UserRegister> call2 = api.registerUser(userRegister);

                            call2.enqueue(new Callback<UserRegister>() {
                                @Override
                                public void onResponse(Call<UserRegister> call, Response<UserRegister> response) {

                                    if (response.isSuccessful()) {

                                        UserRegister result = response.body();

                                        editor = prefs.edit();

                                        editor.putString(PreferenceKeys.USER_TOKEN, result.getToken());
                                        editor.putString(PreferenceKeys.CONTACT_NAME, nombreContactoResult);
                                        editor.putString(PreferenceKeys.CONTACT_TELEFONO, telefonoContactoResult);
                                        editor.putString(PreferenceKeys.CONTACT_ID, contacto_id);

                                        editor.commit();


                                        Intent intentHome = new Intent(ContactosActivity.this, HomeActivity.class);
                                        startActivity(intentHome);
                                        finish();


                                    } else {
                                        Toast.makeText(ContactosActivity.this, R.string.fallo_creando_user, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserRegister> call, Throwable t) {
                                    Log.e("TAG","onFailure newUser: "+t.toString());
                                }
                            });
                            
                        } else {
                            Toast.makeText(ContactosActivity.this, "Fallo crítico creando contacto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ContactoConfianza> call, Throwable t) {
                        Log.e("TAG","onFailure newContactoDeConfianza: "+t.toString());
                    }
                });

            }
        });

    }


    public void selectContact() {
        // Start an activity for the user to pick a phone number from contacts
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int nombreIndex = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
                int telefonoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                nombre = cursor.getString(nombreIndex);
                telefono = cursor.getString(telefonoIndex);
                // Do something with the phone number

                tvNombreContacto.setText(nombre);
                tvTelefonoContacto.setText(telefono);

            }
        }


    }
}
