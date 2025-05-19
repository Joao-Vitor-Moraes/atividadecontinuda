package br.edu.cs.poo.ac.seguro.entidades;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter
@Setter
@RequiredArgsConstructor

public class Sinistro implements Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
    private String numero;
    @NonNull
    private Veiculo veiculo;
    @NonNull
    private LocalDateTime dataHoraSinistro;
    @NonNull
    private LocalDateTime dataHoraRegistro;
    @NonNull
    private String usuarioRegistro;
    @NonNull
    private BigDecimal valorSinistro;
    @NonNull
    private TipoSinistro tipo;


}
