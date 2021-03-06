/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.ml.inference.nlp;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.xpack.ml.inference.nlp.tokenizers.BertTokenizer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class NlpTaskConfig implements ToXContentObject {

    public static final ParseField VOCAB = new ParseField("vocab");
    public static final ParseField TASK_TYPE = new ParseField("task_type");
    public static final ParseField LOWER_CASE = new ParseField("do_lower_case");

    private static final ObjectParser<NlpTaskConfig.Builder, Void> STRICT_PARSER = createParser(false);
    private static final ObjectParser<NlpTaskConfig.Builder, Void> LENIENT_PARSER = createParser(true);

    private static ObjectParser<NlpTaskConfig.Builder, Void> createParser(boolean ignoreUnknownFields) {
        ObjectParser<NlpTaskConfig.Builder, Void> parser = new ObjectParser<>("task_config",
            ignoreUnknownFields,
            Builder::new);

        parser.declareStringArray(Builder::setVocabulary, VOCAB);
        parser.declareString(Builder::setTaskType, TASK_TYPE);
        parser.declareBoolean(Builder::setDoLowerCase, LOWER_CASE);
        return parser;
    }

    public static NlpTaskConfig fromXContent(XContentParser parser, boolean lenient) {
        return lenient ? LENIENT_PARSER.apply(parser, null).build() : STRICT_PARSER.apply(parser, null).build();
    }

    public static String documentId(String model) {
        return model + "_task_config";
    }

    private final TaskType taskType;
    private final List<String> vocabulary;
    private final boolean doLowerCase;

    NlpTaskConfig(TaskType taskType, List<String> vocabulary, boolean doLowerCase) {
        this.taskType = taskType;
        this.vocabulary = vocabulary;
        this.doLowerCase = doLowerCase;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public BertTokenizer buildTokenizer() {
        return BertTokenizer.builder(vocabulary).setDoLowerCase(doLowerCase).build();
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(TASK_TYPE.getPreferredName(), taskType.toString());
        builder.field(VOCAB.getPreferredName(), vocabulary);
        builder.endObject();
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NlpTaskConfig that = (NlpTaskConfig) o;
        return taskType == that.taskType &&
            doLowerCase == that.doLowerCase &&
            Objects.equals(vocabulary, that.vocabulary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskType, vocabulary, doLowerCase);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private TaskType taskType;
        private List<String> vocabulary;
        private boolean doLowerCase = false;

        public Builder setTaskType(TaskType taskType) {
            this.taskType = taskType;
            return this;
        }

        public Builder setTaskType(String taskType) {
            this.taskType = TaskType.fromString(taskType);
            return this;
        }

        public Builder setVocabulary(List<String> vocab) {
            this.vocabulary = vocab;
            return this;
        }

        public Builder setDoLowerCase(boolean doLowerCase) {
            this.doLowerCase = doLowerCase;
            return this;
        }

        public NlpTaskConfig build() {
            return new NlpTaskConfig(taskType, vocabulary, doLowerCase);
        }
    }
}
