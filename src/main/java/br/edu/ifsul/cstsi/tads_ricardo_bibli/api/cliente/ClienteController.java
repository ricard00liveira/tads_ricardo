package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public ResponseEntity<List<ClienteDto>> getClientes() {
        var clientes = clienteRepository.findAll();
        return ResponseEntity
                .ok()
                .body(
                        clientes.stream()
                                .map(ClienteDto::new)
                                .toList());
    }

    @GetMapping("{codigo}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<?> getClienteById(@PathVariable("codigo") Long codigo) {
        var cliente = clienteRepository.findById(codigo);

        if (cliente.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Cliente com código " + codigo + " não encontrado.");
        }

        return ResponseEntity.ok(cliente.get());
    }


    @GetMapping("nome/{nome}")
    public ResponseEntity<List<ClienteDto>> getClientesByNome(@PathVariable("nome") String nome) {
        var clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        var dtos = clientes.stream().map(ClienteDto::new).toList();
        return ResponseEntity.ok(dtos);
    }
}
