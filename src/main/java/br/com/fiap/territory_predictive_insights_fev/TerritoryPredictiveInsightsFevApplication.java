package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.EstrategiaPrevisao;
import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.PrevisaoComparacao;
import br.com.fiap.territory_predictive_insights_fev.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorChuva;
import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights_fev.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class TerritoryPredictiveInsightsFevApplication {

	public static void main(String[] args) {
		SpringApplication.run(TerritoryPredictiveInsightsFevApplication.class, args);
	}

	// TEMPORÁRIO: só pra testar persistência e previsões
	@Bean
	CommandLineRunner teste(ServicoMonitoramento servico) {
		return args -> {
			SensorChuva sensor = new SensorChuva("PLUV-01");
			for (int i = 0; i < 6; i++) {
				servico.registrarLeitura(sensor);
			}

			List<Medicao> historico = servico.buscarHistoricoPorSensor("PLUV-01");
			System.out.println("Total de medições no banco: " + historico.size());

			List<EstrategiaPrevisao> estrategias = List.of(
					new PrevisaoMediaMovel(3),
					new PrevisaoComparacao(3));

			for (EstrategiaPrevisao estrategia : estrategias) {
				try {
					double previsto = estrategia.prever(historico);
					System.out.printf("%s -> %.1f mm/h%n", estrategia.getNome(), previsto);
				} catch (DadosInvalidosException e) {
					System.out.println("Erro: " + e.getMessage());
				}
			}
		};
	}
}