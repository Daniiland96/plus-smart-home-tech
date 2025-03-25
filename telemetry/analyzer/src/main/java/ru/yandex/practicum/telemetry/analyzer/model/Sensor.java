package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@ToString
public class Sensor {
    @Id
    String id;

    @Column(name = "hub_id")
    private String hubId;
}
