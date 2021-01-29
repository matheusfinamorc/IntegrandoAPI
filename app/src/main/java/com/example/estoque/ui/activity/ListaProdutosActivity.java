package com.example.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.estoque.R;
import com.example.estoque.model.Produto;
import com.example.estoque.repository.ProdutoRepository;
import com.example.estoque.ui.dialog.EditaProdutoDialog;
import com.example.estoque.ui.dialog.SalvaProdutoDialog;
import com.example.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String MENSAGEM_ERRO_BUSCA_PRODUTOS = "Não foi possivel carregar os produtos novos";
    private static final String MENSAGEM_ERRO_REMOCAO = "Não foi possivel remover o produto";
    private static final String MENSAGEM_ERRO_SALVA = "Não foi possivel salvar o produto";
    private static final String MENSAGEM_ERRO_EDICAO = "Não foi possivel retirar o produto";
    private ListaProdutosAdapter adapter;
    private ProdutoRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        repository = new ProdutoRepository(this);
        buscaProdutos();
    }

    private void buscaProdutos() {
        repository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallBack<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> produtosNovos) {
                adapter.atualiza(produtosNovos);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraMensagemErro(MENSAGEM_ERRO_BUSCA_PRODUTOS, Toast.LENGTH_LONG);
            }
        });
    }

    private void mostraMensagemErro(String mensagem, int lengthLong) {
        Toast.makeText(this, mensagem,
                lengthLong).show();
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(
                (posicao, produtoEscolhido) -> repository.remove(produtoEscolhido,
                        new ProdutoRepository.DadosCarregadosCallBack<Void>() {
                            @Override
                            public void quandoSucesso(Void resultado) {
                                adapter.remove(posicao);
                            }

                            @Override
                            public void quandoFalha(String erro) {
                                mostraMensagemErro(MENSAGEM_ERRO_REMOCAO, Toast.LENGTH_LONG);
                            }
                        }));
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, produtoCriado -> {
            salva(produtoCriado);
        }).mostra();
    }

    private void salva(Produto produtoCriado) {
        repository.salva(produtoCriado, new ProdutoRepository.DadosCarregadosCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoSalvo) {
                adapter.adiciona(produtoSalvo);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraMensagemErro(MENSAGEM_ERRO_SALVA, Toast.LENGTH_SHORT);
            }
        });
    }


    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoCriado -> edita(posicao, produtoCriado))
                .mostra();
    }

    private void edita(int posicao, Produto produtoCriado) {
        repository.edita(produtoCriado, new ProdutoRepository.DadosCarregadosCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoEditado) {
                adapter.edita(posicao, produtoEditado);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraMensagemErro(MENSAGEM_ERRO_EDICAO, Toast.LENGTH_LONG);
            }
        });
    }

}
