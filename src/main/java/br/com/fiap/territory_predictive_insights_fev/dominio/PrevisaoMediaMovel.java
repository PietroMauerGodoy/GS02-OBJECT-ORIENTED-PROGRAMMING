package br.com.fiap.territory_predictive_insights_fev.dominio;

import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Média móvel", janela);
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);

        double soma = 0;
        for (int i = 0; i < janela; i++) {
            soma += historico.get(i).getValor();
        }
        return soma / janela;
    }
}