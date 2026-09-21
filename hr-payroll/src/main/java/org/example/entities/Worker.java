package org.example.entities;

import java.io.Serializable;
import java.util.Objects;

public class Worker implements Serializable {

    private Long id;
    private String name;
    private Double dailyIncome;

    public Worker(){
    }

    public Worker(Long id, Double dailyIncome, String name) {
        this.id = id;
        this.dailyIncome = dailyIncome;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDailyIncome() {
        return dailyIncome;
    }

    public void setDailyIncome(Double dailyIncome) {
        this.dailyIncome = dailyIncome;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return Objects.equals(id, worker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
