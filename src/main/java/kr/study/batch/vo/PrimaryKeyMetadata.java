package kr.study.batch.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PrimaryKeyMetadata {
    String tableName;
    String columnName;
    String description;
    String isPrimaryKey;
}
