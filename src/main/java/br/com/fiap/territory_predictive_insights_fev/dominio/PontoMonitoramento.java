package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;

public class PontoMonitoramento extends ComponenteTerritorial {

    private final Sensor sensor;

    public PontoMonitoramento(String id, String nome, String regiao, Sensor sensor) {
        super(id, nome, regiao);
        if (sensor == null) {
            throw new DadosInvalidosException("O ponto de monitoramento precisa ter um sensor.");
        }
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }

    @Override
    public String verificarStatus() {
        setStatus(sensor.verificarFuncionamento() ? "OPERACIONAL" : "FORA_DE_OPERACAO");
        return getStatus();
    }
}