package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.Alerta;
import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.NivelAlerta;
import br.com.fiap.territory_predictive_insights_fev.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory_predictive_insights_fev.dominio.Sensor;
import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights_fev.excecao.FonteIndisponivelException;
import br.com.fiap.territory_predictive_insights_fev.persistencia.AlertaRepository;
import br.com.fiap.territory_predictive_insights_fev.persistencia.MedicaoRepository;
import br.com.fiap.territory_predictive_insights_fev.servico.ConfiguracaoSistema;
import br.com.fiap.territory_predictive_insights_fev.servico.ServicoMonitoramento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServicoMonitoramentoTest {

    private MedicaoRepository medicaoRepository;
    private AlertaRepository alertaRepository;
    private ServicoMonitoramento servico;

    @BeforeEach
    void preparar() {
        medicaoRepository = mock(MedicaoRepository.class);
        alertaRepository = mock(AlertaRepository.class);
        servico = new ServicoMonitoramento(medicaoRepository, alertaRepository);

        // O Singleton é global: volta aos limiares padrão antes de cada teste
        ConfiguracaoSistema.getInstancia().definirLimiares(20.0, 40.0, 60.0);

        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(medicaoRepository.save(any(Medicao.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void historicoDoPluviometro(double... valores) {
        List<Medicao> historico = new ArrayList<>();
        for (double valor : valores) {
            historico.add(new Medicao("PLUV-01", "CHUVA_MM_H", valor));
        }
        when(medicaoRepository.findBySensorIdOrderByDataHoraDesc("PLUV-01")).thenReturn(historico);
    }

    @Test
    void previsaoAcimaDoLimiarCritico_emiteAlertaCritico() {
        // Arrange: média das 3 leituras = 80, acima do crítico (60)
        historicoDoPluviometro(70, 80, 90);

        // Act
        Optional<Alerta> alerta = servico.avaliarPrevisao("PLUV-01", "CHUVA_MM_H", new PrevisaoMediaMovel(3));

        // Assert
        assertTrue(alerta.isPresent());
        assertEquals(NivelAlerta.CRITICO, alerta.get().getNivel());
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    void previsaoEntreAlertaECritico_emiteAlertaNivelAlerta() {
        // Arrange: média = 45, entre alerta (40) e crítico (60)
        historicoDoPluviometro(45, 45, 45);

        // Act
        Optional<Alerta> alerta = servico.avaliarPrevisao("PLUV-01", "CHUVA_MM_H", new PrevisaoMediaMovel(3));

        // Assert
        assertTrue(alerta.isPresent());
        assertEquals(NivelAlerta.ALERTA, alerta.get().getNivel());
    }

    @Test
    void previsaoAbaixoDosLimiares_naoEmiteAlerta() {
        // Arrange: média = 5, abaixo de atenção (20)
        historicoDoPluviometro(5, 5, 5);

        // Act
        Optional<Alerta> alerta = servico.avaliarPrevisao("PLUV-01", "CHUVA_MM_H", new PrevisaoMediaMovel(3));

        // Assert
        assertTrue(alerta.isEmpty());
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void historicoInsuficiente_lancaExcecaoESemGravarAlerta() {
        // Arrange: só 2 leituras para uma janela de 3
        historicoDoPluviometro(70, 80);

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> servico.avaliarPrevisao("PLUV-01", "CHUVA_MM_H", new PrevisaoMediaMovel(3)));
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void registrarLeitura_gravaMedicaoNoRepositorio() {
        // Arrange
        Sensor sensor = mock(Sensor.class);
        when(sensor.getId()).thenReturn("PLUV-01");
        when(sensor.getTipo()).thenReturn("CHUVA_MM_H");
        when(sensor.lerValor()).thenReturn(12.5);

        // Act
        Medicao medicao = servico.registrarLeitura(sensor);

        // Assert
        assertEquals(12.5, medicao.getValor(), 0.0001);
        verify(medicaoRepository).save(any(Medicao.class));
    }

    @Test
    void sensorForaDeOperacao_lancaExcecaoESemGravarMedicao() {
        // Arrange
        Sensor sensor = mock(Sensor.class);
        when(sensor.lerValor()).thenThrow(new FonteIndisponivelException("Sensor fora de operação."));

        // Act + Assert
        assertThrows(FonteIndisponivelException.class, () -> servico.registrarLeitura(sensor));
        verify(medicaoRepository, never()).save(any(Medicao.class));
    }
}