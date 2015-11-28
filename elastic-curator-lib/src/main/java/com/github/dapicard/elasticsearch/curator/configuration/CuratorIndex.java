package com.github.dapicard.elasticsearch.curator.configuration;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	public Pattern getNamePattern() {
		return namePattern;
	}

	public void setNamePattern(Pattern namePattern) {
		this.namePattern = namePattern;
	}

	public DateTimeFormatter getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(DateTimeFormatter datePattern) {
		this.datePattern = datePattern;
	}

	public Period getClosePeriod() {
		return closePeriod;
	}

	public void setClosePeriod(Period closePeriod) {
		this.closePeriod = closePeriod;
	}

	public Period getDeletePeriod() {
		return deletePeriod;
	}

	public void setDeletePeriod(Period deletePeriod) {
		this.deletePeriod = deletePeriod;
	}

}
