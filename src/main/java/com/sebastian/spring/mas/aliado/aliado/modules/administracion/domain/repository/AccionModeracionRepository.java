package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.AccionModeracion;
import java.util.List;
import java.util.UUID;

public interface AccionModeracionRepository {
    List<AccionModeracion> porObjetivo(String objetivoTipo, UUID objetivoId);
    AccionModeracion guardar(AccionModeracion accion);
}
