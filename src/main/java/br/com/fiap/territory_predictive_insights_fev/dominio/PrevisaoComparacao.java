package br.com.fiap.territory_predictive_insights_fev.dominio;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoComparacao(int janela) {
        super("Comparação com período anterior", janela * 2);
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);

        double mediaRecente = media(historico, 0, janela);
        double mediaAnterior = media(historico, janela, janela * 2);

        // Projeta a tendência: se subiu entre os períodos, assume que continua subindo
        double tendencia = mediaRecente - mediaAnterior;
        return Math.max(0, mediaRecente + tendencia);
    }

    private double media(List<Medicao> historico, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += historico.get(i).getValor();
        }
        return soma / (fim - inicio);
    }
}