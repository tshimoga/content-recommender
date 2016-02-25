package com.recommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlAndIndex {

	Set<String> wikibooksURL = new HashSet<String>();
	Set<String> oracleURL = new HashSet<String>();

	public void startCrawling() throws IOException {
		String wikiurl = "https://en.wikibooks.org/wiki/Java_Programming";
		String oracleurl = "https://docs.oracle.com/javase/tutorial/java/TOC.html";
		Document doc = Jsoup.connect(wikiurl).get();
		Elements links = doc.getElementsByAttributeValueContaining("title",
				"Java Programming");
		for (Element link : links) {
			String l = link.attr("abs:href");
			wikibooksURL.add(l);
		}

		doc = Jsoup.connect(oracleurl).get();

		Elements li = doc.getElementsByAttributeValue("class", "tocli");
		for (Element l : li) {
			Elements href = l.getElementsByTag("a");
			for (Element e : href) {
				String a = e.attr("abs:href");
				if (!a.contains("question")) {
					oracleURL.add(a);
				}
			}
		}

		try {
			crawlWikibooksContent();
			crawlOracleContent();
		} catch (Exception e) {

		}
		IndexAndSearch.closeWriter();
	}

	public void crawlOracleContent() throws IOException {
		for (String url : oracleURL) {

			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
				Element head = doc.getElementById("PageTitle");
				StringBuilder stringBuilder = new StringBuilder();
				String heading = head.text();

				Element content = doc.getElementById("PageContent");

				for (Element tag : content.children()) {
					if (!tag.tagName().equals("h2")) {
						if (tag.text().compareTo("") == 0) {
							continue;
						}

						stringBuilder.append(tag.text());
						stringBuilder.append(System.lineSeparator());
					} else {
						IndexAndSearch.index(url, heading,
								stringBuilder.toString());

						stringBuilder = new StringBuilder();
						heading = tag.text();
					}
				}

				IndexAndSearch.index(url, heading, stringBuilder.toString());
			} catch (Exception e) {
				continue;
			}

		}

	}

	public void crawlWikibooksContent() throws IOException {

		for (String url : wikibooksURL) {
			if (url.contains("Print_version")) {
				continue;
			}
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (Exception e) {
				continue;
			}
			Element head = doc.getElementById("firstHeading");
			StringBuilder stringBuilder = new StringBuilder();
			String heading = head.text();

			Element content = doc.getElementById("mw-content-text");
			if (url.toLowerCase().contains("keywords")) {
				loadKeywords(content);
			}
			for (Element tag : content.children()) {
				if (!tag.tagName().equals("h2")) {
					if (tag.text().compareTo("") == 0
							|| tag.hasClass("wikitable")
							|| tag.hasClass("noprint")
							|| tag.hasClass("collapsible")) {
						continue;
					}

					stringBuilder.append(tag.text());
					stringBuilder.append(System.lineSeparator());
				} else {
					IndexAndSearch.index(url, heading.replace("[edit]", ""),
							stringBuilder.toString());

					stringBuilder = new StringBuilder();
					heading = tag.text();
				}
			}

			IndexAndSearch.index(url, heading.replace("[edit]", ""),
					stringBuilder.toString());

		}

	}

	private void loadKeywords(Element content) {
		Elements keywordTags = content.getElementsByAttributeValueContaining(
				"title", "Java Programming/Keywords/");
		for (Element tag : keywordTags) {
			String[] arr = tag.attr("title").split("/");
			Post.appendKeyword(arr[2]);
		}

	}

}
