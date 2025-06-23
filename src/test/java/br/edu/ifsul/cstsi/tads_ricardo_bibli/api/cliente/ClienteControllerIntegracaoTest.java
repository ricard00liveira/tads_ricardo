package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente;

import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.usuario.PerfilRepository;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.usuario.UsuarioRepository;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")

public class ClienteControllerIntegracaoTest extends BaseAPIIntegracaoTest {

    // Classes concretas para instanciar a entidade abstrata Cliente durante os testes
    @Entity
    @DiscriminatorValue("ALUNO")
    public static class Aluno extends Cliente {}

    @Entity
    @DiscriminatorValue("PAI_DE_ALUNO")
    public static class PaiDeAluno extends Cliente {}

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Cliente clienteAluno;
    private Cliente clientePai;

    @BeforeEach
    void setUp() {
        // Limpa o repositório de clientes antes de cada teste para garantir isolamento.
        clienteRepository.deleteAll();

        // A lógica de autenticação continua a mesma.
        // A BaseAPIIntegracaoTest carrega o usuário 'admin@email.com' que já
        // foi criado pela aplicação ao iniciar o contexto de teste.
        super.setupTest();

        // Prepara os objetos de cliente para serem usados nos testes.
        clienteAluno = new Aluno();
        clienteAluno.setNome("Ricardo Aluno Teste");
        clienteAluno.setIdade(20);
        clienteAluno.setTelefone("53987654321");
        clienteAluno.setEndereco("Rua Teste, 123");

        clientePai = new PaiDeAluno();
        clientePai.setNome("Carlos Pai Teste");
        clientePai.setIdade(45);
        clientePai.setTelefone("53912345678");
        clientePai.setEndereco("Avenida Teste, 456");
    }

    @Test
    @DisplayName("Deve retornar uma lista com todos os clientes")
    void getAllClientes_retornaListaDeClientes() {
        clienteRepository.save(clienteAluno);
        clienteRepository.save(clientePai);
        ResponseEntity<List> response = get("/api/v1/clientes", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("Deve retornar um cliente pelo seu código (ID)")
    void getClienteById_quandoExiste_retornaClienteDto() {
        Cliente savedCliente = clienteRepository.save(clienteAluno);
        Long id = savedCliente.getCodigo();
        ResponseEntity<ClienteDto> response = get("/api/v1/clientes/" + id, ClienteDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().codigo());
        assertEquals("Ricardo Aluno Teste", response.getBody().nome());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found para um código (ID) de cliente que não existe")
    void getClienteById_quandoNaoExiste_retornaNotFound() {
        ResponseEntity<Map> response = get("/api/v1/clientes/999", Map.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve criar um novo cliente do tipo Aluno com sucesso")
    void createCliente_comDadosValidos_retornaCreated() {
        ClientePostDto novoClienteDto = new ClientePostDto(
                "Novo Aluno", 18, "53999998888", "Rua Nova, 789", "ALUNO");
        ResponseEntity<ClienteDto> response = post("/api/v1/clientes", novoClienteDto, ClienteDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Novo Aluno", response.getBody().nome());
        assertEquals("Aluno", response.getBody().tipo());
    }

    @Test
    @DisplayName("Deve atualizar parcialmente um cliente com sucesso")
    void patchCliente_quandoExiste_atualizaEretornaOk() {
        Cliente savedCliente = clienteRepository.save(clientePai);
        Long id = savedCliente.getCodigo();
        ClientePatchDto patchDto = new ClientePatchDto("Carlos Pai Modificado", null, "53111112222", null);
        ResponseEntity<ClienteDto> response = rest.exchange(
                "/api/v1/clientes/" + id, HttpMethod.PATCH, new HttpEntity<>(patchDto, getHeaders()), ClienteDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Carlos Pai Modificado", response.getBody().nome());
        assertEquals("53111112222", response.getBody().telefone());
    }

    @Test
    @DisplayName("Deve deletar um cliente com sucesso")
    void deleteCliente_quandoExiste_retornaNoContent() {
        // ARRANGE
        Cliente savedCliente = clienteRepository.save(clienteAluno);
        Long id = savedCliente.getCodigo();

        // ACT
        ResponseEntity<Void> deleteResponse = delete("/api/v1/clientes/" + id, Void.class);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verifica se o cliente foi realmente deletado
        ResponseEntity<Map> getResponse = get("/api/v1/clientes/" + id, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}