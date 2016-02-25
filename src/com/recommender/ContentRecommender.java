package com.recommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

public class ContentRecommender extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		CrawlAndIndex crawler = new CrawlAndIndex();

		try {
			crawler.startCrawling();

		} catch (Exception e) {

		}
		DataLoader loader = new DataLoader();
		List<JSONObject> dataArray = new ArrayList<JSONObject>();
		List<Post> posts = loader.loadData();

		for (Post post : posts) {
			JSONObject obj = new JSONObject();
			obj.put("type", post.getType());
			obj.put("text", post.hyperLinkKeywords());
			obj.put("code", post.getCode());
			dataArray.add(obj);
		}

		request.setAttribute("posts", dataArray);
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/recommendation.jsp");
		dispatcher.forward(request, response);

	}

}
