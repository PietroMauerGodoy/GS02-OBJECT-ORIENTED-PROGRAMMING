package br.com.fiap.territory_predictive_insights_fev.servico;

import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;

public class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    // Limiares de chuva prevista, em mm/h
    private double limiarAtencao = 20.0;
    private double limiarAlerta = 40.0;
    private double limiarCritico = 60.0;

    // Construtor privado: ninguém de fora cria outra instância
    private ConfiguracaoSistema() {
    }

    public static synchronized ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public synchronized void definirLimiares(double atencao, double alerta, double critico) {
        if (atencao <= 0) {
            throw new DadosInvalidosException("O limiar de atenção deve ser maior que zero (recebido: " + atencao + ").");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(
                    "Os limiares devem seguir atenção < alerta < crítico (recebido: "
                            + atencao + ", " + alerta + ", " + critico + ").");
        }
        this.limiarAtencao = atencao;
        this.limiarAlerta = alerta;
        this.limiarCritico = critico;
    }

    public double getLimiarAtencao() {
        return limiarAtencao;
    }

    public double getLimiarAlerta() {
        return limiarAlerta;
    }

    public double getLimiarCritico() {
        return limiarCritico;
    }
}