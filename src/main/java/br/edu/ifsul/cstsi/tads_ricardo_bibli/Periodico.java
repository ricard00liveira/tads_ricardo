package br.edu.ifsul.cstsi.tads_ricardo_bibli;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Periodico extends Exemplar {

    private String editora;
}
