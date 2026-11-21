package com.taller.bibliotecas.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import com.taller.bibliotecas.presentationLayer.pagination.Paginacion;

/**
 * Reglas de acceso (RNF-01 y RNF-02). Se evaluan de arriba hacia abajo y gana la primera que coincide.
 *
 * <pre>
 * Ruta                                                   Quien puede
 * POST /auth/login, Swagger, /error, H2 (perfil test)     publico
 * /usuarios-admin/**                                      ADMINISTRADOR_GENERAL
 * POST/PUT/PATCH/DELETE /sedes/**                         ADMINISTRADOR_GENERAL
 * /reportes/**                                            ADMINISTRADOR_GENERAL, ADMINISTRADOR_SEDE
 * DELETE /categorias/** y /libros/**                      ADMINISTRADOR_GENERAL, ADMINISTRADOR_SEDE
 * resto de /api/**                                        cualquier usuario autenticado (los 3 roles)
 * </pre>
 *
 * Los permisos exactos por rol estan "en analisis" en el documento de requerimientos (seccion 3):
 * esta matriz es una propuesta facil de ajustar cambiando solo este archivo.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String GENERAL = "ADMINISTRADOR_GENERAL";
    private static final String SEDE = "ADMINISTRADOR_SEDE";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)                       // el token va en el header Authorization, no en cookie
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)  // 401
                        .accessDeniedHandler(accessDeniedHandler))           // 403
                .authorizeHttpRequests(auth -> auth
                        // preflight de CORS: el navegador nunca envia el token en un OPTIONS
                        .requestMatchers(ant(HttpMethod.OPTIONS, "/**")).permitAll()
                        .requestMatchers(ant("/api/v1/auth/login", "/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html", "/error", "/h2-console/**")).permitAll()

                        .requestMatchers(ant("/api/v1/usuarios-admin/**")).hasRole(GENERAL)
                        .requestMatchers(ant(HttpMethod.POST, "/api/v1/sedes/**")).hasRole(GENERAL)
                        .requestMatchers(ant(HttpMethod.PUT, "/api/v1/sedes/**")).hasRole(GENERAL)
                        .requestMatchers(ant(HttpMethod.PATCH, "/api/v1/sedes/**")).hasRole(GENERAL)
                        .requestMatchers(ant(HttpMethod.DELETE, "/api/v1/sedes/**")).hasRole(GENERAL)
                        .requestMatchers(ant("/api/v1/reportes/**")).hasAnyRole(GENERAL, SEDE)
                        .requestMatchers(ant(HttpMethod.DELETE, "/api/v1/categorias/**", "/api/v1/libros/**"))
                                .hasAnyRole(GENERAL, SEDE)

                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))   // consola H2 (solo perfil test)
                // nuestro filtro va antes del de usuario/clave: cuando llega la autorizacion, el SecurityContext ya tiene al usuario
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Se usan matchers explicitos (Ant) en lugar de Strings porque con la consola H2 activa (perfil test)
     * hay mas de un servlet registrado y Spring Security no puede decidir si un String es patron MVC o no.
     */
    private static RequestMatcher[] ant(String... patterns) {
        return ant((HttpMethod) null, patterns);
    }

    private static RequestMatcher[] ant(HttpMethod method, String... patterns) {
        RequestMatcher[] matchers = new RequestMatcher[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            matchers[i] = method == null
                    ? AntPathRequestMatcher.antMatcher(patterns[i])
                    : AntPathRequestMatcher.antMatcher(method, patterns[i]);
        }
        return matchers;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // sin esto el navegador (frontend en otro origen) no deja leer los headers de paginacion
        config.setExposedHeaders(List.of(Paginacion.HEADER_TOTAL_COUNT, Paginacion.HEADER_TOTAL_PAGES,
                Paginacion.HEADER_PAGE, Paginacion.HEADER_PAGE_SIZE));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** Evita que Spring Boot registre el filtro JWT tambien como filtro global (se ejecutaria dos veces). */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
