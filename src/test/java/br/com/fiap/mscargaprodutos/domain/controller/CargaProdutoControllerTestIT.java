package br.com.fiap.mscargaprodutos.domain.controller;

import br.com.fiap.mscargaprodutos.domain.entities.JobLaunchRequest;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Profile("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CargaProdutoControllerTestIT {
    @LocalServerPort
    private int porta;

    @BeforeEach
    void setUp() {
        RestAssured.port = porta;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class enviarArquivoCarga{

        @Test
        void deveReceberArquivoCargaProdutosCorreto(){
            ClassLoader classLoader = CargaProdutoControllerTestIT.class.getClassLoader();
            File file = new File(classLoader.getResource("produtos.csv").getFile());

            given()
                    .multiPart("arquivo", file)
                    .when()
                        .post("/carga/produtos")
                    .then()
                        .statusCode(HttpStatus.SC_OK);
        }

        @Test
        void naoDeveReceberRequisicaoSemArquivo(){
            given()
                    .when()
                        .post("/carga/produtos")
                    .then()
                        .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                        //.body("message", is("Arquivo não enviado"));

            ClassLoader classLoader = CargaProdutoControllerTestIT.class.getClassLoader();
            File file = new File(classLoader.getResource("arquivoVazio.csv").getFile());

            given()
                    .when()
                    .multiPart("arquivo", file)
                        .post("/carga/produtos")
                    .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .body("message", is("Arquivo não enviado"));
        }

        @Test
        void naoDeveEnviarArquivoCargaProdutosComExtensaoInvalida(){
            ClassLoader classLoader = CargaProdutoControllerTestIT.class.getClassLoader();
            File file = new File(classLoader.getResource("produtos.txt").getFile());

            given()
                    .multiPart("arquivo", file)
                    .when()
                        .post("/carga/produtos")
                    .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .body("message", is("Extensão do arquivo inválida"));
        }
    }

    @Nested
    class executarCargaProdutos{
        @Test
        void deveExecutarCargaProdutos(){
            given()
                    .contentType(ContentType.JSON)
                    .body("""
                        {  
                            "name":"job",
                            "jobParameters":{
                                "foo":"bar",
                                "baz":"quix"
                            }
                        }
                    """)
                    .when()
                        .post("/carga/produtos/executar")
                    .then()
                        .statusCode(HttpStatus.SC_OK)
                        .body("exitCode", is("COMPLETED"));
        }

        @Test
        void naoDeveExecutarCargaProdutosComParametrosInvalidos(){
            given()
                    .contentType(ContentType.JSON)
                    .body("""
                        {  
                            "name":"jobInexistente",
                            "jobParameters":{
                                "foo":"bar",
                                "baz":"quix"
                            }
                        }
                    """)
                    .when()
                    .post("/carga/produtos/executar")
                    .then()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", is("Erro ao tentar executar a carga de produtos"));
        }
    }
}
