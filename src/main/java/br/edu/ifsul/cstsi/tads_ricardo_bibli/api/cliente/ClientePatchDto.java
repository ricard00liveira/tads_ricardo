package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente;

import java.io.Serializable;

public record ClientePatchDto(
        String nome,
        Integer idade,
        Integer telefone,
        String endereco
) implements Serializable {
}
