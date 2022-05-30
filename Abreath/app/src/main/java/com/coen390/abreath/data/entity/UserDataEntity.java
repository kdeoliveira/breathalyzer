package com.coen390.abreath.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Entity representing the data fetched from mock API
 * The response body will be serialized to this class
 */
public class UserDataEntity {
    private String username;
    private float bac;
    @SerializedName("createdAt")
    private Date created_at;

    private int id;
    private String name;
    @SerializedName("lastName")
    private String last_name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    private int age;
    private int weight;
    private float height;

    public int getId() {
        return id;
    }

    public UserDataEntity(String username, float bac, Date created_at, int id, String name, String last_name, int age, int weight, float height) {
        this.username = username;
        this.bac = bac;
        this.created_at = created_at;
        this.id = id;
        this.name = name;
        this.last_name = last_name;
        this.age = age;
        this.weight = weight;
        this.height = height;
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
