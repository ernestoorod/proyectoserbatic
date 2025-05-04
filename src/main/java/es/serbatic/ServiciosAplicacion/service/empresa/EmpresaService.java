package es.serbatic.ServiciosAplicacion.service.empresa;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;


import java.util.List;

public interface EmpresaService {
    public List<EmpresaVO> listarEmpresas();
    public List<EmpresaVO> obtenerEmpresasPorServicio(Integer servicioId);
    public EmpresaVO obtenerEmpresaPorId(Integer id);
    public void guardarEmpresa(EmpresaVO empresa);
    public void eliminarEmpresa(Integer id);
    
}
