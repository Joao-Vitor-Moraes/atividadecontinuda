package br.edu.cs.poo.ac.seguro.entidades;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Veiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    private String placa;

    private int ano;
    private SeguradoEmpresa proprietarioEmpresa;
    private SeguradoPessoa proprietarioPessoa;
    private transient CategoriaVeiculo categoria;
}