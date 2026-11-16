package br.com.fiap.territory_predictive_insights_fev.dominio;

public interface Sensor {

    String getId();

    String getTipo();

    boolean verificarFuncionamento();

    // Lança FonteIndisponivelException se o sensor estiver fora de operação
    double lerValor();
}