/*
......................................................
Desenvolvido por Eng. Maemo, Carlos
CELL GAME, EI | cellgame@email.com | www.cellgame.tk
Centro Médico Embondeiro, Lda. Copyright (C)2019
Gerência de Tecnologias de Informação
......................................................
*/

package com.cmembondeiro.cme.entidades;

public class Pacientes {

    private String id;
    private String nome;
    private String apelido;
    private String pac;
    private String nascimento;
    private String regiao;
    private String provincia;
    private String residencia;
    private String bi;
    private String codigo;
    private String sexo;
    private String celular;
    private String estado;
    private String imagem;
    private String miniatura;
    private String tipo_usuario;

    public Pacientes() {

    }

    public Pacientes(String id, String nome, String apelido, String pac, String nascimento, String regiao, String provincia, String residencia, String codigo, String bi, String celular, String estado, String imagem, String miniatura, String sexo, String tipo_usuario) {
        this.id = id;
        this.nome = nome;
        this.apelido = apelido;
        this.pac = pac;
        this.nascimento = nascimento;
        this.regiao = regiao;
        this.provincia = provincia;
        this.residencia = residencia;
        this.bi = bi;
        this.codigo = codigo;
        this.celular = celular;
        this.estado = estado;
        this.imagem = imagem;
        this.miniatura = miniatura;
        this.sexo = sexo;
        this.tipo_usuario = tipo_usuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getPac() {
        return pac;
    }

    public void setPac(String pac) {
        this.pac = pac;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getMiniatura() {
        return miniatura;
    }

    public void setMiniatura(String miniatura) {
        this.miniatura = miniatura;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getResidencia() {
        return residencia;
    }

    public void setResidencia(String residencia) {
        this.residencia = residencia;
    }

    public String getBi() {
        return bi;
    }

    public void setBi(String bi) {
        this.bi = bi;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }
}

/*
......................................................
Desenvolvido por Eng. Maemo, Carlos
CELL GAME, EI | cellgame@email.com | www.cellgame.tk
Centro Médico Embondeiro, Lda. Copyright (C)2019
Gerência de Tecnologias de Informação
......................................................
*/