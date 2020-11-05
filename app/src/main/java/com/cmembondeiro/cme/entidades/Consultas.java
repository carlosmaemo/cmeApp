package com.cmembondeiro.cme.entidades;

public class Consultas {

    private String id;
    private String consulta;
    private String consulta_data;
    private String consulta_hora_inicial;
    private String consulta_hora_final;
    private String consulta_inf;
    private String consulta_medico;
    private String consulta_ormm;

    public Consultas() {

    }

    public Consultas(String id, String consulta, String consulta_data, String consulta_hora_inicial, String consulta_hora_final, String consulta_inf, String consulta_medico, String consulta_ormm) {
        this.id = id;
        this.consulta = consulta;
        this.consulta_data = consulta_data;
        this.consulta_hora_inicial = consulta_hora_inicial;
        this.consulta_hora_final = consulta_hora_final;
        this.consulta_inf = consulta_inf;
        this.consulta_medico = consulta_medico;
        this.consulta_ormm = consulta_ormm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getConsulta_data() {
        return consulta_data;
    }

    public void setConsulta_data(String consulta_data) {
        this.consulta_data = consulta_data;
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

    public String getConsulta_inf() {
        return consulta_inf;
    }

    public void setConsulta_inf(String consulta_inf) {
        this.consulta_inf = consulta_inf;
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
}
