package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorChuva;
import br.com.fiap.territory_predictive_insights_fev.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TerritoryPredictiveInsightsFevApplication {

	public static void main(String[] args) {
		SpringApplication.run(TerritoryPredictiveInsightsFevApplication.class, args);
	}

	// TEMPORÁRIO: só pra testar a persistência
	@Bean
	CommandLineRunner teste(ServicoMonitoramento servico) {
		return args -> {
			SensorChuva sensor = new SensorChuva("PLUV-01");
			servico.registrarLeitura(sensor);

			System.out.println("=== Histórico do sensor PLUV-01 ===");
			for (Medicao m : servico.buscarHistoricoPorSensor("PLUV-01")) {
				System.out.println(m.getDataHora() + " | " + m.getTipoSensor() + " | " + m.getValor());
			}
		};
	}
}