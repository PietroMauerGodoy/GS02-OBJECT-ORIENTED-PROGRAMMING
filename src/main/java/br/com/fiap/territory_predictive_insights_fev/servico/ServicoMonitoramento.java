package br.com.fiap.territory_predictive_insights_fev.servico;

import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.Sensor;
import br.com.fiap.territory_predictive_insights_fev.persistencia.MedicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;

    public ServicoMonitoramento(MedicaoRepository medicaoRepository) {
        this.medicaoRepository = medicaoRepository;
    }

    // Lê o sensor, valida (via construtor de Medicao) e grava no banco
    public Medicao registrarLeitura(Sensor sensor) {
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor);
        return medicaoRepository.save(medicao);
    }

    public List<Medicao> buscarHistoricoPorSensor(String sensorId) {
        return medicaoRepository.findBySensorIdOrderByDataHoraDesc(sensorId);
    }
}