package br.com.fiap.mscargaprodutos.domain.controllers;

import br.com.fiap.estrutura.utils.MessageErrorHandler;
import br.com.fiap.mscargaprodutos.domain.service.CargaProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("carga/produtos")
public class CargaProdutoController {

    @Autowired
    private CargaProdutoService cargaProdutoService;

    @PostMapping
    public ResponseEntity<?> carregarCargaProdutos(@RequestParam("arquivo")MultipartFile arquivo) {
        try {
            return ResponseEntity.ok().body(MessageErrorHandler.create(cargaProdutoService.receberArquivoCargaProdutos(arquivo)));
        }  catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(MessageErrorHandler.create(e.getMessage()));
        } catch (IOException e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(MessageErrorHandler.create("Erro ao Salvar arquivos"));
        }
    }
}
