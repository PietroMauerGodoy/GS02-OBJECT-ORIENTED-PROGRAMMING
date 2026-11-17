package br.com.fiap.territory_predictive_insights_fev.dominio;

import br.com.fiap.territory_predictive_insights_fev.excecao.DadosInvalidosException;

public abstract class ComponenteTerritorial {

    private final String id;
    private String nome;
    private String regiao;
    private String status;

    protected ComponenteTerritorial(String id, String nome, String regiao) {
        if (id == null || id.isBlank()) {
            throw new DadosInvalidosException("O componente territorial precisa ter um id.");
        }
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O componente territorial precisa ter um nome.");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("O componente territorial precisa ter uma região.");
        }
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = "DESCONHECIDO";
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    // Cada tipo de componente define como descobre o próprio status
    public abstract String verificarStatus();
}