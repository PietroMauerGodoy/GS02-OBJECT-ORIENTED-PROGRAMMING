package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String tipoSensor;

    @Enumerated(EnumType.STRING)
    private NivelAlerta nivel;

    private double valorPrevisto;
    private double limiteUltrapassado;
    private String estrategia;

    @Column(length = 500)
    private String justificativa;

    private LocalDateTime dataHora;

    // Exigido pelo JPA
    protected Alerta() {
    }

    public Alerta(String sensorId, String tipoSensor, NivelAlerta nivel, double valorPrevisto,
                  double limiteUltrapassado, String estrategia, String justificativa) {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("O alerta precisa ter o id do sensor.");
        }
        if (nivel == null) {
            throw new DadosInvalidosException("O alerta precisa ter um nível.");
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new DadosInvalidosException("O alerta precisa ter uma justificativa.");
        }
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiteUltrapassado = limiteUltrapassado;
        this.estrategia = estrategia;
        this.justificativa = justificativa;
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

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimiteUltrapassado() {
        return limiteUltrapassado;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}