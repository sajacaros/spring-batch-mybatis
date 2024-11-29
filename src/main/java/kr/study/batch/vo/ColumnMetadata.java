package kr.study.batch.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ColumnMetadata {
    String tableName;
    String columnName;
    String description;
    String dataTypeCode;
    float[] embedding;

    @Override
    public String toString() {
        return "ColumnMetadata{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", description='" + description + '\'' +
                ", dataTypeCode='" + dataTypeCode + '\'' +
                ", embedding=" + (embedding != null ? Arrays.toString(Arrays.copyOf(embedding, Math.min(embedding.length, 1))) : "null") +
                '}';
    }
}
