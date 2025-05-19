package br.edu.cs.poo.ac.seguro.entidades;

public enum TipoSinistro {
	
	COLISAO(1,"Colisão"),
	INCENDIO(2,"Incêndio"),
	FURTO(3, "Furto"),
	ENCHENTE(4, "Enchente"),
	DEPREDACAO(5, "Depredação");
	
	private int codigo;
	private String nome;
	
	
	
	private TipoSinistro(int codigo,String nome) {
		this.codigo=codigo;
		this.nome=nome;
	}



	public int getCodigo() {
		return codigo;
	}



	public String getNome() {
		return nome;
	}
	
	public static TipoSinistro getTipoSinistro(int codigo) {
		if(codigo==1) {
			return COLISAO;
		}else if(codigo == 2) {
			return INCENDIO;
		}else if(codigo == 3) {
			return FURTO;
		}else if(codigo == 4) {
			return ENCHENTE;
		}else if(codigo == 5) {
			return DEPREDACAO;
		}else {
			return null;
		}
	}
}
