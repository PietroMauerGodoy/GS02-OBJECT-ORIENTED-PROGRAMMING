package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String tipoSensor;
    private double valor;
    private LocalDateTime dataHora;

    // Exigido pelo JPA
    protected Medicao() {
    }

    public Medicao(String sensorId, String tipoSensor, double valor) {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("A medição precisa ter o id do sensor.");
        }
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("A medição precisa ter o tipo do sensor.");
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadosInvalidosException("Valor de medição inválido: " + valor);
        }
        if (valor < 0) {
            throw new DadosInvalidosException(
                    "Medição negativa não é permitida (" + tipoSensor + ": " + valor + ").");
        }
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}