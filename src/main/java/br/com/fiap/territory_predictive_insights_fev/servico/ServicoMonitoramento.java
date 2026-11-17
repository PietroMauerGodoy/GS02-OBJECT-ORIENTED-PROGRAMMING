package br.com.fiap.territory_predictive_insights_fev.servico;

import br.com.fiap.territory_predictive_insights_fev.dominio.Alerta;
import br.com.fiap.territory_predictive_insights_fev.dominio.EstrategiaPrevisao;
import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.NivelAlerta;
import br.com.fiap.territory_predictive_insights_fev.dominio.Sensor;
import br.com.fiap.territory_predictive_insights_fev.persistencia.AlertaRepository;
import br.com.fiap.territory_predictive_insights_fev.persistencia.MedicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;

    public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
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

    public List<Alerta> listarAlertas() {
        return alertaRepository.findAllByOrderByDataHoraDesc();
    }

    // Roda a previsão sobre o histórico do banco e emite alerta se passar de algum limiar.
    // Retorna vazio quando a previsão fica abaixo de todos os limiares.
    public Optional<Alerta> avaliarPrevisao(String sensorId, String tipoSensor, EstrategiaPrevisao estrategia) {
        List<Medicao> historico = buscarHistoricoPorSensor(sensorId);
        double previsto = estrategia.prever(historico);

        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        NivelAlerta nivel;
        double limite;
        if (previsto >= config.getLimiarCritico()) {
            nivel = NivelAlerta.CRITICO;
            limite = config.getLimiarCritico();
        } else if (previsto >= config.getLimiarAlerta()) {
            nivel = NivelAlerta.ALERTA;
            limite = config.getLimiarAlerta();
        } else if (previsto >= config.getLimiarAtencao()) {
            nivel = NivelAlerta.ATENCAO;
            limite = config.getLimiarAtencao();
        } else {
            return Optional.empty();
        }

        String justificativa = String.format(
                "Sensor %s: a estratégia '%s' previu %.1f (limite %s: %.1f), com base em %d leituras do histórico.",
                sensorId, estrategia.getNome(), previsto, nivel, limite, historico.size());

        Alerta alerta = new Alerta(sensorId, tipoSensor, nivel, previsto, limite, estrategia.getNome(), justificativa);
        return Optional.of(alertaRepository.save(alerta));
    }
}