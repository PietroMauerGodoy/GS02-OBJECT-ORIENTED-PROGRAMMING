package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.FonteIndisponivelException;

import java.util.Random;

public class SensorChuva implements Sensor {

    private static final double CHUVA_MAXIMA_MM_H = 80.0;

    private final String id;
    private final Random random = new Random();
    private boolean funcionando = true;

    public SensorChuva(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTipo() {
        return "CHUVA_MM_H";
    }

    @Override
    public boolean verificarFuncionamento() {
        return funcionando;
    }

    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }

    @Override
    public double lerValor() {
        if (!funcionando) {
            throw new FonteIndisponivelException("O sensor de chuva " + id + " está fora de operação.");
        }
        double valor = random.nextDouble() * CHUVA_MAXIMA_MM_H;
        return Math.round(valor * 10.0) / 10.0;
    }
}