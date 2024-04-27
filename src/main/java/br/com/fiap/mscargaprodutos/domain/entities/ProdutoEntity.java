package br.com.fiap.mscargaprodutos.domain.entities;

public class ProdutoEntity {
    private String nome;
    private String descricao;
    private int quantidadeEstoque;
    private double preco;

    public ProdutoEntity() {}

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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}