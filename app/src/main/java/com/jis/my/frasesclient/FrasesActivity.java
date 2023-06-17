package com.jis.my.frasesclient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jis.my.frasesclient.model.entity.Frase;
import com.jis.my.frasesclient.model.entity.FraseApiResponse;
import com.jis.my.frasesclient.service.FraseApiService;
import com.jis.my.frasesclient.utils.RetrofitUtil;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class FrasesActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView frasesListView;
    private ArrayAdapter<Frase> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frases);

        FloatingActionButton fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(this);



        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    loadFrases();
                });

        //We have our list view
        frasesListView = findViewById(R.id.frasesList);
        frasesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Frase item = adapter.getItem(position);
                Intent i = new Intent(FrasesActivity.this, FraseDetailActivity.class);
                i.putExtra(FraseDetailActivity.EXTRA_FRASE,item);
                launcher.launch(i);
            }
        });
        loadFrases();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, PreferenciasActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void loadFrases(){
        FraseApiService apiService = RetrofitUtil.GetRetrofitInstance().create(FraseApiService.class);
        Call<FraseApiResponse> call = apiService.getFrases();
        call.enqueue(new Callback<FraseApiResponse>() {
            List<Frase> fraseList;

            @Override
            public void onResponse(Call<FraseApiResponse> call, Response<FraseApiResponse> response) {
                if (response.isSuccessful()) {
                    FraseApiResponse apiResponse = response.body();
                    //Create adapter for ArrayList
                    adapter = new ArrayAdapter<Frase>(FrasesActivity.this,android.R.layout.simple_list_item_1, apiResponse.getFrases());

                    //Insert Adapter into List
                    frasesListView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<FraseApiResponse> call, Throwable t) {
                Timber.d("Error al recibir frases "+t.getMessage());
                Timber.d("Error al recibir frases "+t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FrasesActivity.this);
        builder.setTitle("Ingrese una nueva frase");

        // Crear el campo de texto
        final EditText editText = new EditText(FrasesActivity.this);
        builder.setView(editText);

        // Configurar los botones del diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textoIngresado = editText.getText().toString();
                if(textoIngresado.isEmpty()){
                    Toast.makeText(FrasesActivity.this,"No se puede ingresar un texto vacio", Toast.LENGTH_SHORT).show();
                    return;
                }

                FraseApiService apiService = RetrofitUtil.GetRetrofitInstance().create(FraseApiService.class);
                Frase frase = new Frase(); // Crea una instancia de la clase Datos con los valores adecuados
                frase.setFrase(textoIngresado);

                Call<Frase> call = apiService.saveFrase(frase);
                call.enqueue(new Callback<Frase>() {


                    @Override
                    public void onResponse(Call<Frase> call, Response<Frase> response) {
                        Toast.makeText(FrasesActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        loadFrases();

                    }

                    @Override
                    public void onFailure(Call<Frase> call, Throwable t) {
                        Toast.makeText(FrasesActivity.this, "Something went wrong "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cerrar el diálogo
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    }
