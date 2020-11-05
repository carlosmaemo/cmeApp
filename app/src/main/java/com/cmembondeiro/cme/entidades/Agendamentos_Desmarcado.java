package com.cmembondeiro.cme.entidades;

public class Agendamentos_Desmarcado {
    private String id;
    private String agendamento_id;
    private String consulta;
    private String agendamento_medico;
    private String agendamento_ormm;
    private String agendamento_inf;
    private String agendamento_data;
    private String agendamento_hora_inicial;
    private String agendamento_hora_final;
    private String agendamento_hora_paciente;
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

    public Agendamentos_Desmarcado() {

    }

    public Agendamentos_Desmarcado(String id, String agendamento_id, String consulta, String agendamento_medico, String agendamento_ormm, String agendamento_inf, String agendamento_data, String agendamento_hora_inicial, String agendamento_hora_final, String agendamento_hora_paciente, String paciente_id, String paciente_nome, String paciente_apelido, String paciente_bi, String paciente_celular, String paciente_nascimento, String paciente_pac, String paciente_provincia, String paciente_regiao, String paciente_residencia, String paciente_sexo) {
        this.id = id;
        this.agendamento_id = agendamento_id;
        this.consulta = consulta;
        this.agendamento_medico = agendamento_medico;
        this.agendamento_ormm = agendamento_ormm;
        this.agendamento_inf = agendamento_inf;
        this.agendamento_data = agendamento_data;
        this.agendamento_hora_inicial = agendamento_hora_inicial;
        this.agendamento_hora_final = agendamento_hora_final;
        this.agendamento_hora_paciente = agendamento_hora_paciente;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgendamento_id() {
        return agendamento_id;
    }

    public void setAgendamento_id(String agendamento_id) {
        this.agendamento_id = agendamento_id;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getAgendamento_medico() {
        return agendamento_medico;
    }

    public void setAgendamento_medico(String agendamento_medico) {
        this.agendamento_medico = agendamento_medico;
    }

    public String getAgendamento_ormm() {
        return agendamento_ormm;
    }

    public void setAgendamento_ormm(String agendamento_ormm) {
        this.agendamento_ormm = agendamento_ormm;
    }

    public String getAgendamento_inf() {
        return agendamento_inf;
    }

    public void setAgendamento_inf(String agendamento_inf) {
        this.agendamento_inf = agendamento_inf;
    }

    public String getAgendamento_data() {
        return agendamento_data;
    }

    public void setAgendamento_data(String agendamento_data) {
        this.agendamento_data = agendamento_data;
    }

    public String getAgendamento_hora_inicial() {
        return agendamento_hora_inicial;
    }

    public void setAgendamento_hora_inicial(String agendamento_hora_inicial) {
        this.agendamento_hora_inicial = agendamento_hora_inicial;
    }

    public String getAgendamento_hora_final() {
        return agendamento_hora_final;
    }

    public void setAgendamento_hora_final(String agendamento_hora_final) {
        this.agendamento_hora_final = agendamento_hora_final;
    }

    public String getAgendamento_hora_paciente() {
        return agendamento_hora_paciente;
    }

    public void setAgendamento_hora_paciente(String agendamento_hora_paciente) {
        this.agendamento_hora_paciente = agendamento_hora_paciente;
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
}
