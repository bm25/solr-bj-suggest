# solr-bj-suggest
Create custom query parser for hierarchical prefix search based on Solr(Lucene). 

==  Описание на русском  ==
Допустим, у нас есть каталог документов с отношением parent-child (продукты и юписи).
У продуктов есть , бизнес поля id, BRAND и PRODUCT_TYPE, у юписей - id, COLOR и SIZE. Можно добавить техническое поле scope, чтобы разделять типы документов.
Мы хотим сделать префиксный поиск, то есть, найти документы по заданной фразе, при этом последнее слово фразы может быть префиксом, а не полноценным словом.

Требования:
1. Написать свой квери парсер, который по заданной фразе строит кверю, которая пытается сматчить куски фразы в поля продуктов и юписей, при этом возвращать мы хотим только продукты. Так же мы хотим, чтобы в полученной квере последнее слово фразы находилось как префикс.
1.1. Мы хотим ценить документы, которые содержат более длинные куски фразы в своих значениях, выше чем документы, содержащие короткие куски фразы. Например, матч куска фразы “calvin klein jeans” как BRAND:“calvin klein jeans” должен цениться выше чем матч этой фразы в разбиении BRAND:“calvin klein” + PRODUCT_TYPE:“jeans”.
2. Реализовать свой реквест хэндлер (замапить на “bjqSuggest”), который будет использовать  написанный нами квери парсер.
2.1. Реквест хэндлер должен поддерживать стандартные поисковые параметры солорических запросов (raws, start, q и тд)
2.2 Реквест хэндлер должен отдавать в ответе список бизнес id найденных продуктов и qTime в виде JSON
3. Создать тестовый набор данных (около 10 иерархических документов), позволяющий продемонстрировать работу хэндлера и парсера.

== Task description in english ==
Let's assume there is a catalog of documents with parent-child relations: product and UPC.
Each product has fields: ID, BRAND, PRODUCT_TYPE
Each UPC has: ID, COLOR, SIZE
SCOPE - is used to distinguish between product and UPC



== Settings ==
solr.​install.​dir = /Users/amoiseenko/soft/solr/solr-8.1.0
solr.​solr.​home   = /Users/amoiseenko/soft/solr/solr-8.1.0/server/solr

== Script ==
1) Download the latest SOLR release https://lucene.apache.org/solr/mirrors-solr-latest-redir.html
Now it is 8.1

2) Package installation
cd ~/
tar zxf solr-7.1.0.tgz

3) Start solr
Standalone server example (start Solr running in the background on port 8984):
> ./solr start -p 8984

4) Check solr status 
> ./solr status

5) Set the path to your plugin in solrconfig.xml in lib tag and put your jar into respective dir
Or you can put your jar just into <solr.​install.​dir>/server/solr-webapp/webapp/WEB-INF/lib

Solrconfig.xml

<lib dir="${solr.install.dir:../../../..}/dist/" regex="solr-bjq-suggest-1.0-SNAPSHOT.jar" />

6) Find your solr.install.dir on http://localhost:8984/solr/#/~java-properties after starting solr server


7 ) Goto <solr.​solr.​home>
> mkdir test

8) Copy 3 files from the project parent folder into <solr.​solr.​home>
schema.xml
solrconfig.xml
stopwords.txt

> copy 

9) Add core
name = test
instanceDir = test
> bin/solr create -c test
or just goto http://localhost:8984/solr/#/~cores

10) Import documents into index from csv-file using SimplePostTool
> bin/post -c test /Users/amoiseenko/workspace/github/solr-bj-suggest/test-data.csv -p 8984

11)Execute search-request
11.1) Search:"Calvin"

GET: http://localhost:8984/solr/test/select?q=calvin

{
  "responseHeader":{
    "status":400,
    "QTime":0,
    "params":{
      "q":"calvin"}},
  "error":{
    "metadata":[
      "error-class","org.apache.solr.common.SolrException",
      "root-error-class","org.apache.solr.common.SolrException"],
    "msg":"undefined field edismax",
    "code":400}}
    
    
Error:   
edismax - ???


