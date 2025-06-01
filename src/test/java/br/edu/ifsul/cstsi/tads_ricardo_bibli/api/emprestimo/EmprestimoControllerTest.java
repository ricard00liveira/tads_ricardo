package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.emprestimo;

import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente.Cliente;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente.ClienteRepository;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.exemplar.Exemplar;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.exemplar.ExemplarRepository;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.aluno.Aluno;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.livro.Livro;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.livro.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ExemplarRepository exemplarRepository;
    
    @Autowired
    private LivroRepository livroRepository;

    private Cliente testCliente;
    private Exemplar testExemplar;

    @BeforeEach
    void setUp() {
        // Create a test client
        Aluno aluno = new Aluno();
        aluno.setNome("Test Aluno for Emprestimo");
        aluno.setIdade(20);
        aluno.setTelefone("1234567890");
        aluno.setEndereco("Test Address");
        testCliente = clienteRepository.save(aluno);

        // Create a test exemplar (using Livro as concrete implementation)
        Livro livro = new Livro();
        livro.setNome("Test Exemplar");
        livro.setAutor("Test Author");
        livro.setEditora("Test Publisher");
        livro.setEdicao(1);
        testExemplar = livroRepository.save(livro);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetAllEmprestimos() throws Exception {
        mockMvc.perform(get("/api/v1/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetEmprestimoById() throws Exception {
        // First create a loan to ensure we have one to retrieve
        EmprestimoPostDto postDto = new EmprestimoPostDto(
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                testCliente.getCodigo(),
                testExemplar.getCodigo()
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn();

        EmprestimoDto createdEmprestimo = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                EmprestimoDto.class
        );

        // Now retrieve the loan by ID
        mockMvc.perform(get("/api/v1/emprestimos/" + createdEmprestimo.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdEmprestimo.id()))
                .andExpect(jsonPath("$.cliente.codigo").value(testCliente.getCodigo()))
                .andExpect(jsonPath("$.exemplar.codigo").value(testExemplar.getCodigo()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnNotFoundForNonExistentEmprestimoId() throws Exception {
        mockMvc.perform(get("/api/v1/emprestimos/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateEmprestimo() throws Exception {
        EmprestimoPostDto postDto = new EmprestimoPostDto(
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                testCliente.getCodigo(),
                testExemplar.getCodigo()
        );

        mockMvc.perform(post("/api/v1/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataEmprestimo").isNotEmpty())
                .andExpect(jsonPath("$.dataDevolucao").isNotEmpty())
                .andExpect(jsonPath("$.cliente.codigo").value(testCliente.getCodigo()))
                .andExpect(jsonPath("$.exemplar.codigo").value(testExemplar.getCodigo()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldRejectInvalidEmprestimoCreation() throws Exception {
        // Missing required fields
        EmprestimoPostDto invalidDto = new EmprestimoPostDto(
                null,  // Missing required dataEmprestimo
                LocalDate.now().minusDays(7), // Invalid dataDevolucao (in the past)
                null,  // Missing required clienteId
                null   // Missing required exemplarId
        );

        mockMvc.perform(post("/api/v1/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateEmprestimoPartially() throws Exception {
        // First create a loan
        EmprestimoPostDto postDto = new EmprestimoPostDto(
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                testCliente.getCodigo(),
                testExemplar.getCodigo()
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn();

        EmprestimoDto createdEmprestimo = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                EmprestimoDto.class
        );

        // Now update just the return date
        LocalDate newReturnDate = LocalDate.now().plusDays(14);
        EmprestimoPatchDto patchDto = new EmprestimoPatchDto(
                null,
                newReturnDate,
                null,
                null
        );

        mockMvc.perform(patch("/api/v1/emprestimos/" + createdEmprestimo.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataDevolucao").value(newReturnDate.toString()))
                .andExpect(jsonPath("$.cliente.codigo").value(testCliente.getCodigo())) // Unchanged
                .andExpect(jsonPath("$.exemplar.codigo").value(testExemplar.getCodigo())); // Unchanged
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteEmprestimo() throws Exception {
        // First create a loan
        EmprestimoPostDto postDto = new EmprestimoPostDto(
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                testCliente.getCodigo(),
                testExemplar.getCodigo()
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn();

        EmprestimoDto createdEmprestimo = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                EmprestimoDto.class
        );

        // Now delete the loan
        mockMvc.perform(delete("/api/v1/emprestimos/" + createdEmprestimo.id()))
                .andExpect(status().isNoContent());

        // Verify it's deleted by trying to get it
        mockMvc.perform(get("/api/v1/emprestimos/" + createdEmprestimo.id()))
                .andExpect(status().isNotFound());
    }
}