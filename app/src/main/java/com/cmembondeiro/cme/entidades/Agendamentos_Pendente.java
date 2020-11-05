package com.cmembondeiro.cme.entidades;

public class Agendamentos_Pendente {

    private String consulta_id;
    private String consulta;
    private String consulta_medico;
    private String consulta_ormm;
    private String consulta_inf;
    private String consulta_data;
    private String consulta_hora_inicial;
    private String consulta_hora_final;
    private String consulta_hora_paciente;
    private String paciente_id;
    private String paciente_nome;
    private String paciente_apelido;
    private String paciente_bi;
    private String paciente_celular;
    private String paciente_nascimento;
    private String paciente_pac;
    private String paciente_provincia;
    private String paciente_regiao;
    private String paciente_residencia;
    private String paciente_sexo;
    private String id;

    public Agendamentos_Pendente() {

    }

    public Agendamentos_Pendente(String id, String consulta_id, String consulta, String consulta_medico, String consulta_ormm, String consulta_inf, String consulta_data, String consulta_hora_inicial, String consulta_hora_final, String consulta_hora_paciente, String paciente_id, String paciente_nome, String paciente_apelido, String paciente_bi, String paciente_celular, String paciente_nascimento, String paciente_pac, String paciente_provincia, String paciente_regiao, String paciente_residencia, String paciente_sexo) {
        this.id = id;
        this.consulta_id = consulta_id;
        this.consulta = consulta;
        this.consulta_medico = consulta_medico;
        this.consulta_ormm = consulta_ormm;
        this.consulta_inf = consulta_inf;
        this.consulta_data = consulta_data;
        this.consulta_hora_inicial = consulta_hora_inicial;
        this.consulta_hora_final = consulta_hora_final;
        this.consulta_hora_final = consulta_hora_paciente;
        this.paciente_id = paciente_id;
        this.paciente_nome = paciente_nome;
        this.paciente_apelido = paciente_apelido;
        this.paciente_bi = paciente_bi;
        this.paciente_celular = paciente_celular;
        this.paciente_nascimento = paciente_nascimento;
        this.paciente_pac = paciente_pac;
        this.paciente_provincia = paciente_provincia;
        this.paciente_regiao = paciente_regiao;
        this.paciente_residencia = paciente_residencia;
        this.paciente_sexo = paciente_sexo;
    }

    public String getConsulta_id() {
        return consulta_id;
    }

    public void setConsulta_id(String consulta_id) {
        this.consulta_id = consulta_id;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getConsulta_medico() {
        return consulta_medico;
    }

    public void setConsulta_medico(String consulta_medico) {
        this.consulta_medico = consulta_medico;
    }

    public String getConsulta_ormm() {
        return consulta_ormm;
    }

    public void setConsulta_ormm(String consulta_ormm) {
        this.consulta_ormm = consulta_ormm;
    }

    public String getConsulta_inf() {
        return consulta_inf;
    }

    public void setConsulta_inf(String consulta_inf) {
        this.consulta_inf = consulta_inf;
    }

    public String getConsulta_data() {
        return consulta_data;
    }

    public void setConsulta_data(String consulta_data) {
        this.consulta_data = consulta_data;
    }

    public String getPaciente_id() {
        return paciente_id;
    }

    public void setPaciente_id(String paciente_id) {
        this.paciente_id = paciente_id;
    }

    public String getPaciente_nome() {
        return paciente_nome;
    }

    public void setPaciente_nome(String paciente_nome) {
        this.paciente_nome = paciente_nome;
    }

    public String getPaciente_apelido() {
        return paciente_apelido;
    }

    public void setPaciente_apelido(String paciente_apelido) {
        this.paciente_apelido = paciente_apelido;
    }

    public String getPaciente_bi() {
        return paciente_bi;
    }

    public void setPaciente_bi(String paciente_bi) {
        this.paciente_bi = paciente_bi;
    }

    public String getPaciente_celular() {
        return paciente_celular;
    }

    public void setPaciente_celular(String paciente_celular) {
        this.paciente_celular = paciente_celular;
    }

    public String getPaciente_nascimento() {
        return paciente_nascimento;
    }

    public void setPaciente_nascimento(String paciente_nascimento) {
        this.paciente_nascimento = paciente_nascimento;
    }

    public String getPaciente_pac() {
        return paciente_pac;
    }

    public void setPaciente_pac(String paciente_pac) {
        this.paciente_pac = paciente_pac;
    }

    public String getPaciente_provincia() {
        return paciente_provincia;
    }

    public void setPaciente_provincia(String paciente_provincia) {
        this.paciente_provincia = paciente_provincia;
    }

    public String getPaciente_regiao() {
        return paciente_regiao;
    }

    public void setPaciente_regiao(String paciente_regiao) {
        this.paciente_regiao = paciente_regiao;
    }

    public String getPaciente_residencia() {
        return paciente_residencia;
    }

    public void setPaciente_residencia(String paciente_residencia) {
        this.paciente_residencia = paciente_residencia;
    }

    public String getPaciente_sexo() {
        return paciente_sexo;
    }

    public void setPaciente_sexo(String paciente_sexo) {
        this.paciente_sexo = paciente_sexo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsulta_hora_inicial() {
        return consulta_hora_inicial;
    }

    public void setConsulta_hora_inicial(String consulta_hora_inicial) {
        this.consulta_hora_inicial = consulta_hora_inicial;
    }

    public String getConsulta_hora_final() {
        return consulta_hora_final;
    }

    public void setConsulta_hora_final(String consulta_hora_final) {
        this.consulta_hora_final = consulta_hora_final;
    }

    public String getConsulta_hora_paciente() {
        return consulta_hora_paciente;
    }

    public void setConsulta_hora_paciente(String consulta_hora_paciente) {
        this.consulta_hora_paciente = consulta_hora_paciente;
    }
}
