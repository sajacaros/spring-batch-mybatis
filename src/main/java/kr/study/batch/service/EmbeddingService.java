package kr.study.batch.service;

import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

public interface EmbeddingService {
    float[] embedding(String text);

    @Service
    class Default implements EmbeddingService {
        @Autowired
        private OpenAiEmbeddingModel embeddingModel;

        @Override
        public float[] embedding(String text) {
            if(Objects.isNull(text) || text.isEmpty()) {
                return null;
            }
            return embeddingModel.embed(text);
        }
    }
}
