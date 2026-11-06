package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.config;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.HasheadorPassword;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.PerfilBasePort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.service.RegistrarUsuarioService;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.usecase.RegistrarUsuarioUseCase;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.ConsentimientoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** Composición técnica de los casos de uso del módulo auth. */
@Configuration
public class AuthApplicationConfig {

    @Bean
    public RegistrarUsuarioService registrarUsuarioService(UsuarioRepository usuarios,
                                                           ConsentimientoRepository consentimientos,
                                                           PerfilBasePort perfilBase,
                                                           HasheadorPassword hasheadorPassword) {
        return new RegistrarUsuarioService(usuarios, consentimientos, perfilBase, hasheadorPassword);
    }

    @Bean
    @Primary
    public RegistrarUsuarioUseCase registrarUsuarioUseCase(RegistrarUsuarioService service,
                                                            PlatformTransactionManager transactionManager) {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        return command -> transaction.execute(status -> service.ejecutar(command));
    }
}
