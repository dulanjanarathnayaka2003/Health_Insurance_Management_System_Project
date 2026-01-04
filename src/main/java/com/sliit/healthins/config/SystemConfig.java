package com.sliit.healthins.config;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_key")
    private Long id;

    private String value;

    @Column(name = "config_name")
    private String configName;

    public SystemConfig() {

    }

}