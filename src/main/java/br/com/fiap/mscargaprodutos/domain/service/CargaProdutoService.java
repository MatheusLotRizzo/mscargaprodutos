package br.com.fiap.mscargaprodutos.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CargaProdutoService {

    public String receberArquivoCargaProdutos(MultipartFile arquivo) throws IOException {
        String extensaoArquivo = extrairExtensao(arquivo.getOriginalFilename());

        if (!extensaoArquivo.equals("csv")) {
            throw new IllegalArgumentException("Extensão do arquivo inválida");
        }

        String path = "C:/Users/mathe/Downloads/";
        String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));

        String caminho = path + "carga-" + dataAtual + "." + extensaoArquivo;
        Files.copy(arquivo.getInputStream(), Paths.get(caminho));
        return "Arquivo salvo com sucesso";
    }

    private String extrairExtensao(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}
