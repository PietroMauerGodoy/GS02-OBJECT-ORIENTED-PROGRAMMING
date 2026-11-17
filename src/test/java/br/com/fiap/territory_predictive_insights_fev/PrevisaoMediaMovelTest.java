package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrevisaoMediaMovelTest {

    private Medicao leitura(double valor) {
        return new Medicao("PLUV-01", "CHUVA_MM_H", valor);
    }

    @Test
    void prever_calculaMediaDasUltimasLeituras() {
        // Arrange: histórico do mais recente (10) para o mais antigo (100)
        List<Medicao> historico = List.of(leitura(10), leitura(20), leitura(30), leitura(100));
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: só as 3 mais recentes entram, a de 100 fica de fora
        assertEquals(20.0, previsto, 0.0001);
    }

    @Test
    void historicoInsuficiente_lancaDadosInvalidosException() {
        // Arrange
        List<Medicao> historico = List.of(leitura(10), leitura(20));
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);

        // Act
        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class,
                () -> estrategia.prever(historico));

        // Assert
        assertTrue(excecao.getMessage().contains("Histórico insuficiente"));
    }
}