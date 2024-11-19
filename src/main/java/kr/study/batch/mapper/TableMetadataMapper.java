package kr.study.batch.mapper;

import kr.study.batch.vo.TableMetadata;
import org.springframework.stereotype.Repository;

@Repository
public interface TableMetadataMapper {
    TableMetadata fetchTableMetadata();

    void insertTableMetadata(TableMetadata tableMetadata);
}
