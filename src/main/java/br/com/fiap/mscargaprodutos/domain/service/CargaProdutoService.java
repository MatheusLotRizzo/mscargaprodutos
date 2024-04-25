package br.com.fiap.mscargaprodutos.domain.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CargaProdutoService {

    public String receberArquivoCargaProdutos(MultipartFile arquivo) throws IOException {
        String extensaoArquivo = extrairExtensao(arquivo.getOriginalFilename());

        if (!extensaoArquivo.equals("csv")) {
            throw new IllegalArgumentException("Extensão do arquivo inválida");
        }

        String caminho = new ClassPathResource("/src/main/resources/arquivoscarga/").getPath();
        Path path = Paths.get(caminho + "carga-produtos." + extensaoArquivo);

        Files.deleteIfExists(path);
        Files.copy(arquivo.getInputStream(), path);
        return "Arquivo salvo com sucesso";
    }

    private String extrairExtensao(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}
