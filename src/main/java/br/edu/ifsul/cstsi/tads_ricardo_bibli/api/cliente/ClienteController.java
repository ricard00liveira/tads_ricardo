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
    public ResponseEntity<Cliente> getClienteById(@PathVariable("codigo") Long codigo) {
        var cliente = clienteRepository.findById(codigo);
        return cliente.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(cliente.get());
    }

    @GetMapping("nome/{nome}")
    public String getClienteByNome(@PathVariable("nome") String nome) {
        return "Cliente com nome: " + nome;
    }
}
