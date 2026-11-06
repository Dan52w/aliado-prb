package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.Auditoria;
import java.util.List;

public interface AuditoriaRepository {
    Auditoria guardar(Auditoria auditoria);
    List<Auditoria> recientes(int limite);
}
