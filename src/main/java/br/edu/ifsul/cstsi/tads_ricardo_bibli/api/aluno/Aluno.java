package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.aluno;

import br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente.Cliente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("ALUNO")
public class Aluno extends Cliente {}
