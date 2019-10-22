package com.gd.bjq.suggest.index.constants;

import java.util.Arrays;
import java.util.List;

/*
id,_version_,BRAND,_root_,SCOPE,PRODUCT_TYPE,COLOR,SIZE
 */
public interface LuceneConstants {
    String BRAND_FLD_NAME = "BRAND";
    String SCOPE_FLD_NAME = "SCOPE";
    String PRODUCT_TYPE_FLD_NAME = "PRODUCT_TYPE";
    String COLOR_FLD_NAME = "COLOR";
    String SIZE_FLD_NAME = "SIZE";

    List<String> PARENT_DOC_FIELD_NAMES = Arrays.asList(BRAND_FLD_NAME, PRODUCT_TYPE_FLD_NAME);
    List<String> CHILD_DOC_FIELD_NAMES = Arrays.asList(COLOR_FLD_NAME, SIZE_FLD_NAME);

    int MAX_SEARCH = 30;
}
