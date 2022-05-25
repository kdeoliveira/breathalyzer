package com.coen390.abreath.model;

import java.util.Date;

public class SampleEntity {
    private String username;
    private float bac;
    private Date created_at;

    public int getId() {
        return id;
    }

    public SampleEntity(String username, float bac, Date created_at, int id, String name, String last_name) {
        this.username = username;
        this.bac = bac;
        this.created_at = created_at;
        this.id = id;
        this.name = name;
        this.last_name = last_name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    private int id;
    private String name;
    private String last_name;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getBac() {
        return bac;
    }

    public void setBac(float bac) {
        this.bac = bac;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
