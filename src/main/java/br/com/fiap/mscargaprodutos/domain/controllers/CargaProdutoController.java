package br.com.fiap.mscargaprodutos.domain.controllers;

import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerOk;
import br.com.fiap.estrutura.utils.SpringControllerUtils;
import br.com.fiap.mscargaprodutos.domain.entities.JobLaunchRequest;
import br.com.fiap.mscargaprodutos.domain.service.CargaProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("carga/produtos")
public class CargaProdutoController {

    @Autowired
    private CargaProdutoService cargaProdutoService;

    @PostMapping
    @ApiResponseSwaggerOk
    public ResponseEntity<?> carregarCargaProdutos(@RequestParam("arquivo") MultipartFile arquivo) {
        return SpringControllerUtils.response(HttpStatus.OK, () -> cargaProdutoService.receberArquivoCargaProdutos(arquivo));
    }

    @PostMapping("/executar")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> executarCargaProdutos(@RequestBody JobLaunchRequest request) {
        return SpringControllerUtils.response(HttpStatus.OK, () -> cargaProdutoService.executarCargaProdutos(request));
    }
}