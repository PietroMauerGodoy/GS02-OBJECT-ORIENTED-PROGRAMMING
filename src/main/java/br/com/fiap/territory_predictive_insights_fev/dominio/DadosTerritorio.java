package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights_fev.servico.ConfiguracaoSistema;

public class DadosTerritorio {

    private String nomeRegiao;
    private int populacao;

    public DadosTerritorio(String nomeRegiao, int populacao) {
        setNomeRegiao(nomeRegiao);
        setPopulacao(populacao);
    }

    public String getNomeRegiao() {
        return nomeRegiao;
    }

    public void setNomeRegiao(String nomeRegiao) {
        if (nomeRegiao == null || nomeRegiao.isBlank()) {
            throw new DadosInvalidosException("O nome da região não pode ser vazio.");
        }
        this.nomeRegiao = nomeRegiao;
    }

    public int getPopulacao() {
        return populacao;
    }

    public void setPopulacao(int populacao) {
        if (populacao < 0) {
            throw new DadosInvalidosException("A população não pode ser negativa (recebido: " + populacao + ").");
        }
        this.populacao = populacao;
    }

    public double getLimiarAtencao() {
        return ConfiguracaoSistema.getInstancia().getLimiarAtencao();
    }

    public double getLimiarAlerta() {
        return ConfiguracaoSistema.getInstancia().getLimiarAlerta();
    }

    public double getLimiarCritico() {
        return ConfiguracaoSistema.getInstancia().getLimiarCritico();
    }

    // A validação (atenção < alerta < crítico) acontece na ConfiguracaoSistema
    public void alterarLimiares(double atencao, double alerta, double critico) {
        ConfiguracaoSistema.getInstancia().definirLimiares(atencao, alerta, critico);
    }
}