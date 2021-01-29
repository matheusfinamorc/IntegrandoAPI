package com.example.estoque.retrofit;

import com.example.estoque.retrofit.service.ProdutoService;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EstoqueRetrofit {
    private static final String URL_BASE = "http://192.168.0.108:8080/";
    private final ProdutoService produtoService;

    public EstoqueRetrofit() {
        // configuração retirada da Web do site Loggin Interceptor
        OkHttpClient client = configuraClient();

        // usar o usesClearTextTrafic = "true" no Manifest para liberar o trafego pelo http, ou coloca o http's' na baseUrl
        // colocar uses-permission INTERNET E ACESS_NETWORK no Manifest para liberar acesso a internet
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE) // ip da maquina(PC e celular tem que estar conectados na mesma rede) + porta de acesso liberada
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        produtoService = retrofit.create(ProdutoService.class);
    }

    @NotNull
    private OkHttpClient configuraClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    public ProdutoService getProdutoService() {
        return produtoService;
    }
}
