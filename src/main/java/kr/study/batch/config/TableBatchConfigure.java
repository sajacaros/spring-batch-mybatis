package kr.study.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import kr.study.batch.vo.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class TableBatchConfigure {

    private static final int PAGE_SIZE = 100;
    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job tableMetadataJob(JobRepository jobRepository, @Qualifier("tableEmbeddingStep") Step tableEmbeddingStep) {
        log.info("===== tableMetadataJob =====");

        return new JobBuilder("tableMetadataJob", jobRepository)
                .start(tableEmbeddingStep)
                .build();
    }

    @Bean
    @JobScope
    public Step tableEmbeddingStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              MyBatisPagingItemReader<TableMetadata> reader,
                              ItemProcessor<TableMetadata, TableMetadata> processor,
                              MyBatisBatchItemWriter<TableMetadata> writer
    ) {
        log.info("===== tableEmbeddingStep =====");
        return new StepBuilder("tableEmbeddingStep", jobRepository)
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
    public ItemProcessor<TableMetadata, TableMetadata> processor() {
        log.info("===== processor =====");
        final AtomicInteger counter = new AtomicInteger(0);
        return item -> {
            if (
                    Objects.isNull(item.getDescription())
                            || item.getDescription().isEmpty()
                            || item.getDescription().equals("사용안함")
            ) {
                return null;
            }
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

