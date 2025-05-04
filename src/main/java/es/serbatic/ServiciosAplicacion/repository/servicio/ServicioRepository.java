package es.serbatic.ServiciosAplicacion.repository.servicio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioVO, Integer> {

    ServicioVO findByNombre(String nombre);
    
}
