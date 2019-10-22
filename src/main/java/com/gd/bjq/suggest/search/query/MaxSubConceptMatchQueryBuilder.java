package com.gd.bjq.suggest.search.query;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.util.List;

import static com.gd.bjq.suggest.index.constants.LuceneConstants.CHILD_DOC_FIELD_NAMES;
import static com.gd.bjq.suggest.index.constants.LuceneConstants.PARENT_DOC_FIELD_NAMES;

public class MaxSubConceptMatchQueryBuilder {
    private List<List<String>> phraseSubCombinations;

    public MaxSubConceptMatchQueryBuilder(List<List<String>> phraseSubCombinations) {
        this.phraseSubCombinations = phraseSubCombinations;
    }

    public Query build(){
        HierarchicalPrefixParentMatchQueryBuilder hierarchicalQueryBuilder = new HierarchicalPrefixParentMatchQueryBuilder(PARENT_DOC_FIELD_NAMES, CHILD_DOC_FIELD_NAMES);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

        for (List<String> combination: phraseSubCombinations) {
            queryBuilder.add(hierarchicalQueryBuilder.build(combination), BooleanClause.Occur.SHOULD);
        }

        return queryBuilder.build();
    }
}
