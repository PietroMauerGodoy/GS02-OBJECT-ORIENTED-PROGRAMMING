package br.com.fiap.territory_predictive_insights_fev.excecao;

public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}