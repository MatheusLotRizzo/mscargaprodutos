package br.com.fiap.mscargaprodutos.domain.entities;

import br.com.fiap.estrutura.exception.BusinessException;
import jakarta.persistence.*;

@Table(name = "produtos")
@Entity
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String descricao;
    private int quantidadeEstoque;
    private double preco;

    public ProdutoEntity() {}

    public ProdutoEntity(String nome, String descricao, int quantidadeEstoque, double preco) throws BusinessException {
        validacoesPadroes(nome, descricao, quantidadeEstoque, preco);
        this.nome = nome;
        this.descricao = descricao;
        this.quantidadeEstoque = quantidadeEstoque;
        this.preco = preco;
    }

    public ProdutoEntity(int id, String nome, String descricao, int quantidadeEstoque, double preco) throws BusinessException {
        validacoesPadroes(nome, descricao, quantidadeEstoque, preco);
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.quantidadeEstoque = quantidadeEstoque;
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public double getPreco() {
        return preco;
    }

    private void validacoesPadroes(String nome, String descricao, int quantidadeEstoque, double preco) throws BusinessException {
        if (nome == null || nome.isEmpty()) {
            throw new BusinessException("Nome do produto não pode ser nulo ou vazio");
        }

        if (descricao == null || descricao.isEmpty()) {
            throw new BusinessException("Descrição do produto não pode ser nula ou vazia");
        }

        if (quantidadeEstoque < 0) {
            throw new BusinessException("Quantidade em estoque não pode ser negativa");
        }

        if (preco <= 0) {
            throw new BusinessException("Preço do produto não pode ser menor ou igual a zero");
        }
    }
}