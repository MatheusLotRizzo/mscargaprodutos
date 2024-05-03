package br.com.fiap.mscargaprodutos.domain.service;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.mscargaprodutos.domain.entities.JobLaunchRequest;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private ApplicationContext context;

    public String receberArquivoCargaProdutos(MultipartFile arquivo) throws BusinessException {
        try{
            validaArquivo(arquivo);
            Path path = Paths.get(new ClassPathResource(caminhoBase).getPath() + "carga-produtos.csv");
            Files.deleteIfExists(path);
            Files.copy(arquivo.getInputStream(), path);
            return "Arquivo salvo com sucesso";
        }catch (IOException e){
            throw new BusinessException("Erro ao salvar arquivo");
        }
    }

    public ExitStatus executarCargaProdutos(JobLaunchRequest request) throws BusinessException {
        try{
            Job job = this.context.getBean(request.getName(), Job.class);

            JobParameters jobParameters = new JobParametersBuilder(request.getJobParameters(), this.jobExplorer)
                    .getNextJobParameters(job)
                    .toJobParameters();
            return this.jobLauncher.run(job, jobParameters).getExitStatus();
        }catch (Exception e) {
            throw new BusinessException("Erro ao tentar executar a carga de produtos");
        }
    }

    private void validaArquivo(MultipartFile arquivo) throws BusinessException {
        if (arquivo.isEmpty() || arquivo == null) {
            throw new BusinessException("Arquivo não enviado");
        }

        String extensaoArquivo = extrairExtensao(arquivo.getOriginalFilename());
        if (!extensaoArquivo.equals("csv")) {
            throw new BusinessException("Extensão do arquivo inválida");
        }
    }

    private String extrairExtensao(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}