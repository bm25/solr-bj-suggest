package com.gd.bjq.suggest.search.query;

import com.gd.bjq.suggest.index.constants.ScopeFieldTypes;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;

import java.util.List;

import static com.gd.bjq.suggest.index.constants.LuceneConstants.SCOPE_FLD_NAME;

public class HierarchicalPrefixParentMatchQueryBuilder {
    private List<String> parentFieldNames;
    private List<String> childFieldNames;

    public HierarchicalPrefixParentMatchQueryBuilder(List<String> parentFieldNames, List<String> childFieldNames) {
        this.parentFieldNames = parentFieldNames;
        this.childFieldNames = childFieldNames;
    }

    public Query build(List<String> tokenizedQuery){
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(buildSubQueryByParentFields(tokenizedQuery), BooleanClause.Occur.SHOULD);
        queryBuilder.add(buildToParentBJSubQueryByChildFields(tokenizedQuery), BooleanClause.Occur.SHOULD);
        return queryBuilder.build();

    }

    private Query buildSubQueryByParentFields(List<String> tokenizedQuery){
        int tokensCount = tokenizedQuery.size();

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        Term term;
        Query query;

        for (int i=0; i<tokensCount; i++) {
            String token = tokenizedQuery.get(i);

            for (String fieldName :parentFieldNames) {
                term = new Term(fieldName, token);
                query = i < tokensCount - 1 ? new TermQuery(term) : new PrefixQuery(term);
                queryBuilder.add(query, BooleanClause.Occur.SHOULD);
            }
        }

        return queryBuilder.build();
    }

    private Query buildToParentBJSubQueryByChildFields(List<String> tokenizedQuery){
        int tokensCount = tokenizedQuery.size();

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        Term term;
        Query query;

        for (int i=0; i<tokensCount; i++) {
            String token = tokenizedQuery.get(i);

            for (String fieldName :childFieldNames) {
                term = new Term(fieldName, token);
                Query childQuery = i < tokensCount - 1 ? new TermQuery(term): new PrefixQuery(term);
                Query parentFilter = new TermQuery(new Term(SCOPE_FLD_NAME, ScopeFieldTypes.PRODUCT.getType()));
                query = new ToParentBlockJoinQuery(childQuery, new QueryBitSetProducer(parentFilter) ,ScoreMode.None);
                queryBuilder.add(query, BooleanClause.Occur.SHOULD);
            }
        }

        return queryBuilder.build();
    }
}
