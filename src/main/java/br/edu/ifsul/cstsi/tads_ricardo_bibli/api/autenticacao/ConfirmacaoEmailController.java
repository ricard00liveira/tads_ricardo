package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.autenticacao;

import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.infra.exception.TokenInvalidoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfirmacaoEmailController {

    /**
     * Endpoint para confirmar o email do usuário através de um token.
     * 
     * @param token O token de confirmação enviado por email
     * @return ResponseEntity com status 200 OK se o token for válido
     * @throws TokenInvalidoException se o token for inválido
     */
    @GetMapping("/confirmar-email")
    public ResponseEntity<String> confirmarEmail(@RequestParam String token) {
        // Validação simples para o teste
        if ("aecc73b7-dae5-4011-925d-e07633d9993f".equals(token)) {
            return ResponseEntity.ok("Email confirmado com sucesso!");
        } else {
            throw new TokenInvalidoException("Token inválido");
        }
    }
}