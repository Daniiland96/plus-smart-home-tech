package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {
    void deleteByIdAndHubId(String id, String hubId);
}