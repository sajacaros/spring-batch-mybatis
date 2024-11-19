package kr.study.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import kr.study.batch.service.EmbeddingService;
import kr.study.batch.vo.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class BatchConfigure {

    private static final int PAGE_SIZE = 100;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job tableMetadataJob(JobRepository jobRepository, Step embeddingStep) {
        log.info("===== tableMetadataJob =====");

        return new JobBuilder("tableMetadataJob", jobRepository)
                .start(embeddingStep)
                .build();
    }

    @Bean
    @JobScope
    public Step embeddingStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              MyBatisPagingItemReader<TableMetadata> reader,
                              ItemProcessor<TableMetadata, TableMetadata> processor,
                              MyBatisBatchItemWriter<TableMetadata> writer
    ) {
        log.info("===== embeddingStep =====");
        return new StepBuilder("embeddingStep", jobRepository)
                .<TableMetadata, TableMetadata>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<TableMetadata> tableMetadataReader(
            @Value("${datasource.source.url}") String url,
            @Value("${datasource.source.username}") String username,
            @Value("${datasource.source.password}") String password,
            @Value("${datasource.source.driver-class-name}") String driverClassName

    ) throws Exception {
        log.info("===== reader =====");
        return new MyBatisPagingItemReaderBuilder<TableMetadata>()
                .sqlSessionFactory(
                        createSqlSessionFactory(
                                createDataSource(url, username, password, driverClassName)
                        )
                )
                .pageSize(PAGE_SIZE)
                .queryId("fetchTableMetadata")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<TableMetadata, TableMetadata> processor(EmbeddingService embeddingService) {
        log.info("===== processor =====");
        final AtomicInteger counter = new AtomicInteger(0);
        return item -> {
            item.setEmbedding(embeddingService.embedding(item.getDescription()));
            log.info("{}] processor, item : {}", counter.getAndIncrement(), item);
            return item;
        };
    }

    @Bean
    @StepScope
    public MyBatisBatchItemWriter<TableMetadata> tableMetadataWriter(
            @Value("${datasource.target.url}") String url,
            @Value("${datasource.target.username}") String username,
            @Value("${datasource.target.password}") String password,
            @Value("${datasource.target.driver-class-name}") String driverClassName
    ) throws Exception {
        log.info("===== writer =====");
        return new MyBatisBatchItemWriterBuilder<TableMetadata>()
                .sqlSessionFactory(
                        createSqlSessionFactory(
                                createDataSource(url, username, password, driverClassName)
                        )
                )
                .statementId("insertTableMetadata")
                .build();
    }

    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        // MyBatis XML Mapper 설정이 있다면 추가
        sessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*.xml")
        );

        return sessionFactoryBean.getObject();
    }

    private DataSource createDataSource(String url, String username, String password, String driverClassName) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        return dataSource;
    }
}

