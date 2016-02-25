package com.recommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Post {

	private String type;
	private String text;
	private String code = "";
	private String keywords;
	static private Set<String> wordList = new HashSet<String>();

	public Post() {
	};

	public Post(String type, String text, String code) throws IOException {
		super();
		this.type = type;
		this.text = text;
		this.code = code;
		this.keywords = generateKeywords();
	}

	public String getType() {
		return type;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public static void appendKeyword(String word) {
		wordList.add("constructor");
		wordList.add("classes");
		wordList.add("objects");
		wordList.add("implement");
		wordList.add("instance");
		wordList.add("session");
		wordList.add("main");
		wordList.add("inheritence");
		wordList.add("derives");
		wordList.add("serialize");
		wordList.add("deserialize");
		wordList.add("reference");
		wordList.add("interface");
		wordList.add("object");
		wordList.add(word);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String generateKeywords() throws IOException {
		StringBuilder keywords = new StringBuilder();
		Set<String> set = new HashSet<String>();
		for (String s : text.split(" ")) {
			String str = s.toLowerCase();
			if (wordList.contains(str) && s.length() != 1) {
				set.add(str);
			}
		}

		for (String s : set) {
			keywords.append(s + " ");
		}
		return keywords.toString();

	}

	public String hyperLinkKeywords() {

		StringBuilder builder = new StringBuilder();
		String[] arr = text.split(" ");
		for (String s : arr) {
			if (wordList.contains(s.toLowerCase()) && s.length() != 1) {
				builder.append("<a href='javascript:void(0);' onClick=getRecommendations('"
						+ s.toLowerCase() + "',0)>" + s + "</a>" + " ");
			} else {
				builder.append(s + " ");
			}
		}
		return builder.toString();
	}
}
