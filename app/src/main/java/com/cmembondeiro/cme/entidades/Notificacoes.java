package com.cmembondeiro.cme.entidades;

public class Notificacoes {

    private String id;
    private String titulo;
    private String descricao;
    private String paciente_id;

    public Notificacoes() {

    }

    public Notificacoes(String id, String titulo, String descricao, String paciente_id) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.paciente_id = paciente_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPaciente_id() {
        return paciente_id;
    }

    public void setPaciente_id(String paciente_id) {
        this.paciente_id = paciente_id;
    }

}
