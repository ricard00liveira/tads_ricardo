package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente;

import java.io.Serializable;

/**
 * DTO for {@link Cliente}
 */
public record ClienteDto(Long codigo, String nome, Integer idade, Integer telefone, String endereco)
        implements Serializable {
    public ClienteDto(Cliente cliente) {
        this(cliente.getCodigo(), cliente.getNome(), cliente.getIdade(), cliente.getTelefone(), cliente.getEndereco());
    }
}
