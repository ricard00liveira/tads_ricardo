package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente;

import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.aluno.Aluno;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.paidealuno.PaiDeAluno;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                    .status(HttpStatus.NOT_FOUND)
                    .body("Cliente com código " + codigo + " não encontrado.");
        }

        return ResponseEntity.ok(new ClienteDto(cliente.get()));
    }


    @GetMapping("nome/{nome}")
    public ResponseEntity<List<ClienteDto>> getClientesByNome(@PathVariable("nome") String nome) {
        var clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        var dtos = clientes.stream().map(ClienteDto::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("tipo/{tipo}")
    public ResponseEntity<List<ClienteDto>> getClientesByTipo(@PathVariable String tipo) {
        List<Cliente> todos = clienteRepository.findAll();

        List<ClienteDto> filtrados = todos.stream()
                .filter(c -> c.getClass().getSimpleName().equalsIgnoreCase(tipo))
                .map(ClienteDto::new)
                .toList();

        return ResponseEntity.ok(filtrados);
    }

    // PATCH
    @PatchMapping("/{codigo}")
    public ResponseEntity<ClienteDto> updateParcial(
            @PathVariable Long codigo,
            @RequestBody ClientePatchDto dto
    ) {
        return clienteRepository.findById(codigo).map(cliente -> {
            if (dto.nome() != null) cliente.setNome(dto.nome());
            if (dto.idade() != null) cliente.setIdade(dto.idade());
            if (dto.telefone() != null) cliente.setTelefone(dto.telefone());
            if (dto.endereco() != null) cliente.setEndereco(dto.endereco());
            clienteRepository.save(cliente);
            return ResponseEntity.ok(new ClienteDto(cliente));
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Cliente com código " + codigo + " não encontrado."));
    }

    // POST
    @PostMapping
    public ResponseEntity<ClienteDto> create(@RequestBody ClientePostDto dto) {
        Cliente novoCliente;
        if ("ALUNO".equalsIgnoreCase(dto.tipo())) {
            novoCliente = new Aluno();
        } else if ("PAI_DE_ALUNO".equalsIgnoreCase(dto.tipo())) {
            novoCliente = new PaiDeAluno();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de cliente inválido");
        }

        novoCliente.setNome(dto.nome());
        novoCliente.setIdade(dto.idade());
        novoCliente.setTelefone(dto.telefone());
        novoCliente.setEndereco(dto.endereco());

        var saved = clienteRepository.save(novoCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClienteDto(saved));
    }

    // DELETE
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(@PathVariable Long codigo) {
        return clienteRepository.findById(codigo).map(cliente -> {
            clienteRepository.delete(cliente);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cliente com código " + codigo + " não encontrado."));
    }
}
