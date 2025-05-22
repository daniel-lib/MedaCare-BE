package com.medacare.backend.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SystemSetting implements Serializable {

    public SystemSetting(String name, String value1, String value2, String value3, String value4) {
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
    }
    public SystemSetting(){
        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String value1;
    private String value2;
    private String value3;
    private String value4;
}
