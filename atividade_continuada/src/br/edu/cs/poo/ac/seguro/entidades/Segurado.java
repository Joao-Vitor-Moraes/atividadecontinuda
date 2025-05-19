package br.edu.cs.poo.ac.seguro.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Segurado implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String nome;
	private Endereco endereco;
	private LocalDate dataCriacao;
	private BigDecimal bonus;
	
	public Segurado(String nome,Endereco endereco,LocalDate dataCriacao,BigDecimal bonus) {
		this.nome=nome;
		this.endereco=endereco;
		this.dataCriacao=dataCriacao;
		this.bonus=bonus;
	}
	
	
	public String getNome() {
		return nome;
	}


	public Endereco getEndereco() {
		return endereco;
	}


	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	protected LocalDate getDataCriacao() {
		return dataCriacao;
	}


	protected void setDataCriacao(LocalDate dataCriacao) {
		this.dataCriacao = dataCriacao;
	}


	public BigDecimal getBonus() {
		return bonus;
	}
	
	public int getIdade() {
		LocalDate dataAtual = LocalDate.now();
        int idade = dataAtual.getYear() - getDataCriacao().getYear();
        if (dataAtual.getDayOfYear() < getDataCriacao().getDayOfYear()) {
            idade--;
        }
        return idade;
	}
	
	public void creditarBonus(BigDecimal valor) {
	    if (valor.compareTo(BigDecimal.ZERO) > 0) {
	        bonus = bonus.add(valor);
	    }
	}
	public void debitarBonus(BigDecimal valor) {
	    if (valor.compareTo(BigDecimal.ZERO) > 0 && bonus.compareTo(valor) >= 0) {
	        bonus = bonus.subtract(valor);
	    }
	}

}
