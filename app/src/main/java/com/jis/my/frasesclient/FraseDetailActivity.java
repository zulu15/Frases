package com.jis.my.frasesclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jis.my.frasesclient.model.entity.Frase;
import com.jis.my.frasesclient.service.FraseApiService;
import com.jis.my.frasesclient.utils.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FraseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_FRASE = "com.jis.my.frasesclient.EXTRA_FRASE";
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabMenuOption1;
    private FloatingActionButton fabMenuOption2;
    private EditText editFrase;
    private Frase fraseParam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frase_detail);

        Intent i = getIntent();
        if(i.hasExtra(EXTRA_FRASE)){
            fraseParam = (Frase) i.getSerializableExtra(EXTRA_FRASE);
            editFrase = (EditText) findViewById(R.id.editTextDetail);
            editFrase.setText(fraseParam.getFrase());
        }

        fabMenu = findViewById(R.id.fab_menu);
        fabMenuOption1 = findViewById(R.id.menu_guardar);
        fabMenuOption2 = findViewById(R.id.fab_menu_eliminar);

        fabMenuOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String frase = editFrase.getText().toString();
                if(!frase.isEmpty()){
                    FraseApiService apiService = RetrofitUtil.GetRetrofitInstance().create(FraseApiService.class);

                    fraseParam.setFrase(frase);

                    Call<Frase> call = apiService.saveFrase(fraseParam);
                    call.enqueue(new Callback<Frase>() {


                        @Override
                        public void onResponse(Call<Frase> call, Response<Frase> response) {
                            Toast.makeText(FraseDetailActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            volverActividadPrincipal();

                        }

                        @Override
                        public void onFailure(Call<Frase> call, Throwable t) {
                            Toast.makeText(FraseDetailActivity.this, "Something went wrong "+t.getMessage(), Toast.LENGTH_SHORT).show();
                            volverActividadPrincipal();
                        }
                    });
                }else{
                    Toast.makeText(FraseDetailActivity.this,"No se puede ingresar un texto vacio", Toast.LENGTH_SHORT).show();
                    volverActividadPrincipal();

                }
            }
        });

        fabMenuOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FraseApiService apiService = RetrofitUtil.GetRetrofitInstance().create(FraseApiService.class);

                Call<Void> call = apiService.deleteItem(fraseParam.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(FraseDetailActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            volverActividadPrincipal();

                        } else {
                            // Error en la solicitud DELETE
                            Toast.makeText(FraseDetailActivity.this, "Error en la solicitud DELETE", Toast.LENGTH_SHORT).show();
                            volverActividadPrincipal();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(FraseDetailActivity.this, "Something went wrong "+t.getMessage(), Toast.LENGTH_SHORT).show();
                        volverActividadPrincipal();
                    }
                });

            }
        });

    }


    private void volverActividadPrincipal(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}