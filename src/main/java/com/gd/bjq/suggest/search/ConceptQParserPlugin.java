package com.gd.bjq.suggest.search;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class ConceptQParserPlugin extends QParserPlugin {
    @Override
    public QParser createParser(String qstr, SolrParams localParams,
                                SolrParams params, SolrQueryRequest req) {
            return new ConceptQParser(qstr, localParams, params, req);
    }
}
