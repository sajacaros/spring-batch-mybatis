package kr.study.batch.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class TableMetadata {
    String tableName;
    String description;
    float[] embedding;

    @Override
    public String toString() {
        return "TableMetadata{" +
                "tableName='" + tableName + '\'' +
                ", description='" + description + '\'' +
                ", embedding=" + (embedding != null ? Arrays.toString(Arrays.copyOf(embedding, Math.min(embedding.length, 5))) : "null") +
                '}';
    }
}
