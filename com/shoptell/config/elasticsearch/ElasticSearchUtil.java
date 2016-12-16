/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.config.elasticsearch;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.db.messagelog.MessageEnum.ERROR;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.fuzzyQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.ColorMapDTO;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.db.messagelog.MessageLogUtil;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "ElasticSearchUtil")
public class ElasticSearchUtil {

	private static final Logger log = LoggerFactory.getLogger(ElasticSearchUtil.class);

	@Inject
	private ElasticSearchClient es;

	@Inject
	private Environment env;

	@Inject
	private BatchRepository repository;

	@Inject
	private MessageLogUtil msgLog;

	private TransportClient client;

	private BulkRequestBuilder bulkRequest;

	private BulkResponse bulkResponse;

	private int bulkCounter = 0;

	private long lastIndexed = 0;

	private long lastUpdated = 0;

	private String indexName;

	private String indexType;

	public void onLoad() {
		indexName = env.getProperty("es.index.name", "afferve");
		indexType = env.getProperty("es.index.type", "afferve");
	}

	@PostConstruct
	public void start() {
		onLoad();
		try {
			this.client = es.getObject();
			IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
			if (!res.isExists()) {
				createIndex();
			}
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("ES START ERROR", e);
		}
		bulkRequest = client.prepareBulk();
		seedData();
	}

	public void init() {
		try {
			deleteIndex();
			// reNewIndexes();
		} catch (ElasticsearchException e) {
			log.error("ES INIT ERROR", e);
		} catch (Exception e) {
			log.error("ES INIT ERROR", e);
		}
	}

	public void indexExists() throws IOException, InterruptedException {
		// Remove index
		try {
			client.admin().indices().prepareDelete(indexName).get();
		} catch (IndexMissingException e) {
			log.error("ES ERROR (CAN BE IGNORED)", e);
		}

		// Create the index
		client.admin().indices().prepareCreate(indexName).get();

		IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).get();
		if (!res.isExists()) {
			log.info("Index does not exist. Creating...");
			client.admin().indices().prepareCreate(indexName).get();
		}
		else {
			log.info("Index already exists.");
		}
	}

	public void reNewIndexes() throws Exception {
		deleteIndex();
		createIndex();
		IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).get();
		if (!res.isExists()) {
			log.info("Index does not exist. Creating...");
			createIndex();
		}
		else {
			log.info("Index already exists.");
		}
		reIndexAllData();
	}
	
	public void reIndexAllData(){
		Iterator<MergedProductInfoDTO> products = repository.getMergedProductData();
		while (products.hasNext()) {
			MergedProductInfoDTO data = products.next();
			saveRequest(data);
		}
		save();
	}
	
	public void execute() {

	}

	@SuppressWarnings("unused")
	private void changeIndex() throws ElasticsearchException, Exception {
		client.admin().indices().prepareClose(indexName).execute().actionGet();
		client.admin().indices().prepareUpdateSettings(indexName).setSettings(getSettings()).execute().actionGet();
		client.admin().indices().prepareOpen(indexName).execute().actionGet();
		client.admin().indices().prepareDeleteMapping(indexName).setType(indexType).execute().actionGet();
		client.admin().indices().preparePutMapping(indexName).setType(indexType).setSource(getMappings()).execute().actionGet();
	}

	private XContentBuilder getMappings() throws Exception {
		XContentBuilder object = jsonBuilder().startObject().startObject(indexType).startObject("_all").field("enabled", true)
				.field("index_analyzer", "edge_nGram_analyzer").field("search_analyzer", "whitespace_analyzer").endObject().startObject("properties")
				.startObject("name").field("type", "string").field("index_analyzer", "edge_nGram_analyzer").field("search_analyzer", "whitespace_analyzer")
				.endObject().endObject().endObject().endObject();
		return object;
	}

	private String getSettings() throws Exception {
		String object = jsonBuilder().startObject().startObject("analysis").startObject("filter").startObject("edgeNGram_filter").field("type", "edgeNGram")
				.field("min_gram", 2).field("max_gram", 30).endObject().endObject().startObject("analyzer").startObject("edge_nGram_analyzer")
				.field("type", "custom").field("tokenizer", "edge_ngram_tokenizer").field("filter", "lowercase", "asciifolding", "edgeNGram_filter")
				.endObject().startObject("whitespace_analyzer").field("type", "custom").field("tokenizer", "whitespace")
				.field("filter", "lowercase", "asciifolding").endObject().endObject().startObject("tokenizer").startObject("edge_ngram_tokenizer")
				.field("type", "edgeNGram").field("min_gram", 2).field("max_gram", 30).field("token_chars", "letter", "digit").endObject().endObject()
				.endObject().endObject().string();
		return object;
	}

	private void createIndex() throws Exception {
		try {
			client.admin()
					.indices()
					.create(new CreateIndexRequest(indexName).settings(ImmutableSettings.settingsBuilder().loadFromSource(getSettings())).mapping(indexType,
							getMappings())).actionGet();
		} catch (IndexAlreadyExistsException e) {
			log.error("ES ERROR (CAN BE IGNORED)", e);
		}
	}

	public void deleteIndex() {
		try {
			DeleteIndexResponse delete = client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
			if (!delete.isAcknowledged()) {
				log.info("Indexes not cleared");
			}
			// client.admin().indices().prepareDelete(indexName).get();
		} catch (IndexMissingException e) {
			log.error("ES ERROR (CAN BE IGNORED)", e);
		}
	}

	public List<ElasticSearchResponse> Search(String text, CategoryEnum category) {
		try {
			String subCategory = null;
			text = text.replaceAll("\\s+", " ").toLowerCase();
			List<ElasticSearchResponse> list = new LinkedList<ElasticSearchResponse>();
			Set<String> set = new HashSet<String>();// to have unique Keys
			String tokens[] = text.split(" ");
			int len = tokens.length;
			BoolQueryBuilder query = boolQuery().must(matchQuery("name", text).operator(Operator.AND).boost(2));

//			for (int i = 0; i < len; i++) {
//				/*query =*/ query.should(matchQuery("name", tokens[i++]));
//			}

			query = query.should(fuzzyQuery("name", text).fuzziness(Fuzziness.AUTO).maxExpansions(2).boost(0.5f));
			
			if (category != null && !StringUtils.equalsIgnoreCase(CategoryEnum.ALL.name(), category.name())){
				//query = query.must(matchQuery("subCategory", category));
				query = query.must(boolQuery().should(matchQuery("subCategory", category)).should(matchQuery("subCategory", "COUPONS")).should(matchQuery("subCategory", "CATEGORY")).minimumNumberShouldMatch(1));
			}

			SearchResponse response = client.prepareSearch(indexName).setTypes(indexType).setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(query)
					.setFrom(0).setSize(20).setExplain(true).addSort(getSort("rank", SortOrder.ASC)).addSort(getSort("views", SortOrder.DESC)).addSort("_score", SortOrder.DESC).execute()
					.actionGet();
			for (SearchHit hit : response.getHits()) {
				Map<String, Object> map = hit.getSource();
				String id = hit.getId();
				String name = (String) map.get("name");
				String rnk = String.valueOf(map.get("rank"));
				double rank = Double.MAX_VALUE;
				if (NumberUtils.isNumber(rnk)){
					rank = Double.parseDouble(rnk);
				}
				int views =  Integer.parseInt(String.valueOf(map.get("views")).split("\\.")[0]);
				subCategory = (String) map.get("subCategory");
				String metaCategory = (String) map.get("metaCategory");
				String productBrand = (String) map.get("productBrand");
				
				try {
					ElasticSearchResponse resp = new ElasticSearchResponse(id, name, rank, views, subCategory, metaCategory, productBrand);
					if (!set.contains(name)) {
						set.add(name);
						list.add(resp);
						if (list.size() == 6) {
							break;
						}
					}
				} catch (Exception e) {
					log.error("HIGH PIORITY EXCEPTION AT ELASTIC SEARCH", e);
					msgLog.addError(e);
				}
			}
			return list;
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("ES SEARCH ERROR", e);
		}
		return null;
	}

	private SortBuilder getSort(String param, SortOrder order) {
		@SuppressWarnings("deprecation")
		SortBuilder sb = SortBuilders.fieldSort(param).order(order).ignoreUnmapped(true);
		return sb;
	}

	public void deleteIndexById(String id) {
		try {
			client.prepareDelete(indexName, indexType, id).execute().actionGet();
		} catch (Exception e) {
			log.error("ES INDEX DELETE ERROR", e);
		}
	}
	
	public void seedData(){
		try {
			double rank = 1;
			for (CategoryEnum category : CategoryEnum.values()){
				if (CategoryEnum.ALL.equals(category)){
					continue;
				}
				String tmp = category.name();
				bulkRequest.add(client.prepareIndex(indexName, indexType, tmp).setSource(
					jsonBuilder().startObject().field("name", tmp).field("subCategory","CATEGORY").field("rank", rank).field("views", 0).endObject()));
			}
			
			for (HomeEnum home : HomeEnum.values()){
				String tmp = home.name();
				bulkRequest.add(client.prepareIndex(indexName, indexType, tmp).setSource(
					jsonBuilder().startObject().field("name", tmp).field("subCategory","COUPONS").field("rank", rank).field("views", 0).endObject()));
			}
			save();
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("ES SAVE ERROR", e);
		}
		finally {
			try {
				save();
			} catch (Exception ex) {}
		}
	}

	public void saveRequest(MergedProductInfoDTO dto) {
		try {
			double rank = getRank(dto);
			bulkRequest.add(client.prepareIndex(indexName, indexType, dto.getId().toString()).setSource(
					jsonBuilder().startObject().field("name", dto.getName()).field("subCategory",dto.getSubCategoryName()).field("rank", rank).field("views", 0)
					.field("metaCategory", dto.getMetaCategory()).field("productBrand", dto.getProductBrand()).endObject()));
			++bulkCounter;
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("ES SAVE ERROR", e);
		}
		if (bulkCounter > BATCHSIZE || isOverAnHour()) {
			save();
			bulkCounter = 0;
		}
	}

	public void updateName(String id, String name) {
		try {
			client.prepareUpdate(indexName, indexType, id).setDoc("name", name).get();
		} catch (Exception e) {
			log.error("ES UPDATE NAME ERROR", e);
			msgLog.addError(e);
		}
	}

	public void updateRank(String id, double rank) {
		try {
			client.prepareUpdate(indexName, indexType, id).setDoc("rank", rank).get();
		} catch (Exception e) {
			log.error("ES UPDATE RANK ERROR", e);
			msgLog.addError(e);
		}
	}
	
	public void updateViews(String id, int view) {
		try {
			client.prepareUpdate(indexName, indexType, id).setDoc("views", view).get();
		} catch (Exception e) {
			log.error("ES UPDATE VIEW ERROR", e);
			msgLog.addError(e);
		}
	}

	public void updateViews(String id) {
		try {
			bulkRequest.add(client.prepareUpdate(indexName, indexType, id).setScript("ctx._source.views+=1", ScriptType.INLINE));
			++bulkCounter;
		} catch (Exception e) {
			log.error("ES UPDATE VIEW ERROR", e);
			msgLog.addError(e);
		}
		if (bulkCounter > 1 || isOverTenMinutes()) {
			save();
			bulkCounter = 0;
			lastUpdated = System.currentTimeMillis();
		}
	}

	private boolean isOverTenMinutes() {
		long time = System.currentTimeMillis() - lastUpdated;
		if (time > 1000 * 10 * 60 * 60) {
			return true;
		}
		return false;
	}

	private boolean isOverAnHour() {
		long time = System.currentTimeMillis() - lastIndexed;
		if (time > 1000 * 60 * 60 * 60) {
			return true;
		}
		return false;
	}

	public void save() {
		bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			StringBuilder sb = new StringBuilder();
			for (BulkItemResponse item : bulkResponse.getItems()){
				if (item.isFailed()){
					sb.append(item.getId()).append(" : ").append(item.getFailureMessage()).append("\n");
				}
			}
			if (StringUtils.isNotBlank(sb.toString())){
				msgLog.add(ERROR, "ElasticSearchUtil", sb.toString());
			}
			log.error("Error while bulk indexing");
		}
		lastIndexed = System.currentTimeMillis();
		client.admin().indices().flush(new FlushRequest(indexName)).actionGet();
	}

	@Async
	public void updateRank(List<MergedProductInfoDTO> outcome) {
		try {
			for (MergedProductInfoDTO info : outcome) {
				double rank = getRank(info);
				if (rank > 0 && rank < Double.POSITIVE_INFINITY){
					updateRank(info.getId().toString(), rank);
				}
			}
			//client.admin().indices().flush(new FlushRequest(indexName)).actionGet();
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("ES UPDATE RANK", e);
		}
	}

	private double getRank(MergedProductInfoDTO info) {
		double rank = 0;
		int count = 0;
		info.generateRank();

		if (info.getData() != null && info.getData().size() > 0) {
			for (ColorMapDTO data : info.getData().values()) {
				rank += data.getAvgSalesRank();
				++count;
			}
		}
		if (count == 0 || rank == 0) {
			rank = Double.POSITIVE_INFINITY;
		}
		else {
			rank /= count;
		}

		return rank;
	}

	public void update(ElasticSearchResponse request) {
		String id = request.getId();
		String name = request.getName();
		double rank = request.getRank();
		int view = request.getViews();
		
		String category = request.getSubCategory();
		CategoryEnum cat = CategoryEnum.getCategory(category);
		if (cat != null){
			updateSubCategory(id,cat.name());
		}
		
		updateRank(id, rank);
		updateViews(id, view);
		updateName(id, name);
		try {
			client.admin().indices().flush(new FlushRequest(indexName)).actionGet();
		} catch (Exception e) {
			log.error("ES UPDATE VIEW ERROR", e);
			msgLog.addError(e);
		}
	}

	private void updateSubCategory(String id, String category) {
		try {
			client.prepareUpdate(indexName, indexType, id).setDoc("subCategory", category).get();
		} catch (Exception e) {
			log.error("ES UPDATE VIEW ERROR", e);
			msgLog.addError(e);
		}
	}

	public void deleteIndexByCategory(CategoryEnum tmp) {
		log.info("deleteIndexByCategory() Enter");
		final Set<String> lostItems = new TreeSet<>();
		final TimeValue time = TimeValue.timeValueMinutes(1);
		long count = 0;
		SearchResponse response = client.prepareSearch(indexName).setTypes(indexType).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)/*.setNoFields()*/.setFetchSource(false)
				.setQuery(boolQuery().should(matchAllQuery()).should(termQuery(indexType+".subCategory", tmp.name()))).setScroll(time).execute().actionGet();
		do {
			final BulkRequestBuilder bulk = client.prepareBulk();
			for (final SearchHit hit : response.getHits()) {
				bulk.add(client.prepareDelete(indexName, indexType, hit.getId()));
			}
			if (bulk.numberOfActions() > 0) {
				lostItems.clear();
				final BulkResponse bulkResponse = bulk.get();
				if (bulkResponse.hasFailures()) {
					log.error("Error while executing bulk request: ", bulkResponse.buildFailureMessage());
				}
				// count items for safety:
				int deletedItems = 0;
				for (final BulkItemResponse item : bulkResponse.getItems()) {
					final DeleteResponse delResp = item.getResponse();
					if (delResp.isFound()) {
						deletedItems++;
					}
					else {
						lostItems.add(delResp.getId());
					}
				}
				count += deletedItems;
				if (!lostItems.isEmpty()) {
					log.warn("Some metadata items could not be deleted because they disappeared in the meantime: ", lostItems);
				}
				log.info("Deleted " + count + " metadata items until now, working...");
			}
			if (response.getScrollId() == null)
				break;
			response = client.prepareSearchScroll(response.getScrollId()).setScroll(time).get();
		} while (response.getHits().getHits().length > 0);
		log.info("deleteIndexByCategory() Exit");
	}
}