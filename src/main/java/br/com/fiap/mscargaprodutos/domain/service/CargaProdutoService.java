package br.com.fiap.mscargaprodutos.domain.service;

import br.com.fiap.estrutura.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CargaProdutoService {

    @Value("${caminho.base}")
    private String caminhoBase;

    public String receberArquivoCargaProdutos(MultipartFile arquivo) throws BusinessException {
        try{
            validaArquivo(arquivo);
            Path path = Paths.get(new ClassPathResource(caminhoBase).getPath() + "carga-produtos.csv");
            Files.deleteIfExists(path);
            Files.copy(arquivo.getInputStream(), path);
            return "Arquivo salvo com sucesso";
        }catch (IOException e){
            throw new BusinessException("Erro ao salvar arquivo");
        }catch (IllegalArgumentException e){
            throw new BusinessException(e.getMessage());
        }
    }

    private void validaArquivo(MultipartFile arquivo) {
        if (arquivo.isEmpty() || arquivo == null) {
            throw new IllegalArgumentException("Arquivo não enviado");
        }

        String extensaoArquivo = extrairExtensao(arquivo.getOriginalFilename());
        if (!extensaoArquivo.equals("csv")) {
            throw new IllegalArgumentException("Extensão do arquivo inválida");
        }
    }

    private String extrairExtensao(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}