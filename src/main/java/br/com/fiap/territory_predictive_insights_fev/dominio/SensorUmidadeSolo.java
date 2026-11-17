package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.FonteIndisponivelException;

import java.util.Random;

public class SensorUmidadeSolo implements Sensor {

    private static final double UMIDADE_MAXIMA_PCT = 100.0;

    private final String id;
    private final Random random = new Random();
    private boolean funcionando = true;

    public SensorUmidadeSolo(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTipo() {
        return "UMIDADE_SOLO_PCT";
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
            throw new FonteIndisponivelException("O sensor de umidade do solo " + id + " está fora de operação.");
        }
        double valor = random.nextDouble() * UMIDADE_MAXIMA_PCT;
        return Math.round(valor * 10.0) / 10.0;
    }
}