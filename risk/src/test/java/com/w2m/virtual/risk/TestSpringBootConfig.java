package com.w2m.virtual.risk;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Configuración mínima para los slice tests (@WebMvcTest) del módulo {@code risk}.
 * El módulo bootstrap real ({@code app}) no es accesible desde aquí.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TestSpringBootConfig {
}
