package br.com.fiap.mscargaprodutos.domain.controllers;

import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerOk;
import br.com.fiap.estrutura.utils.MessageErrorHandler;
import br.com.fiap.estrutura.utils.SpringControllerUtils;
import br.com.fiap.mscargaprodutos.domain.entities.JobLaunchRequest;
import br.com.fiap.mscargaprodutos.domain.service.CargaProdutoService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("carga/produtos")
public class CargaProdutoController {

    @Autowired
    private CargaProdutoService cargaProdutoService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private ApplicationContext context;

    @PostMapping
    @ApiResponseSwaggerOk
    public ResponseEntity<?> carregarCargaProdutos(@RequestParam("arquivo")MultipartFile arquivo) {
        return SpringControllerUtils.response(HttpStatus.OK, () -> cargaProdutoService.receberArquivoCargaProdutos(arquivo));
    }

    @PostMapping("/executar")
    public ResponseEntity<?> executarCargaProdutos(@RequestBody JobLaunchRequest request) {
        try {
            Job job = this.context.getBean(request.getName(), Job.class);

            JobParameters jobParameters = new JobParametersBuilder(request.getJobParameters(), this.jobExplorer)
                    .getNextJobParameters(job)
                    .toJobParameters();

            return ResponseEntity.ok().body(this.jobLauncher.run(job, jobParameters).getExitStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(MessageErrorHandler.create("Erro ao tentar executar a carga de produtos"));
        }
    }
}