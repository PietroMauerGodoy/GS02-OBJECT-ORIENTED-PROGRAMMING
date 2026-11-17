package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.ComponenteTerritorial;
import br.com.fiap.territory_predictive_insights_fev.dominio.DadosTerritorio;
import br.com.fiap.territory_predictive_insights_fev.dominio.PontoMonitoramento;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorChuva;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorNivelRio;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorUmidadeSolo;
import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights_fev.excecao.FonteIndisponivelException;
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

    // TEMPORÁRIO: só pra testar componentes, sensores e dados do território
    @Bean
    CommandLineRunner teste() {
        return args -> {
            SensorChuva chuva = new SensorChuva("PLUV-01");
            SensorNivelRio rio = new SensorNivelRio("RIO-01");
            SensorUmidadeSolo solo = new SensorUmidadeSolo("SOLO-01");

            List<ComponenteTerritorial> pontos = List.of(
                    new PontoMonitoramento("P1", "Pluviômetro da praça", "Zona Sul", chuva),
                    new PontoMonitoramento("P2", "Régua do córrego", "Zona Sul", rio),
                    new PontoMonitoramento("P3", "Sonda da encosta", "Zona Sul", solo));

            rio.setFuncionando(false);

            for (ComponenteTerritorial p : pontos) {
                System.out.println(p.getNome() + " -> " + p.verificarStatus());
            }

            try {
                rio.lerValor();
            } catch (FonteIndisponivelException e) {
                System.out.println("Erro: " + e.getMessage());
            }

            DadosTerritorio territorio = new DadosTerritorio("Zona Sul", 12000);
            try {
                territorio.setPopulacao(-5);
            } catch (DadosInvalidosException e) {
                System.out.println("Erro: " + e.getMessage());
            }
            try {
                territorio.alterarLimiares(50, 30, 70);
            } catch (DadosInvalidosException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        };
    }
}