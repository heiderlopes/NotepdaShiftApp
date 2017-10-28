package br.com.heiderlopes.notepdashiftapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.picasso.Picasso;

import br.com.heiderlopes.notepdashiftapp.api.NotaAPI;
import br.com.heiderlopes.notepdashiftapp.model.Nota;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etTitulo;
    private EditText etTexto;
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etTexto = (EditText) findViewById(R.id.etTexto);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);

        Picasso.with(this)
                .load("https://lh3.ggpht.com/1aLXJxjLDnnMnkSg6mgPy1A-gn46jTEbmFofpjpA-i81jYZoMVVPe0NcA-ExNanQnYlL=w300")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.erro)
                .into(ivLogo);

        Stetho.initializeWithDefaults(this);
    }

    public void limpar(View v) {
        etTitulo.setText("");
        etTexto.setText("");
    }

    public void pesquisar(View v) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                "Aguarde! Estamos buscando seus dados", true);

        dialog.show();

        NotaAPI api = getRetrofit().create(NotaAPI.class);
        api.buscarNota(etTitulo.getText().toString())
                .enqueue(new Callback<Nota>() {
                    @Override
                    public void onResponse(Call<Nota> call, Response<Nota> response) {
                        dialog.dismiss();
                        if(response.isSuccessful())
                            etTexto.setText(response.body().getDescricao());
                    }

                    @Override
                    public void onFailure(Call<Nota> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "Ops! Deu ruim!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void salvar(View v) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                "Aguarde! Estamos gravando seus dados", true);

        dialog.show();

        NotaAPI api = getRetrofit().create(NotaAPI.class);
        Nota nota = new Nota(etTitulo.getText().toString(),
                etTexto.getText().toString());

        api.salvar(nota).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this,
                        "Dado gravado com sucesso", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this,
                        "Ops! Deu ruim!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private Retrofit getRetrofit() {

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://notepadcloudshiftheider.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


}
