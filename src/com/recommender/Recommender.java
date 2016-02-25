package com.recommender;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryparser.classic.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Recommender extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		DataLoader loader = new DataLoader();
		List<Post> dataList = loader.loadData();
		String requestedSearchString;
		if (req.getParameter("post").equals("1")) {
			Post query = dataList.get(Integer.parseInt(req
					.getParameter("query")) - 1);
			requestedSearchString = query.getKeywords();
		} else {
			requestedSearchString = req
					.getParameter("query");
		}
		 

		Set<Data> files=null;
		try {
			files = IndexAndSearch.search(requestedSearchString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONArray result = new JSONArray();
		
		for(Data d : files) {
			JSONObject obj = new JSONObject();
			obj.put("url",d.getUrl());
			obj.put("header",d.getHeader());
			obj.put("content",getInitialWords(d.getContent()));
			result.add(obj);
		}
		
		

		resp.setContentType("text/text");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(result.toJSONString());

	}
	
	public String getInitialWords(String text) {
		StringBuilder builder = new StringBuilder();
		String[] arr = text.split(" ");
		
		for(int i=0;i<60;i++) {
			if(i==arr.length) {
				break;
			}
			builder.append(arr[i] + " ");
		}
		return builder.toString();
	}

}
