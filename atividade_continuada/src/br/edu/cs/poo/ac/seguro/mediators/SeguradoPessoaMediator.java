package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.daos.SeguradoPessoaDAO;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SeguradoPessoaMediator {
    private SeguradoMediator seguradoMediator = SeguradoMediator.getInstancia();
    private SeguradoPessoaDAO seguradoPessoaDAO = new SeguradoPessoaDAO();

    private static final SeguradoPessoaMediator instancia = new SeguradoPessoaMediator();

    public static SeguradoPessoaMediator getInstancia() {
        return instancia;
    }

    public String validarCpf(String cpf) {
        if (StringUtils.ehNuloOuBranco(cpf)) {
            return "CPF deve ser informado";
        }
        if (!StringUtils.temSomenteNumeros(cpf)) {
            return "CPF deve conter apenas números.";
        }
        if (cpf.length() != 11) {
            return "CPF deve ter 11 caracteres";
        }
        if (!ValidadorCpfCnpj.ehCpfValido(cpf)) {
            return "CPF com dígito inválido";
        }
        return null;
    }

    public String validarRenda(double renda) {
        if (renda < 0) {
            return "Renda deve ser maior ou igual à zero";
        }
        return null;
    }
    
    public String validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return "Data do nascimento deve ser informada";
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            return "Data do nascimento deve ser menor ou igual à data atual";
        }
        return null;
    }

    public String incluirSeguradoPessoa(SeguradoPessoa seg) {
        String msg = validarSeguradoPessoa(seg);
        if (msg != null) {
            return msg;
        }
        if (seguradoPessoaDAO.incluir(seg)) {
            return null;
        } else {
            return "CPF do segurado pessoa já existente";
        }
    }

    public String alterarSeguradoPessoa(SeguradoPessoa seg) {
        String msg = validarSeguradoPessoa(seg);
        if (msg != null) {
            return msg;
        }
        if (seguradoPessoaDAO.alterar(seg)) {
            return null;
        } else {
            return "CPF do segurado pessoa não existente";
        }
    }

    public String excluirSeguradoPessoa(String cpf) {
        String msg = validarCpf(cpf);
        if (msg != null) {
        	return "CPF do segurado pessoa não existente";
        }
        if (!seguradoPessoaDAO.excluir(cpf)) {
            return null;
        }
        return null;
    }

    public SeguradoPessoa buscarSeguradoPessoa(String cpf) {
        String msg = validarCpf(cpf);
        if (msg != null) {
            return null;
        }else {
        	return seguradoPessoaDAO.buscar(cpf);
        }
    }

    public String validarSeguradoPessoa(SeguradoPessoa seg) {
        if (seg == null) {
            return "Segurado não pode ser nulo.";
        }

        String msg = seguradoMediator.validarNome(seg.getNome());
        if (msg != null) return msg;

        msg = seguradoMediator.validarEndereco(seg.getEndereco());
        if (msg != null) return msg;

        msg = validarDataNascimento(seg.getDataNascimento());
        if (msg != null) return msg;

        msg = validarCpf(seg.getCpf());
        if (msg != null) return msg;

        msg = validarRenda(seg.getRenda());
        if (msg != null) return msg;

        return null;
    }
}