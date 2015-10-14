package com.github.dapicard.elasticsearch.curator.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.dapicard.elasticsearch.curator.TestUtil;
import com.github.dapicard.elasticsearch.curator.configuration.Configuration;
import com.github.dapicard.elasticsearch.curator.service.MatchingService;

public class MatchingServiceTest {
	private static final Logger LOGGER = LogManager.getLogger(MatchingServiceTest.class);
	
	static DateTimeFormatter d = DateTimeFormat.forPattern("YYYY.MM.dd");
	static DateTimeFormatter h = DateTimeFormat.forPattern("YYYY.MM.dd-HH");
	
	@Test
	public void testDayMatching() throws JsonParseException, JsonMappingException, IOException {
		MatchingService service = new MatchingService(Configuration.getConfiguration());

		String indexName = "logstash-" + d.print(TestUtil.nowMinusPeriod("1 day"));
		LOGGER.info("Test with index name : " + indexName);
		Assert.assertFalse(service.toDelete(indexName));
		indexName = "logstash-" + d.print(TestUtil.nowMinusPeriod("2 day"));
		LOGGER.info("Test with index name : " + indexName);
		Assert.assertFalse(service.toDelete(indexName));
		Assert.assertFalse(service.toDelete(indexName));
		indexName = "logstash-" + d.print(TestUtil.nowMinusPeriod("3 day"));
		Assert.assertTrue(service.toDelete(indexName));
	}
	
	@Test
	public void testHourMatching() throws JsonParseException, JsonMappingException, IOException {
		MatchingService service = new MatchingService(Configuration.getConfiguration());

		String indexName = "logstash2-" + h.print(TestUtil.nowMinusPeriod("2 hour"));
		LOGGER.info("Test with index name : " + indexName);
		Assert.assertFalse(service.toDelete(indexName));
		
		indexName = "logstash2-" + h.print(TestUtil.nowMinusPeriod("10 hour"));
		LOGGER.info("Test with index name : " + indexName);
		Assert.assertTrue(service.toDelete(indexName));
		
	}
	
}
