package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.FonteIndisponivelException;

import java.util.Random;

public class SensorNivelRio implements Sensor {

    private static final double NIVEL_MAXIMO_CM = 500.0;

    private final String id;
    private final Random random = new Random();
    private boolean funcionando = true;

    public SensorNivelRio(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTipo() {
        return "NIVEL_RIO_CM";
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
            throw new FonteIndisponivelException("O sensor de nível do rio " + id + " está fora de operação.");
        }
        double valor = random.nextDouble() * NIVEL_MAXIMO_CM;
        return Math.round(valor * 10.0) / 10.0;
    }
}