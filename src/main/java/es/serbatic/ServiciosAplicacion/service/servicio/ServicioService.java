package es.serbatic.ServiciosAplicacion.service.servicio;

import java.util.List;
import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;

public interface ServicioService {
    List<ServicioVO> getAll();
    ServicioVO getById(Integer id);
    ServicioVO save(ServicioVO servicio);
    void deleteById(Integer id);
    boolean existePorNombre(String nombre);
    ServicioVO getByNombre(String nombre);
}
