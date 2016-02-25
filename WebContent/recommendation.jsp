<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">




<html>
<head>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Content Recommender</title>

<style type="text/css">
.panel-heading:hover {
	background-color: #909090;
}

div.outer {
	width: 100%;
	display: inline-block;
}

div.container {
	display: inline-block;
	margin-left: 10px;
	width: 35%;
	float: left;
}
</style>

<script type="text/javascript">

function getRecommendations(query,post) {
		$.ajax({
			url: "<c:url value="/recommend" />",
			data :{ "query":query, "post":post},
			success: function(res) {
				var data = JSON.parse(res);
				buildTable(data);
			}
		});
		
}



function buildTable(arr) {
	
	var container = document.getElementById("container1");
	var children = container.children;
	if(children.length!=0) {
		for(i=0;i<children.length;) {
			container.removeChild(children[i]);
		}
	}
	
	if(arr.length==0) {
		var p = document.createElement('p');
		p.innerHTML = "No recommendations for the keyword.";
		container.appendChild(p);
	}
	for(var i=0;i<arr.length;i++){
		var panel  = document.createElement('div');
		panel.className = "panel panel-default";
		var panelHeading = document.createElement('div');
		panelHeading.className = "panel-heading";
	    panelHeading.innerHTML = arr[i].header;
	     
	    var panelBody = document.createElement('div');
		panelBody.className = "panel-body";
		panelBody.innerHTML = "<a href='" + arr[i].url + "' >" +  arr[i].content + "..." + "</a>";
	    panel.appendChild(panelHeading);
	    panel.appendChild(panelBody);
	    container.appendChild(panel);
	    
    }
	
}
	
</script>

</head>
<body>
	<div class="outer">
		<div class="container">
			<h2>Posts</h2>

			<c:forEach items="${posts}" var="post" varStatus="i">
				<div class="panel panel-default">
					<div class="panel-heading"
						onClick="getRecommendations(${i.count},1)">
						<a href="javascript:void(0);"><b>Post ${i.count}:
								${post.type}</b></a>
					</div>
					<div class="panel-body">
						<b>Text:</b><br />${post.text}</div>
					<c:if test='${post.code!=""}'>
						<div class="panel-body">
							<b>Code:</b><br />${post.code}</div>
					</c:if>
				</div>
			</c:forEach>

		</div>
		<div style="float: left; width: 55%; display: inline-block;">
			<h2>Recommendations</h2>
			<div id="container1" style="float: left;"></div>
		</div>
	</div>
	<h1>
		<b>Implementation:</b>
	</h1>
	<br />
	<p>
		The Content Recommender crawls contents from <a
			href="https://en.wikibooks.org/wiki/Java_Programming">Java
			Wikibooks</a> and <a
			href="https://docs.oracle.com/javase/tutorial/java/TOC.html">Oracle
			Java Tutorial</a> and suggests content based on the posts. It also
		suggests content based on the keywords crawled from those sites. The
		implementation is broken down into sub tasks namely:
	</p>
	<ul>
		<li>Crawling</li>
		<li>Indexing</li>
		<li>Content Loading and Creation</li>
		<li>Searching</li>
		<li>Recommendation Presentation</li>
	</ul>

	<table>
		<tr >
			<th style="width:150px">Classes</th>
			<th>Responsibilities</th>
		</tr>
		<tr>
			<td>DataLoader</td>
			<td>Loads data from data.xlsx using poi-ooxml-3.11-beta2 library</td>
		</tr>
		<tr>
			<td>Post</td>
			<td>Object representation of individual posts. Post object has
				three attributes, type, data and code.</td>
		</tr>
		<tr>
			<td>Data</td>
			<td>Object representation of recommendations. Data object has
				attributes url, content and header</td>
		</tr>

		<tr>
			<td>CrawlAndIndex</td>
			<td>Performs crawling as mentioned in the Crawling section and
				calls IndexAndSearch for Indexing</td>
		</tr>

		<tr>
			<td>IndexAndSearch</td>
			<td>Responsible for Indexing and Searching as mentioned in
				detail.</td>
		</tr>

	</table>

	<h2>
		<b>Crawling</b>
	</h2>
	<p>
		The class responsible for crawling is CrawlAndIndex. This class uses
		JSoup to get the source from sites, breaks the page content into
		logical topic wise sub contents and passes the information to Indexer
		performed by IndexAndSearch.<br> <b>Detailed functioning:</b><br>
		The Crawler looks for headings in a page, h1 or h2 tags. This serves
		as the topic for the document. The crawler then reads the contents of
		the p tag, till the next h2 tag is encountered. This entire content
		from heading tag to the next h tag serves as the main content of the
		document.<br> <b>Keyword collection:</b><br> The crawler
		looks for keywords in the page content of urls that have "keyword" in
		them. It then adds it to the list which will later be used to
		highlight keywords in the posts.
	</p>
	<br>

	<h2>
		<b>Indexing</b>
	</h2>
	<p>IndexAndSearch is the class responsible for indexing crawled
		content given by CrawlAndIndex. Stemming is performed by a method
		called String:stem(String). Each document consists of 5 key value pair
		which are</p>
	<ul>
		<li>url: which has the url</li>
		<li>header: which has the heading of the subtopic</li>
		<li>stemmedHeader: contains the heading of the subtopic after
			stemming <b>(stemmedHeader is given a boost of 2.0f)</b>
		</li>
		<li>contents: contains the content of the subtopic</li>
		<li>stemmedContents: which has the content of the subtopic after
			stemming</li>
	</ul>
	<br>

	<h2>
		<b>Content Loading and Creation:</b>
	</h2>
	<p>The content is provided in Excel format. A library called
		"poi-ooxml" is used to parse the Excel sheet and load content into
		objects representing the posts called Post. The Post class consists of
		text, code and type representing each post in the excel sheet. Apart
		from that, it is also responsible for maintaining a list of keywords
		that will be used to highlight them in UI. The Post objects are then
		shown to the User. jstl libraries are used to perform looping and
		relative url mapping</p>
	<br>

	<h2>
		<b>Searching:</b>
	</h2>
	<p>
		Searching is performed through ajax call and can be done in two ways<br>
		1. Post wise search<br> 2. Keyword wise search<br> Post wise
		search is performed by clicking on the panel heading of each post,
		which then filters the post contents based on keywords, and then is
		forwarded to IndexAndSearch for searching.<br> IndexAndSearch
		then performs stemming of the given query string, and searches in
		stemmedHeader and stemmedContent attributes. The results are then
		loaded into a Set of "Data" which represents each result.<br>
		Keyword wise search is directly given to the IndexAndSearch for
		searching which does the exact same steps as mentioned above.<br>
		The search query performs a multi-field search as follows <i>querystr
			= "stemmedHeader:" + stem + " AND stemmedContent:" + stem;</i>
	</p>
	<br>

	<h2>
		<b>Recommendation Presentation:</b>
	</h2>
	<p>Each recommendation JSON object is presented in a panel with the
		heading being the heading of the content indexed and the body being
		the content. The content is truncated to 60 words and is clickable
		which redirects the user to the URL of the actual crawled page.</p>
	<br>
	<br>

	<h2>
		<b>Project information:</b>
	</h2>
	<p>
		Dependencies are maven managed.<br> Context root is
		/content-recommender/.<br> Use Tomcat7 for deployment.
	</p>
	<h1 style="color: red;">NOTE: Some of the keywords will not
		recommend content because they are blocked by the analyzer.</h1>


</body>
</html>