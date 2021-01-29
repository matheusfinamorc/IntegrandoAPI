package com.example.estoque.repository;

import android.content.Context;

import com.example.estoque.asynctask.BaseAsyncTask;
import com.example.estoque.database.EstoqueDatabase;
import com.example.estoque.database.dao.ProdutoDAO;
import com.example.estoque.model.Produto;
import com.example.estoque.retrofit.EstoqueRetrofit;
import com.example.estoque.retrofit.callback.CallbackComRetorno;
import com.example.estoque.retrofit.callback.CallbackSemRetorno;
import com.example.estoque.retrofit.service.ProdutoService;

import java.util.List;

import retrofit2.Call;
import retrofit2.internal.EverythingIsNonNull;

public class ProdutoRepository {

    private final ProdutoDAO dao;
    private final ProdutoService service;

    public ProdutoRepository(Context context) {
        EstoqueDatabase db = EstoqueDatabase.getInstance(context);
        dao = db.getProdutoDAO();
        service = new EstoqueRetrofit().getProdutoService();

    }

    public void buscaProdutos(DadosCarregadosCallBack<List<Produto>> callback) {
        //executa a Call

        buscaProdutosInternos(callback);
    }

    private void buscaProdutosInternos(DadosCarregadosCallBack<List<Produto>> callback) {
        // atualização INTERNA
        new BaseAsyncTask<>(dao::buscaTodos,
                resultado -> {
                    // notifica que o dado esta pronto
                    callback.quandoSucesso(resultado);
                    buscaProdutosNaAPI(callback);
                }).execute();
    }

    private void buscaProdutosNaAPI(DadosCarregadosCallBack<List<Produto>> callback) {
        //ProdutoService service = new EstoqueRetrofit().getProdutoService();
        Call<List<Produto>> call = service.buscaTodos();

        call.enqueue(new CallbackComRetorno<>(new CallbackComRetorno.RespostaCallBack<List<Produto>>() {
            @Override
            @EverythingIsNonNull
            public void quandoSucesso(List<Produto> produtosNovos) {
                atualizaInterna(produtosNovos,callback);
            }

            @Override
            @EverythingIsNonNull
            public void quandoFalha(String erro) {
                callback.quandoFalha(erro);
            }
        }));
    }

    private void atualizaInterna(List<Produto> produtos,
                                 DadosCarregadosCallBack<List<Produto>> callback) {
        new BaseAsyncTask<>(() ->{
            dao.salva(produtos);
            return dao.buscaTodos();
        }, callback::quandoSucesso)
                .execute();
    }

    public void salva(Produto produto,
                      DadosCarregadosCallBack<Produto> callback) {
        salvaNaAPI(produto, callback);
    }

    private void salvaNaAPI(Produto produto,
                            DadosCarregadosCallBack<Produto> callback) {
        Call<Produto> call = service.salva(produto);
        call.enqueue(new CallbackComRetorno<>(new CallbackComRetorno.RespostaCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoSalvo) {
                salvaInterno(produtoSalvo,callback);
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha(erro);
            }
        }));
    }

    private void salvaInterno(Produto produto,
                              DadosCarregadosCallBack<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produto);
            return dao.buscaProduto(id);
        }, callback::quandoSucesso)
                .execute();
    }

    public void edita(Produto produto,
                      DadosCarregadosCallBack<Produto> callBack) {

        editaNaAPI(produto, callBack);

    }

    private void editaNaAPI(Produto produto,
                            DadosCarregadosCallBack<Produto> callBack) {
        Call<Produto> call = service.edita(produto.getId(), produto);
        call.enqueue(new CallbackComRetorno<>(new CallbackComRetorno.RespostaCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {
                editaInterno(produto, callBack);
            }

            @Override
            public void quandoFalha(String erro) {
                callBack.quandoFalha(erro);
            }
        }));
    }

    private void editaInterno(Produto produto,
                              DadosCarregadosCallBack<Produto> callBack) {
        new BaseAsyncTask<>(() -> {
            dao.atualiza(produto);
            return produto;
        }, callBack::quandoSucesso).execute();
    }

    public void remove(Produto produto, DadosCarregadosCallBack<Void> callBack) {

        removeNaAPI(produto, callBack);


    }

    private void removeNaAPI(Produto produto, DadosCarregadosCallBack<Void> callBack) {
        Call<Void> call = service.remove(produto.getId());
        call.enqueue(new CallbackSemRetorno(new CallbackSemRetorno.RespostaCallBack() {
            @Override
            public void quandoSucesso() {
                removeInterno(produto, callBack);
            }

            @Override
            public void quandoFalha(String erro) {
                callBack.quandoFalha(erro);
            }
        }));
    }

    private void removeInterno(Produto produto,
                               DadosCarregadosCallBack<Void> callBack) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produto);
            return null;
        },callBack::quandoSucesso)
                .execute();
    }


    public interface DadosCarregadosCallBack <T>{
       void quandoSucesso(T resultado);
       void quandoFalha(String erro);
    }

}
