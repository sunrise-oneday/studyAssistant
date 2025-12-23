package org.example.studyassistant.pojo.enums;

public enum DifficultyType {
    CALCULATION("计算困难"),
    CONCEPT("概念模糊"),
    METHOD("方法不当");

    private final String description;

    DifficultyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}