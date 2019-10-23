package com.gd.bjq.suggest.search;

import com.gd.bjq.suggest.search.query.MaxSubConceptMatchQueryBuilder;
import com.gd.bjq.suggest.search.query.util.SequentialWordCombinationsGenerator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.*;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptQParser extends QParser {
    private static final String TOKEN_STREAM_DEFAULT_FIELD_NAME = "BRAND";

    public ConceptQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        super(qstr, localParams, params, req);
    }

    @Override
    public Query parse() throws SyntaxError {
        List<String> tokenizedQuery = new ArrayList<>();
        Analyzer analyzer = req.getSchema().getQueryAnalyzer();
        try (TokenStream stream = analyzer.tokenStream(TOKEN_STREAM_DEFAULT_FIELD_NAME,new StringReader(qstr))) {
            stream.reset();
            while (stream.incrementToken()) {
                tokenizedQuery.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
        } catch (IOException e) {
            throw new SyntaxError(e);
        }

        return buildQuery(tokenizedQuery);
    }

    private Query buildQuery(List<String> tokenizedQuery){
        List<List<String>> subPhrases = new SequentialWordCombinationsGenerator(tokenizedQuery).generate();
        return new MaxSubConceptMatchQueryBuilder(subPhrases).build();
    }

    private List<String> getAllNotServiceFields(SolrCore core){
        return core.getLatestSchema().getFields().values().stream()
                .map(field -> field.getName())
                .filter(fieldName -> !isServiceField(fieldName))
                .collect(Collectors.toList());
    }

    private boolean isServiceField(String fieldName){
        return "id".equals(fieldName) || fieldName.charAt(0) == '_';
    }
}
