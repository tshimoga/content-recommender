Author

-------------------------------------------------

Name: Tarun Shimoga



Description
------------------------------------------------


The Content Recommender crawls contents from 
Java wikibooks-https://en.wikibooks.org/wiki/Java_Programming
and Oracle Java Tutorial-https://docs.oracle.com/javase/tutorial/java/TOC.htmlOracle
and suggests content based on the posts. It also
suggests content based on the keywords crawled from those sites. The
implementation is broken down into sub tasks namely:
	
1. Crawling
2. Indexing
3. Content Loading and Creation
4. Searching
5. Recommendation Presentation
			
Classes                  Responsibilities
				
DataLoader		Loads data from data.xlsx using poi-ooxml-3.11-beta2 library
Post			Object representation of individual posts. Post object has
			three attributes, type, data and code.
Data			Object representation of recommendations. Data object has
			attributes url, content and header
CrawlAndIndex		Performs crawling as mentioned in the Crawling section and
			calls IndexAndSearch for Indexing	
IndexAndSearch		Responsible for Indexing and Searching as mentioned in
			detail.	
		
		
			
Crawling
			
The class responsible for crawling is CrawlAndIndex. This class uses
JSoup to get the source from sites, breaks the page content into
logical topic wise sub contents and passes the information to Indexer
performed by IndexAndSearch. Detailed functioning:
The Crawler looks for headings in a page, h1 or h2 tags. This serves
as the topic for the document. The crawler then reads the contents of
the p tag, till the next h2 tag is encountered. This entire content
from heading tag to the next h tag serves as the main content of the
document. Keyword collection: The crawler
looks for keywords in the page content of urls that have "keyword" in
them. It then adds it to the list which will later be used to
highlight keywords in the posts.		

		
Indexing			
			
IndexAndSearch is the class responsible for indexing crawled
content given by CrawlAndIndex. Stemming is performed by a method
called String:stem(String). Each document consists of 5 key value pair
which are
	
1. url: which has the url
2. header: which has the heading of the subtopic
3. stemmedHeader: contains the heading of the subtopic after
		  stemming (stemmedHeader is given a boost of 2.0f)
4. contents: contains the content of the subtopic		
5. stemmedContents: which has the content of the subtopic after
		    stemming		
				

Content Loading and Creation:
		
The content is provided in Excel format. A library called			
"poi-ooxml" is used to parse the Excel sheet and load content into
objects representing the posts called Post. The Post class consists of
text, code and type representing each post in the excel sheet. Apart
from that, it is also responsible for maintaining a list of keywords
that will be used to highlight them in UI. The Post objects are then
shown to the User. jstl libraries are used to perform looping and
relative url mapping			
		

	
Searching:

Searching is performed through ajax call and can be done in two ways
1. Post wise search 
2. Keyword wise search 

Post wise search is performed by clicking on the panel heading of each post,
which then filters the post contents based on keywords, and then is
forwarded to IndexAndSearch for searching. IndexAndSearch
then performs stemming of the given query string, and searches in
stemmedHeader and stemmedContent attributes. The results are then
loaded into a Set of "Data" which represents each result.
Keyword wise search is directly given to the IndexAndSearch for
searching which does the exact same steps as mentioned above.
The search query performs a multi-field search as follows 
querystr = "stemmedHeader:" + stem + " AND stemmedContent:" + stem;		
	
	
Recommendation Presentation:

Each recommendation JSON object is presented in a panel with the
heading being the heading of the content indexed and the body being
the content. The content is truncated to 60 words and is clickable
which redirects the user to the URL of the actual crawled page.		
	
	
Project information:
	
Dependencies are maven managed. Context root is
/content-recommender/. Use Tomcat7 for deployment.		
	
	
	
	

	
		
	
	
		
	

	
		
	
	
		
	
	

	
		
	
	
	
	

	
		
	
	
		