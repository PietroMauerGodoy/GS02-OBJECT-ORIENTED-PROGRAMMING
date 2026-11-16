package br.com.fiap.territory_predictive_insights_fev.persistencia;

import br.com.fiap.territory_predictive_insights_fev.dominio.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    List<Medicao> findBySensorIdOrderByDataHoraDesc(String sensorId);
}