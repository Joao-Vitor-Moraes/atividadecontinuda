package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.daos.SeguradoEmpresaDAO;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;

public class SeguradoEmpresaMediator {
	private SeguradoMediator seguradoMediator = SeguradoMediator.getInstancia();
	private SeguradoEmpresaDAO seguradoEmpresaDAO = new SeguradoEmpresaDAO();

	private static final SeguradoEmpresaMediator instancia = new SeguradoEmpresaMediator();

	public static SeguradoEmpresaMediator getInstancia() {
		return instancia;
	}

	public String validarCnpj(String cnpj) {
		
		if (StringUtils.ehNuloOuBranco(cnpj)) {
			return "CNPJ deve ser informado";
		}
		if (!StringUtils.temSomenteNumeros(cnpj)) {
			return "CNPJ deve conter apenas números.";
		}
		if (cnpj.length() != 14) {
			return "CNPJ deve ter 14 caracteres";
		}
		if (!ValidadorCpfCnpj.ehCnpjValido(cnpj)) {
			return "CNPJ com dígito inválido";
		}
		return null;
	}

	public String validarFaturamento(double faturamento) {
		if (faturamento <= 0) {
			return "Faturamento deve ser maior que zero";
		}
		return null;
	}

	public String incluirSeguradoEmpresa(SeguradoEmpresa seg) {
		String msg = validarSeguradoEmpresa(seg);
		if (msg != null) {
			return msg;
		}
		if (seguradoEmpresaDAO.incluir(seg)) {
			return null;
		} else {
			return "CNPJ do segurado empresa já existente";
		}
	}

	public String alterarSeguradoEmpresa(SeguradoEmpresa seg) {
		String msg = validarSeguradoEmpresa(seg);
		if (msg != null) {
			return msg;
		}
		if (seguradoEmpresaDAO.alterar(seg)) {
			return null;
		} else {
			return "CNPJ do segurado empresa não existente";
		}
	}

	public String excluirSeguradoEmpresa(String cnpj) {
		String msg = validarCnpj(cnpj);
		if (msg != null) {
			return msg;
		}
		if (!seguradoEmpresaDAO.excluir(cnpj)) {
			return "CNPJ do segurado empresa não existente";
		}
		return null;
	}

	public SeguradoEmpresa buscarSeguradoEmpresa(String cnpj) {
	    return seguradoEmpresaDAO.buscar(cnpj);
	}

	public String validarSeguradoEmpresa(SeguradoEmpresa seg) {
		if (seg == null) {
			return "Segurado não pode ser nulo.";
		}

		String msg = seguradoMediator.validarNome(seg.getNome());
		if (msg != null) return msg;

		msg = seguradoMediator.validarEndereco(seg.getEndereco());
		if (msg != null) return msg;

		msg = seguradoMediator.validarDataCriacao(seg.getDataAbertura());
		if (msg != null) return "Data da abertura deve ser informada";

		msg = validarCnpj(seg.getCnpj());
		if (msg != null) return msg;

		msg = validarFaturamento(seg.getFaturamento());
		if (msg != null) return msg;

		return null;
	}
}
