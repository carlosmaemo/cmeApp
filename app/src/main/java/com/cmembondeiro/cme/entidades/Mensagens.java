package com.cmembondeiro.cme.entidades;

public class Mensagens {

    private String id;
    private String de;
    private String mensagem;
    private Long tempo;
    private String data;
    private String usuario;

    public Mensagens() {

    }

    public Mensagens(String id, String de, String mensagem, Long tempo, String data, String usuario) {
        this.id = id;
        this.de = de;
        this.mensagem = mensagem;
        this.tempo = tempo;
        this.data = data;
        this.usuario = usuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
