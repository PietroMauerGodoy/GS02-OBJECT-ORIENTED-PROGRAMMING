package br.com.fiap.territory_predictive_insights_fev;

import br.com.fiap.territory_predictive_insights_fev.dominio.Alerta;
import br.com.fiap.territory_predictive_insights_fev.dominio.DadosTerritorio;
import br.com.fiap.territory_predictive_insights_fev.dominio.EstrategiaPrevisao;
import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import br.com.fiap.territory_predictive_insights_fev.dominio.PontoMonitoramento;
import br.com.fiap.territory_predictive_insights_fev.dominio.PrevisaoComparacao;
import br.com.fiap.territory_predictive_insights_fev.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorChuva;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorNivelRio;
import br.com.fiap.territory_predictive_insights_fev.dominio.SensorUmidadeSolo;
import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights_fev.excecao.FonteIndisponivelException;
import br.com.fiap.territory_predictive_insights_fev.servico.ConfiguracaoSistema;
import br.com.fiap.territory_predictive_insights_fev.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class SistemaMonitoramento implements CommandLineRunner {

    private final ServicoMonitoramento servico;
    private final Scanner scanner = new Scanner(System.in);

    private final SensorChuva sensorChuva = new SensorChuva("PLUV-01");
    private final SensorNivelRio sensorRio = new SensorNivelRio("RIO-01");
    private final SensorUmidadeSolo sensorSolo = new SensorUmidadeSolo("SOLO-01");

    private final DadosTerritorio territorio = new DadosTerritorio("Zona Sul", 12000);
    private final List<PontoMonitoramento> pontos;
    private final List<EstrategiaPrevisao> estrategias =
            List.of(new PrevisaoMediaMovel(3), new PrevisaoComparacao(3));

    public SistemaMonitoramento(ServicoMonitoramento servico) {
        this.servico = servico;
        this.pontos = List.of(
                new PontoMonitoramento("P1", "Pluviômetro da praça", territorio.getNomeRegiao(), sensorChuva),
                new PontoMonitoramento("P2", "Régua do córrego", territorio.getNomeRegiao(), sensorRio),
                new PontoMonitoramento("P3", "Sonda da encosta", territorio.getNomeRegiao(), sensorSolo));
    }

    @Override
    public void run(String... args) {
        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            try {
                int opcao = lerInteiro("Escolha uma opção: ");
                switch (opcao) {
                    case 1 -> verificarSensores();
                    case 2 -> rodarPrevisoes();
                    case 3 -> verHistorico();
                    case 4 -> gerenciarTerritorio();
                    case 5 -> statusCompleto();
                    case 6 -> simularFalhaSensor();
                    case 0 -> rodando = false;
                    default -> System.out.println("Opção inexistente. Escolha um número do menu.");
                }
            } catch (DadosInvalidosException | FonteIndisponivelException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        System.out.println("Sistema encerrado. Os dados continuam salvos no banco.");
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("===== MONITORAMENTO TERRITORIAL: " + territorio.getNomeRegiao() + " =====");
        System.out.println("1 - Verificar sensores (ler e gravar)");
        System.out.println("2 - Rodar previsões de chuva");
        System.out.println("3 - Ver histórico");
        System.out.println("4 - Gerenciar dados do território");
        System.out.println("5 - Status completo");
        System.out.println("6 - Simular falha ou recuperação de sensor");
        System.out.println("0 - Sair");
    }

    // 1 - Verificar sensores
    private void verificarSensores() {
        for (PontoMonitoramento ponto : pontos) {
            String status = ponto.verificarStatus();
            try {
                Medicao medicao = servico.registrarLeitura(ponto.getSensor());
                System.out.printf("%s [%s]: %.1f (%s)%n", ponto.getNome(), status,
                        medicao.getValor(), medicao.getTipoSensor());
            } catch (FonteIndisponivelException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        }
    }

    // 2 - Previsões e alertas (só para chuva, pois os limiares são em mm/h)
    private void rodarPrevisoes() {
        for (EstrategiaPrevisao estrategia : estrategias) {
            try {
                List<Medicao> historico = servico.buscarHistoricoPorSensor(sensorChuva.getId());
                double previsto = estrategia.prever(historico);
                System.out.printf("%n[%s] Previsão de chuva: %.1f mm/h%n", estrategia.getNome(), previsto);

                Optional<Alerta> alerta = servico.avaliarPrevisao(
                        sensorChuva.getId(), sensorChuva.getTipo(), estrategia);
                if (alerta.isPresent()) {
                    System.out.println("  " + alerta.get().getNivel() + ": " + alerta.get().getJustificativa());
                    System.out.println("  Este alerta apoia a decisão; a ação é de responsabilidade do gestor.");
                } else {
                    System.out.println("  Abaixo dos limiares: nenhum alerta emitido.");
                }
            } catch (DadosInvalidosException e) {
                System.out.println("[" + estrategia.getNome() + "] Sem previsão: " + e.getMessage());
            }
        }
    }

    // 3 - Histórico
    private void verHistorico() {
        System.out.println("1 - Medições por sensor");
        System.out.println("2 - Alertas emitidos");
        int opcao = lerInteiro("Escolha: ");
        if (opcao == 1) {
            System.out.println("Sensores: 1 - PLUV-01 | 2 - RIO-01 | 3 - SOLO-01");
            int sensor = lerInteiro("Escolha o sensor: ");
            String sensorId = switch (sensor) {
                case 1 -> sensorChuva.getId();
                case 2 -> sensorRio.getId();
                case 3 -> sensorSolo.getId();
                default -> throw new DadosInvalidosException("Sensor inexistente: " + sensor);
            };
            List<Medicao> medicoes = servico.buscarHistoricoPorSensor(sensorId);
            if (medicoes.isEmpty()) {
                System.out.println("Nenhuma medição gravada para " + sensorId + ".");
            }
            for (Medicao m : medicoes) {
                System.out.printf("%s | %s | %.1f%n", m.getDataHora(), m.getTipoSensor(), m.getValor());
            }
        } else if (opcao == 2) {
            List<Alerta> alertas = servico.listarAlertas();
            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta emitido até agora.");
            }
            for (Alerta a : alertas) {
                System.out.println(a.getDataHora() + " | " + a.getNivel() + " | " + a.getJustificativa());
            }
        } else {
            System.out.println("Opção inexistente.");
        }
    }

    // 4 - Dados do território
    private void gerenciarTerritorio() {
        System.out.println("Região: " + territorio.getNomeRegiao() + " | População: " + territorio.getPopulacao());
        System.out.printf("Limiares de chuva (mm/h): atenção %.1f | alerta %.1f | crítico %.1f%n",
                territorio.getLimiarAtencao(), territorio.getLimiarAlerta(), territorio.getLimiarCritico());
        System.out.println("1 - Alterar população");
        System.out.println("2 - Alterar limiares");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Escolha: ");
        if (opcao == 1) {
            territorio.setPopulacao(lerInteiro("Nova população: "));
            System.out.println("População atualizada.");
        } else if (opcao == 2) {
            double atencao = lerDecimal("Novo limiar de atenção: ");
            double alerta = lerDecimal("Novo limiar de alerta: ");
            double critico = lerDecimal("Novo limiar crítico: ");
            territorio.alterarLimiares(atencao, alerta, critico);
            System.out.println("Limiares atualizados.");
        }
    }

    // 5 - Status completo
    private void statusCompleto() {
        System.out.println("--- Pontos de monitoramento ---");
        for (PontoMonitoramento ponto : pontos) {
            String sensorId = ponto.getSensor().getId();
            System.out.printf("%s (%s) | %s | %d medições gravadas%n", ponto.getNome(), sensorId,
                    ponto.verificarStatus(), servico.buscarHistoricoPorSensor(sensorId).size());
        }
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        System.out.printf("--- Limiares de chuva: %.1f / %.1f / %.1f mm/h%n",
                config.getLimiarAtencao(), config.getLimiarAlerta(), config.getLimiarCritico());
        System.out.println("--- Alertas emitidos: " + servico.listarAlertas().size());
        System.out.println("--- População da região: " + territorio.getPopulacao());
    }

    // 6 - Simular falha
    private void simularFalhaSensor() {
        System.out.println("Sensores: 1 - PLUV-01 | 2 - RIO-01 | 3 - SOLO-01");
        int opcao = lerInteiro("Qual sensor alternar entre funcionando e com falha? ");
        switch (opcao) {
            case 1 -> sensorChuva.setFuncionando(!sensorChuva.verificarFuncionamento());
            case 2 -> sensorRio.setFuncionando(!sensorRio.verificarFuncionamento());
            case 3 -> sensorSolo.setFuncionando(!sensorSolo.verificarFuncionamento());
            default -> throw new DadosInvalidosException("Sensor inexistente: " + opcao);
        }
        System.out.println("Estado do sensor alterado. Use a opção 5 para conferir.");
    }

    private int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        String linha = scanner.nextLine().trim();
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Digite um número inteiro (recebido: '" + linha + "').");
        }
    }

    private double lerDecimal(String mensagem) {
        System.out.print(mensagem);
        String linha = scanner.nextLine().trim().replace(',', '.');
        try {
            return Double.parseDouble(linha);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Digite um número válido (recebido: '" + linha + "').");
        }
    }
}