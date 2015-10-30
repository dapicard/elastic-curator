package com.github.dapicard.elasticsearch.curator.configuration;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;

/**
 * Cleanup configuration for an indice pattern
 * @author Damien Picard
 *
 */
public class CuratorIndex implements Serializable {
	private static final long serialVersionUID = 3022002331851445269L;

	//Configured field
	private String name;
	private String pattern;
	private String close;
	private String delete;

	//Computed field
	private Period closePeriod;
	private Period deletePeriod;
	private Pattern namePattern;
	private DateTimeFormatter datePattern;

	public CuratorIndex() {
	}

	public CuratorIndex(String name, String pattern, String close, String delete) {
		super();
		this.name = name;
		this.pattern = pattern;
		this.close = close;
		this.delete = delete;
	}

	/**
	 * @return The configuration name, used for logging purpose
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @see CuratorIndex#getName()
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The indices name pattern to match
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern
	 * @see CuratorIndex#getPattern()
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return The period after which an index has to be closed
	 */
	public String getClose() {
		return close;
	}

	/**
	 * @param close
	 * @see CuratorIndex#getClose()
	 */
	public void setClose(String close) {
		this.close = close;
	}

	/**
	 * @return The period after which an index has to be deleted
	 */
	public String getDelete() {
		return delete;
	}

	/**
	 * @param delete
	 * @see CuratorIndex#getDelete()
	 */
	public void setDelete(String delete) {
		this.delete = delete;
	}

	/**
	 * @return The indices name pattern to match
	 */
	public Pattern getNamePattern() {
		return namePattern;
	}

	/**
	 * @param namePattern
	 * @see CuratorIndex#getNamePattern()
	 */
	public void setNamePattern(Pattern namePattern) {
		this.namePattern = namePattern;
	}

	/**
	 * @return The date pattern to read the date from the index name
	 */
	public DateTimeFormatter getDatePattern() {
		return datePattern;
	}

	/**
	 * @param datePattern
	 * @see CuratorIndex#getDatePattern()
	 */
	public void setDatePattern(DateTimeFormatter datePattern) {
		this.datePattern = datePattern;
	}

	/**
	 * @see CuratorIndex#getClose()
	 * @return
	 */
	public Period getClosePeriod() {
		return closePeriod;
	}

	/**
	 * @see CuratorIndex#getClose()
	 * @param closePeriod
	 */
	public void setClosePeriod(Period closePeriod) {
		this.closePeriod = closePeriod;
	}

	/**
	 * @see CuratorIndex#getDelete()
	 * @return
	 */
	public Period getDeletePeriod() {
		return deletePeriod;
	}

	/**
	 * @see CuratorIndex#getDelete()
	 * @param deletePeriod
	 */
	public void setDeletePeriod(Period deletePeriod) {
		this.deletePeriod = deletePeriod;
	}

}
