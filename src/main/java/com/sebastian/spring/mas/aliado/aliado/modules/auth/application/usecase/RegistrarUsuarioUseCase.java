package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.usecase;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto.RegistrarUsuarioCommand;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto.ResultadoRegistro;

public interface RegistrarUsuarioUseCase {
    ResultadoRegistro ejecutar(RegistrarUsuarioCommand command);
}
