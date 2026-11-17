package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.Alerta;
import br.com.fiap.territory_predictive_insights_fev.dominio.EstrategiaPrevisao;
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
import java.util.Optional;

@SpringBootApplication
public class TerritoryPredictiveInsightsFevApplication {

	public static void main(String[] args) {
		SpringApplication.run(TerritoryPredictiveInsightsFevApplication.class, args);
	}

	// TEMPORÁRIO: só pra testar persistência, previsões e alertas
	@Bean
	CommandLineRunner teste(ServicoMonitoramento servico) {
		return args -> {
			SensorChuva sensor = new SensorChuva("PLUV-01");
			for (int i = 0; i < 6; i++) {
				servico.registrarLeitura(sensor);
			}

			List<EstrategiaPrevisao> estrategias = List.of(
					new PrevisaoMediaMovel(3),
					new PrevisaoComparacao(3));

			for (EstrategiaPrevisao estrategia : estrategias) {
				try {
					Optional<Alerta> alerta = servico.avaliarPrevisao("PLUV-01", sensor.getTipo(), estrategia);
					if (alerta.isPresent()) {
						System.out.println("[" + alerta.get().getNivel() + "] " + alerta.get().getJustificativa());
					} else {
						System.out.println(estrategia.getNome() + ": sem alerta.");
					}
				} catch (DadosInvalidosException e) {
					System.out.println("Erro: " + e.getMessage());
				}
			}

			System.out.println("Alertas gravados no banco: " + servico.listarAlertas().size());
		};
	}
}