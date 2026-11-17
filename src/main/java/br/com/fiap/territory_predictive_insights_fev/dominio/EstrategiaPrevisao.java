package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;

import java.util.List;

public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nome, int minimoLeituras) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("A estratégia de previsão precisa ter um nome.");
        }
        if (minimoLeituras < 1) {
            throw new DadosInvalidosException("O mínimo de leituras deve ser pelo menos 1.");
        }
        this.nome = nome;
        this.minimoLeituras = minimoLeituras;
    }

    public String getNome() {
        return nome;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

    // O histórico vem ordenado da leitura MAIS RECENTE para a mais antiga
    public abstract double prever(List<Medicao> historico);

    protected void validarHistorico(List<Medicao> historico) {
        if (historico == null || historico.size() < minimoLeituras) {
            int atual = historico == null ? 0 : historico.size();
            throw new DadosInvalidosException(
                    "Histórico insuficiente para a estratégia " + nome + ": são necessárias "
                            + minimoLeituras + " leituras e existem " + atual + ".");
        }
    }
}