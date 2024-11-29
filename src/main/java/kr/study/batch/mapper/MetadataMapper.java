package kr.study.batch.mapper;

import kr.study.batch.vo.ColumnMetadata;
import kr.study.batch.vo.PrimaryKeyMetadata;
import kr.study.batch.vo.TableMetadata;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataMapper {
    TableMetadata fetchTableMetadata();

    void insertTableMetadata(TableMetadata tableMetadata);

    ColumnMetadata fetchColumnMetadata();

    void insertColumnMetadata(ColumnMetadata columnMetadata);

    PrimaryKeyMetadata fetchPrimaryKeyMetadata();
}
