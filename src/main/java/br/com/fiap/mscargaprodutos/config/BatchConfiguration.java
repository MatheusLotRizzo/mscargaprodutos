package br.com.fiap.mscargaprodutos.config;

import br.com.fiap.mscargaprodutos.domain.entities.ProdutoEntity;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    @Value("${spring.datasource.url}")
    private String urlBanco;

    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("jobAtualizarProdutos", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                    ItemReader<ProdutoEntity> reader, ItemWriter<ProdutoEntity> writer){
        return new StepBuilder("step", jobRepository)
                .<ProdutoEntity, ProdutoEntity>chunk(32, transactionManager)
                .reader(reader)
                .writer(writer)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public ItemReader<ProdutoEntity> itemReader(){
        BeanWrapperFieldSetMapper<ProdutoEntity> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(ProdutoEntity.class);

        return new FlatFileItemReaderBuilder<ProdutoEntity>()
                .name("produtoReader")
                .resource(new ClassPathResource("arquivoscarga/carga-produtos.csv"))
                .linesToSkip(1)
                .delimited()
                .names("nome", "descricao", "quantidadeEstoque", "preco")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public ItemWriter<ProdutoEntity> itemWriter(@Autowired DataSource dataSource){
        String sql;
        if (urlBanco.contains("postgresql")){
            sql = """
                INSERT INTO produtos (nome, descricao, quantidade_estoque, preco)
                        VALUES (:nome, :descricao, :quantidadeEstoque, :preco)
                        ON CONFLICT (nome)
                                DO UPDATE SET
                        descricao = EXCLUDED.descricao,
                                quantidade_estoque = EXCLUDED.quantidade_estoque,
                                preco = EXCLUDED.preco;
                """;
        }else{
            sql = """
                    MERGE INTO produtos AS target
                    USING (VALUES (:nome, :descricao, :quantidadeEstoque, :preco)) AS source (nome, descricao, quantidade_estoque, preco)
                    ON target.nome = source.nome
                    WHEN MATCHED THEN
                        UPDATE SET target.descricao = source.descricao, target.quantidade_estoque = source.quantidade_estoque, target.preco = source.preco
                    WHEN NOT MATCHED THEN
                        INSERT (nome, descricao, quantidade_estoque, preco) VALUES (source.nome, source.descricao, source.quantidade_estoque, source.preco);
                """;
        }

        return new JdbcBatchItemWriterBuilder<ProdutoEntity>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .sql(sql)
                .build();
    }
}