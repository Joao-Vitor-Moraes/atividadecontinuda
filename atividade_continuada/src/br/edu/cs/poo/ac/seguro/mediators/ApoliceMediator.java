package br.edu.cs.poo.ac.seguro.mediators;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.math.RoundingMode;
import java.util.Arrays;
import br.edu.cs.poo.ac.seguro.daos.ApoliceDAO;
import br.edu.cs.poo.ac.seguro.daos.SeguradoEmpresaDAO;
import br.edu.cs.poo.ac.seguro.daos.SeguradoPessoaDAO;
import br.edu.cs.poo.ac.seguro.daos.SinistroDAO;
import br.edu.cs.poo.ac.seguro.daos.VeiculoDAO;
import br.edu.cs.poo.ac.seguro.entidades.Apolice;
import br.edu.cs.poo.ac.seguro.entidades.CategoriaVeiculo;
import br.edu.cs.poo.ac.seguro.entidades.PrecoAno;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;
import br.edu.cs.poo.ac.seguro.entidades.Sinistro;
import br.edu.cs.poo.ac.seguro.entidades.Veiculo;

public class ApoliceMediator {
	private static ApoliceMediator instancia;
	private SeguradoPessoaDAO daoSegPes;
	private SeguradoEmpresaDAO daoSegEmp;
	private VeiculoDAO daoVel;
	private ApoliceDAO daoApo;
	private SinistroDAO daoSin;

	private ApoliceMediator() {
	    this.daoSegPes = new SeguradoPessoaDAO();
	    this.daoSegEmp = new SeguradoEmpresaDAO();
	    this.daoVel    = new VeiculoDAO();
	    this.daoApo    = new ApoliceDAO();
	    this.daoSin    = new SinistroDAO();
	}
	
	public static ApoliceMediator getInstancia() {
        if (instancia == null) {
            instancia = new ApoliceMediator();
        }
        return instancia;
    }

	public RetornoInclusaoApolice incluirApolice(DadosVeiculo dados) {
        String erro = validarTodosDadosVeiculo(dados);
        if (erro != null) {
            return new RetornoInclusaoApolice(null, erro);
        }
        erro = validarCpfCnpjValorMaximo(dados);
        if (erro != null) {
            return new RetornoInclusaoApolice(null, erro);
        }
        String cpfOuCnpj = dados.getCpfOuCnpj().trim();
        boolean isCpf = cpfOuCnpj.length() == 11;
        SeguradoPessoa segPes = null;
        SeguradoEmpresa segEmp = null;
        if (isCpf) {
        	
        	if (!ValidadorCpfCnpj.ehCpfValido(cpfOuCnpj)) {
                return new RetornoInclusaoApolice(null, "CPF inválido");
            }
        	
            segPes = daoSegPes.buscar(cpfOuCnpj);

            if (segPes == null) {
                return new RetornoInclusaoApolice(null, "CPF inexistente no cadastro de pessoas");
            }
        } else {
        	if (!ValidadorCpfCnpj.ehCnpjValido(cpfOuCnpj)) {
                return new RetornoInclusaoApolice(null, "CNPJ inválido");
            }
        	
            segEmp = daoSegEmp.buscar(cpfOuCnpj);
            
            if (segEmp == null) {
                return new RetornoInclusaoApolice(null, "CNPJ inexistente no cadastro de empresas");
            }
        }
        String placa = dados.getPlaca().trim();
        Veiculo veiculo = daoVel.buscar(placa);
        CategoriaVeiculo categoria = CategoriaVeiculo.values()[dados.getCodigoCategoria()];
        if (veiculo == null) {
            veiculo = new Veiculo(placa, dados.getAno(), segEmp, segPes, categoria);
            daoVel.incluir(veiculo);
        } else {
            veiculo.setProprietarioEmpresa(segEmp);
            veiculo.setProprietarioPessoa(segPes);
            daoVel.alterar(veiculo);
        }
        int anoAtual = LocalDate.now().getYear();
        String numero;
        if (isCpf) {
            numero = anoAtual + "000" + cpfOuCnpj + placa;
        } else {
            numero = anoAtual + cpfOuCnpj + placa;
        }
        if (daoApo.buscar(numero) != null) {
            return new RetornoInclusaoApolice(null, "Apólice já existente para ano atual e veículo");
        }
 
        BigDecimal vpa = dados.getValorMaximoSegurado()
                .multiply(new BigDecimal("0.03"))
                .setScale(2, RoundingMode.HALF_UP);
        boolean isEmpresa = !isCpf;
        BigDecimal vpb = (isEmpresa && segEmp.isEhLocadoraDeVeiculos())
                ? vpa.multiply(new BigDecimal("1.2")).setScale(2, RoundingMode.HALF_UP)
                : vpa;
        BigDecimal bonusAtual = isCpf ? segPes.getBonus() : segEmp.getBonus();
        BigDecimal vpc = vpb.subtract(bonusAtual.divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal premio = vpc.compareTo(BigDecimal.ZERO) > 0 ? vpc : BigDecimal.ZERO;
        BigDecimal franquia = vpb.multiply(new BigDecimal("1.3"))
                .setScale(2, RoundingMode.HALF_UP);
        Apolice ap = new Apolice(numero, veiculo, franquia, premio, dados.getValorMaximoSegurado(), LocalDate.now());
        daoApo.incluir(ap);
        Sinistro[] sinistros = daoSin.buscarTodos();
        final Veiculo veiculoFinal = veiculo;
        final int anoAnterior = ap.getDataInicioVigencia().getYear() - 1;

        boolean possuiSinistroAnoAnterior = Arrays.stream(sinistros)
                .anyMatch(s -> s.getVeiculo().equals(veiculoFinal)
                        && s.getDataHoraSinistro().getYear() == anoAnterior);
        if (!possuiSinistroAnoAnterior) {
            BigDecimal incremento = premio.multiply(new BigDecimal("0.3"))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal novoBonus = bonusAtual.add(incremento).setScale(2, RoundingMode.HALF_UP);

            if (isCpf) {
                segPes = new SeguradoPessoa(
                    segPes.getNome(),
                    segPes.getEndereco(),
                    segPes.getDataNascimento(),
                    novoBonus,
                    segPes.getCpf(),
                    segPes.getRenda()
                );
                daoSegPes.alterar(segPes);
            } else {
                segEmp = new SeguradoEmpresa(
                    segEmp.getNome(),
                    segEmp.getEndereco(),
                    segEmp.getDataAbertura(),
                    novoBonus,
                    segEmp.getCnpj(),
                    segEmp.getFaturamento(),
                    segEmp.isEhLocadoraDeVeiculos()
                );
                daoSegEmp.alterar(segEmp);
            }
        }
        return new RetornoInclusaoApolice(numero, null);
    }

    public Apolice buscarApolice(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return null;
        }
        return daoApo.buscar(numero);
    }

    public String excluirApolice(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return "Número deve ser informado";
        }
        Apolice ap = daoApo.buscar(numero);
        if (ap == null) {
            return "Apólice inexistente";
        }
        Sinistro[] sinistros = daoSin.buscarTodos();
        boolean bloqueia = Arrays.stream(sinistros)
                .anyMatch(s -> s.getVeiculo().equals(ap.getVeiculo())
                        && s.getDataHoraSinistro().getYear() == ap.getDataInicioVigencia().getYear());
        if (bloqueia) {
            return "Existe sinistro cadastrado para o veículo em questão "
                    + "e no mesmo ano da apólice";
        }
        daoApo.excluir(numero);
        return null;
    }

    private String validarTodosDadosVeiculo(DadosVeiculo dados) {
        if (dados == null) {
            return "Dados do veículo devem ser informados";
        }
        if (dados.getCpfOuCnpj() == null || dados.getCpfOuCnpj().trim().isEmpty()) {
            return "CPF ou CNPJ deve ser informado";
        }
        String id = dados.getCpfOuCnpj().trim();
        if (!(id.matches("\\d{11}") || id.matches("\\d{14}"))) {
            return id.length() == 11 ? "CPF inválido" : "CNPJ inválido";
        }
        if (dados.getPlaca() == null || dados.getPlaca().trim().isEmpty()) {
            return "Placa do veículo deve ser informada";
        }
        int ano = dados.getAno();
        int atual = LocalDate.now().getYear();
        if (ano < 2020 || ano > atual) {
            return "Ano tem que estar entre 2020 e " + atual + ", incluindo estes";
        }
        if (dados.getValorMaximoSegurado() == null) {
            return "Valor máximo segurado deve ser informado";
        }
        int codCat = dados.getCodigoCategoria();
        CategoriaVeiculo[] cats = CategoriaVeiculo.values();
        if (codCat < 0 || codCat >= cats.length) {
            return "Categoria inválida";
        }
        return null;
    }

    private String validarCpfCnpjValorMaximo(DadosVeiculo dados) {
        BigDecimal valor = dados.getValorMaximoSegurado();
        BigDecimal ref = obterValorMaximoPermitido(dados.getAno(), dados.getCodigoCategoria());
        BigDecimal min = ref.multiply(new BigDecimal("0.75")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal max = ref.setScale(2, RoundingMode.HALF_UP);
        if (valor.compareTo(min) < 0 || valor.compareTo(max) > 0) {
            return "Valor máximo segurado deve estar entre 75% e 100% do valor do carro encontrado na categoria";
        }
        return null;
    }

    private BigDecimal obterValorMaximoPermitido(int ano, int codigoCat) {
        CategoriaVeiculo cat = CategoriaVeiculo.values()[codigoCat];
        for (PrecoAno pa : cat.getPrecosAnos()) {
            if (pa.getAno() == ano) {
                return BigDecimal.valueOf(pa.getPreco()).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}