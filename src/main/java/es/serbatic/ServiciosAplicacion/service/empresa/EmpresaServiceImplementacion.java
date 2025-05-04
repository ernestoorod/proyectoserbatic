package es.serbatic.ServiciosAplicacion.service.empresa;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaDTO;
import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.repository.empresa.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmpresaServiceImplementacion implements EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public List<EmpresaVO> listarEmpresas() {
        return empresaRepository.findAll();
    }

    @Override
    public List<EmpresaVO> obtenerEmpresasPorServicio(Integer servicioId) {
        return empresaRepository.findByServicioId(servicioId);
    }

    @Override
    public EmpresaVO obtenerEmpresaPorId(Integer id) {
        return empresaRepository.findById(id).orElse(null);
    }

    @Override
    public void guardarEmpresa(EmpresaVO empresa) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime apertura = LocalTime.parse(empresa.getHoraApertura(), formatter);
        LocalTime cierre   = LocalTime.parse(empresa.getHoraCierre(), formatter);

        if (!apertura.isBefore(cierre)) {
            throw new IllegalArgumentException("La hora de apertura debe ser anterior a la hora de cierre.");
        }

        empresaRepository.save(empresa);
    }

    @Override
    public void eliminarEmpresa(Integer id) {
        empresaRepository.deleteById(id);
    }

    public interface EmpresaService {
        void insertarEmpresa(EmpresaDTO empresa);
    }


}
