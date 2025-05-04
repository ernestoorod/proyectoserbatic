package es.serbatic.ServiciosAplicacion.service.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;
import es.serbatic.ServiciosAplicacion.repository.servicio.ServicioRepository;

@Service
public class ServicioServiceImplementacion implements ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public List<ServicioVO> getAll() {
        return servicioRepository.findAll();
    }

    @Override
    public ServicioVO getById(Integer id) {
        return servicioRepository.findById(id).orElse(null);
    }

    @Override
    public ServicioVO save(ServicioVO servicio) {
        if (servicio.getId() == null || servicio.getId() == 0) {
            if (servicioRepository.findByNombre(servicio.getNombre()) != null) {
                throw new IllegalArgumentException("Ya existe un servicio con ese nombre.");
            }
        } else {
            ServicioVO existente = servicioRepository.findByNombre(servicio.getNombre());
            if (existente != null && !existente.getId().equals(servicio.getId())) {
                throw new IllegalArgumentException("Ya existe un servicio con ese nombre.");
            }
        }
        return servicioRepository.save(servicio);
    }

    @Override
    public void deleteById(Integer id) {
        servicioRepository.deleteById(id);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return servicioRepository.findByNombre(nombre) != null;
    }

    @Override
    public ServicioVO getByNombre(String nombre) {
        return servicioRepository.findByNombre(nombre);
    }
}
