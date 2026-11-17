package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.PrevisaoComparacao;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrevisaoComparacaoTest {

    private Medicao leitura(double valor) {
        return new Medicao("PLUV-01", "CHUVA_MM_H", valor);
    }

    @Test
    void tendenciaDeAlta_projetaValorMaiorQueARecente() {
        // Arrange: recente = média(30, 30) = 30 | anterior = média(10, 10) = 10
        List<Medicao> historico = List.of(leitura(30), leitura(30), leitura(10), leitura(10));
        PrevisaoComparacao estrategia = new PrevisaoComparacao(2);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: 30 + (30 - 10) = 50
        assertEquals(50.0, previsto, 0.0001);
    }

    @Test
    void tendenciaDeQueda_naoPrevValorNegativo() {
        // Arrange: recente = 0 | anterior = 50, então a tendência seria -50
        List<Medicao> historico = List.of(leitura(0), leitura(0), leitura(50), leitura(50));
        PrevisaoComparacao estrategia = new PrevisaoComparacao(2);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: chuva não pode ser negativa, então o resultado trava em zero
        assertEquals(0.0, previsto, 0.0001);
    }
}