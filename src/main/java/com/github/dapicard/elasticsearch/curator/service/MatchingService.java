package com.github.dapicard.elasticsearch.curator.service;

import java.util.Date;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.github.dapicard.elasticsearch.curator.configuration.Configuration;
import com.github.dapicard.elasticsearch.curator.configuration.CuratorIndex;

public class MatchingService {
	private static final Logger LOGGER = LogManager.getLogger(MatchingService.class);

	private Configuration configuration;

	protected static enum Action {
		CLOSE, DELETE
	}

	public MatchingService(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Indicates if this index needs to be deleted
	 * 
	 * @param indexName
	 * @return true if the index needs to be deleted
	 */
	public boolean toDelete(String indexName) {
		return needAction(indexName, Action.DELETE);
	}

	/**
	 * Indicates if this index needs to be closed
	 * 
	 * @param indexName
	 * @return true if the index needs to be closed
	 */
	public boolean toClose(String indexName) {
		return needAction(indexName, Action.CLOSE);
	}

	protected boolean needAction(String indexName, Action action) {
		DateTime now = new DateTime();
		for (CuratorIndex curator : configuration.getCurator()) {
			Matcher indexMatcher = curator.getNamePattern().matcher(indexName);
			if (indexMatcher.matches()) {
				LOGGER.debug("[{}] matches index name                  : {}", curator.getName(), indexName);
				//Extract the date
				String dateString = indexMatcher.group(1);
				DateTime indexDate = null;
				try {
					indexDate = curator.getDatePattern().parseDateTime(dateString);
				} catch (IllegalArgumentException iae) {
					//Could not parse the date, so this is probably not the good curator
					LOGGER.debug("[{}] does not match date pattern         : {} - {}", curator.getName(), curator.getDatePattern().print(new Date().getTime()), dateString);
					continue;
				}
				if (indexDate.getSecondOfDay() == 0) {
					//means that we don't parse the time, so we go to the end of this day
					indexDate = indexDate.plus(24 * 60 * 60 * 1000 - 1);
				}
				LOGGER.debug("[{}] matches index name and date pattern : {}", curator.getName(), indexName);
				DateTime maxDate = indexDate;
				switch (action) {
				case CLOSE:
					maxDate = maxDate.plus(curator.getClosePeriod());
					break;
				case DELETE:
					maxDate = maxDate.plus(curator.getDeletePeriod());
					break;
				}
				LOGGER.debug("[{}]         | now                       : {}", curator.getName(), now);
				LOGGER.debug("[{}]         | extracted date            : {}", curator.getName(), indexDate);
				LOGGER.debug("[{}]         | extracted + delete period : {}", curator.getName(), maxDate);
				if (now.isAfter(maxDate)) {
					LOGGER.info("[{}] needs action                        : {} on {}", curator.getName(), action.name(), indexName);
					return true;
				} else {
					//Even if this rule matches and the period is not expired, we continue to test other curation because
					//it is possible for the user to configure multiple curations for one index pattern (not sure that is really useful)
					LOGGER.info("[{}] does not need action                : {} on {}", curator.getName(), action.name(), indexName);
				}
			}
		}
		return false;
	}
}