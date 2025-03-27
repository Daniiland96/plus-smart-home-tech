package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {
    void deleteByIdAndHubId(String id, String hubId);

    boolean existsAllByIdInAndHubId(List<String> ids, String hubId);
}