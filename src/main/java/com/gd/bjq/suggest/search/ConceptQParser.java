package com.gd.bjq.suggest.search;

import com.gd.bjq.suggest.search.query.MaxSubConceptMatchQueryBuilder;
import com.gd.bjq.suggest.search.query.util.SequentialWordCombinationsGenerator;
import com.google.common.collect.Lists;
import org.apache.lucene.search.*;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

import java.util.List;
import java.util.stream.Collectors;

public class ConceptQParser extends QParser {
    protected List<String> tokenizedQuery;

    public ConceptQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        super(qstr, localParams, params, req);
    }

    @Override
    public Query parse() throws SyntaxError {
        tokenizedQuery = tokenizeSearchQuery(qstr);
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

    public static List<String> tokenizeSearchQuery(final String qString) {
        return Lists.newArrayList(TokenStreams.tokenizeWhitespace(qString));
    }
}
