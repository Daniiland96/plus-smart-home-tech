package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Scenario;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    Optional<Scenario> findByNameAndHubId(String name, String hubId);

    List<Scenario> findAllByHubId(String hubId);

    void deleteByHubIdAndName(String hubId, String name);
}
