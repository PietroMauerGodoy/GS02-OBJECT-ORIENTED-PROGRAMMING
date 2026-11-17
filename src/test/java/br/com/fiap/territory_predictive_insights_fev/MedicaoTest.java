package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicaoTest {

    @Test
    void criarMedicaoValida_guardaOsValoresInformados() {
        // Arrange
        String sensorId = "PLUV-01";
        String tipo = "CHUVA_MM_H";
        double valor = 25.5;

        // Act
        Medicao medicao = new Medicao(sensorId, tipo, valor);

        // Assert
        assertEquals("PLUV-01", medicao.getSensorId());
        assertEquals("CHUVA_MM_H", medicao.getTipoSensor());
        assertEquals(25.5, medicao.getValor(), 0.0001);
        assertNotNull(medicao.getDataHora());
    }

    @Test
    void valorNegativo_lancaDadosInvalidosException() {
        // Arrange
        double valorNegativo = -3.0;

        // Act
        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLUV-01", "CHUVA_MM_H", valorNegativo));

        // Assert
        assertTrue(excecao.getMessage().contains("negativa"));
    }

    @Test
    void sensorIdVazio_lancaDadosInvalidosException() {
        // Arrange
        String sensorIdVazio = "  ";

        // Act
        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class,
                () -> new Medicao(sensorIdVazio, "CHUVA_MM_H", 10.0));

        // Assert
        assertTrue(excecao.getMessage().contains("id do sensor"));
    }
}