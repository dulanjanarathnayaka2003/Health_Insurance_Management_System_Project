package com.sliit.healthins.dto;

public class ConfigDTO {
    private String key;
    private String value;

    public ConfigDTO() {}

    public ConfigDTO(Object o, String value) {
        this.key = (String) o;
        this.value = value;
    }

    // Getters
    public String getKey() { return key; }
    public String getValue() { return value; }

    // Setters
    public void setKey(String key) { this.key = key; }
    public void setValue(String value) { this.value = value; }
}