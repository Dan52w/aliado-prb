package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.AccionModeracion;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.Auditoria;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.BloqueoPreventivo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.MarcaAltoRiesgo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.AccionModeracionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.AuditoriaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.BloqueoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.MarcaRiesgoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa.AccionModeracionJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa.AuditoriaJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa.BloqueoJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa.MarcaRiesgoJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.mapper.AdministracionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class AdministracionPersistenceAdapter implements BloqueoRepository, AccionModeracionRepository,
        MarcaRiesgoRepository, AuditoriaRepository {
    private final BloqueoJpaRepository bloqueos;
    private final AccionModeracionJpaRepository acciones;
    private final MarcaRiesgoJpaRepository marcas;
    private final AuditoriaJpaRepository auditoria;

    public AdministracionPersistenceAdapter(BloqueoJpaRepository bloqueos,
                                            AccionModeracionJpaRepository acciones,
                                            MarcaRiesgoJpaRepository marcas,
                                            AuditoriaJpaRepository auditoria) {
        this.bloqueos = bloqueos;
        this.acciones = acciones;
        this.marcas = marcas;
        this.auditoria = auditoria;
    }

    @Override
    public Optional<BloqueoPreventivo> activoDeUsuario(UUID usuarioId) {
        return bloqueos.findByUsuarioIdAndActivoTrue(usuarioId)
                .map(AdministracionPersistenceMapper::aDominio);
    }

    @Override
    public Optional<BloqueoPreventivo> porId(UUID id) {
        return bloqueos.findById(id).map(AdministracionPersistenceMapper::aDominio);
    }

    @Override
    public BloqueoPreventivo guardar(BloqueoPreventivo bloqueo) {
        return AdministracionPersistenceMapper.aDominio(
                bloqueos.save(AdministracionPersistenceMapper.aEntidad(bloqueo)));
    }

    @Override
    public List<AccionModeracion> porObjetivo(String objetivoTipo, UUID objetivoId) {
        return acciones.findByObjetivoTipoAndObjetivoId(objetivoTipo, objetivoId).stream()
                .map(AdministracionPersistenceMapper::aDominio).toList();
    }

    @Override
    public AccionModeracion guardar(AccionModeracion accion) {
        return AdministracionPersistenceMapper.aDominio(
                acciones.save(AdministracionPersistenceMapper.aEntidad(accion)));
    }

    @Override
    public Optional<MarcaAltoRiesgo> activaDeServicio(UUID servicioId) {
        return marcas.findByServicioIdAndActivaTrue(servicioId)
                .map(AdministracionPersistenceMapper::aDominio);
    }

    @Override
    public MarcaAltoRiesgo guardar(MarcaAltoRiesgo marca) {
        return AdministracionPersistenceMapper.aDominio(
                marcas.save(AdministracionPersistenceMapper.aEntidad(marca)));
    }

    @Override
    public Auditoria guardar(Auditoria registro) {
        return AdministracionPersistenceMapper.aDominio(
                auditoria.save(AdministracionPersistenceMapper.aEntidad(registro)));
    }

    @Override
    public List<Auditoria> recientes(int limite) {
        return auditoria.findAllByOrderByFechaDesc(PageRequest.of(0, limite)).stream()
                .map(AdministracionPersistenceMapper::aDominio).toList();
    }
}
