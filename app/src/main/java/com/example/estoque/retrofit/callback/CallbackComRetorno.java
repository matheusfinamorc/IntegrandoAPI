package com.example.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackComRetorno<T>  implements Callback {

    public static final String RESPOSTA_ERRO_RESPOSTA_NAO_SUCEDIDA = "Resposta não sucedida";
    public static final String MENSAGEM_ERRO_FALHA_COMUNICACAO = "Falha de comunicação:";
    private final RespostaCallBack<T> callBack;

    public CallbackComRetorno(RespostaCallBack<T> callBack) {
        this.callBack = callBack;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call call, Response response) {
            if(response.isSuccessful()){
                T resultado = (T) response.body();
                if(resultado != null){
                    // notifica que tem resposta com sucesso
                    callBack.quandoSucesso(resultado);
                }
            }else{
                // notifica falha
                callBack.quandoFalha(RESPOSTA_ERRO_RESPOSTA_NAO_SUCEDIDA);
            }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call call, Throwable t) {
        // notifica falha
        callBack.quandoFalha(MENSAGEM_ERRO_FALHA_COMUNICACAO + t.getMessage());

    }
    public interface RespostaCallBack <T>{
        void quandoSucesso(T resultado);
        void quandoFalha(String erro);
    }
}
