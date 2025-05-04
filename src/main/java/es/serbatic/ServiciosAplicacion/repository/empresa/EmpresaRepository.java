package es.serbatic.ServiciosAplicacion.repository.empresa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;

public interface EmpresaRepository extends JpaRepository<EmpresaVO, Integer> {
    List<EmpresaVO> findByServicioId(Integer servicioId);
}
