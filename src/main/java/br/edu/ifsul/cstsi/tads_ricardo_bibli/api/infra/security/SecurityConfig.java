package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) //controle de acesso por anotação em métodos
public class SecurityConfig {
    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter){
        this.securityFilter = securityFilter;
    }
    @Bean
    //indica ao spring boot que essa configuração deve ser adicionada ao contexto do aplicativo (objeto injetado em AutenticacaoController)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean //indica ao spring boot que essa configuração deve ser adicionada ao contexto do aplicativo (utilizado pelo Spring Boot para o decode da senha)
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(); //devolve o BCryptPasswordEncoder como codificador e decodificador de senhas
    }

    @Bean //indica ao spring boot que essa configuração deve ser adicionada ao contexto do aplicativo (configura a segurança do app)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Configuração JWT Authentication
        http
                .csrf(csrf -> csrf.disable()) //desabilita a proteção contra ataques Cross-site Request Forger, comum em conexões Stateless
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //sem sessão (desabilita o stateful)
                .authorizeHttpRequests(authorize -> {  //configurar a autorização
                    authorize.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll(); //exceto, a rota de documentação (para doc em html no navegador; e para ferramentas automatizadas de geração de código)
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/login", "/api/v1/usuarios/cadastrar").permitAll(); //exceto, a rota de login e de cadastro de usuário
                    authorize.requestMatchers(HttpMethod.GET, "/confirmar-email").permitAll(); //exceto, a rota de confirmação de email
                    authorize.anyRequest().authenticated(); //demais rotas devem ser autenticadas
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class); //manda o filter do projeto vir antes do filter do Spring

        return http.build();
    }

}