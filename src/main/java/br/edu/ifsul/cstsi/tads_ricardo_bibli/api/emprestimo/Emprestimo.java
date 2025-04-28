package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.emprestimo;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.exemplar.Exemplar;
import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente.Cliente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;

    // Muitos empréstimos para um cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Muitos empréstimos para um exemplar
    @ManyToOne
    @JoinColumn(name = "exemplar_id")
    private Exemplar exemplar;
}
