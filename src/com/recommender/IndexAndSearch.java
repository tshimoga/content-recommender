package com.recommender;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Attribute;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class IndexAndSearch {

	static Directory indexDir = new RAMDirectory();
	static Analyzer analyzer = new StandardAnalyzer();
	static IndexWriterConfig config = new IndexWriterConfig(analyzer);
	static IndexWriter writer;

	public static void index(String url, String title, String content)
			throws IOException {
		if (writer == null) {
			writer = new IndexWriter(indexDir, config);
		}
		Document doc = new Document();
		doc.add(new TextField("url", url, TextField.Store.YES));
		TextField header = new TextField("header", title, TextField.Store.YES);
		//getKeywords(title);
		header.setBoost(2.0f);
		doc.add(header);
		TextField stemmedHeader = new TextField("stemmedHeader", stem(title), TextField.Store.YES);
		stemmedHeader.setBoost(2.0f);
		doc.add(stemmedHeader);
		doc.add(new TextField("contents", content, TextField.Store.YES));
		doc.add(new TextField("stemmedContent", stem(content),
				TextField.Store.YES));
		writer.addDocument(doc);

	}

	public static void closeWriter() throws IOException {
		writer.flush();
		writer.close();
	}

	public static String stem(String text) throws IOException {
		StringBuilder keywords = new StringBuilder();

		TokenStream tokenStream = analyzer.tokenStream("contents",
				new StringReader(text));
		Attribute term = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		PorterStemmer stemmer = new PorterStemmer();
		while (tokenStream.incrementToken()) {
			stemmer.setCurrent(term.toString());
			stemmer.stem();
			String s = stemmer.getCurrent();

			keywords.append(s + " ");
		}
		tokenStream.close();
		return keywords.toString();

	}

	public static void getKeywords(String text) throws IOException {
		TokenStream tokenStream = analyzer.tokenStream("contents",
				new StringReader(text));
		Attribute term = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			Post.appendKeyword(term.toString());
		}
		tokenStream.close();

	}

	public static Set<Data> search(String query) throws ParseException,
			IOException {
		Set<Data> files;
		String stem = stem(query);
		String querystr="";
		if("".equals(stem)) {
			querystr = "header:" + query + " AND contents:" + query;
		} else {
			querystr = "stemmedHeader:" + stem + " AND stemmedContent:" + stem;
		}

		files = sear(querystr);
		
		if(files.size()<10) {
			if("".equals(stem)) {
				querystr = "header:" + query + " OR contents:" + query;
			} else {
				querystr = "stemmedHeader:" + stem + " OR stemmedContent:" + stem;
			}
			files.addAll(sear(querystr));
		}
		
		return files;
	}
	
	private static Set<Data> sear(String querystr) throws IOException, ParseException {
		Set<Data> files = new LinkedHashSet<Data>();
		Query q = new QueryParser("contents", new StandardAnalyzer())
				.parse(querystr);
		int hitsPerPage = 10;
		IndexReader reader = null;

		TopScoreDocCollector collector = null;
		IndexSearcher searcher = null;
		reader = DirectoryReader.open(indexDir);
		searcher = new IndexSearcher(reader);
		collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d;
			d = searcher.doc(docId);
			files.add(new Data(d.get("url"), d.get("contents"), d.get("header")));
		}
		reader.close();
		return files;

	}

}