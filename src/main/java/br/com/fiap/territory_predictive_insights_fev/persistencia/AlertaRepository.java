package br.com.fiap.territory_predictive_insights_fev.persistencia;

import br.com.fiap.territory_predictive_insights_fev.dominio.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findAllByOrderByDataHoraDesc();
}