package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.application;

import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.ServicioContratado;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Exportación del historial de servicios a CSV (SPEC-14). */
public final class HistorialCsvExporter {
    private HistorialCsvExporter() {}

    public static byte[] exportar(List<ServicioContratado> servicios) {
        StringBuilder csv = new StringBuilder("servicio_id,estado,fecha_contratacion\n");
        for (var s : servicios) {
            csv.append(s.id()).append(',').append(s.estado().name()).append(',')
                    .append(s.fechaContratacion()).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
