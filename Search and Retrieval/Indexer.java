import java.io.StringReader;
import java.io.File;
import java.nio.file.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.sql.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Indexer {
    public Indexer() {}
    public static IndexWriter indexWriter;
    public static void main(String args[]) {
	String usage = "java Indexer";
	rebuildIndexes("indexes");
    }

    public static void insertDoc(IndexWriter i, int id,String name, String categories,String description,double price){
	Document doc = new Document();
        doc.add(new TextField("id", Integer.toString(id), Field.Store.YES));
	doc.add(new TextField("name", name, Field.Store.YES));
	doc.add(new TextField("categories", categories,Field.Store.YES));
        doc.add(new TextField("description", description,Field.Store.NO));
        doc.add(new TextField("price", Double.toString(price),Field.Store.YES));

        String contentString  = name+" "+ categories+" "+description;

        doc.add(new TextField("content",contentString,Field.Store.NO));

	try { i.addDocument(doc); } catch (Exception e) { e.printStackTrace(); }
    }

    public static void rebuildIndexes(String indexPath) {
	try {
	    Connection conn =null;
	    
            conn=DbManager.getConnection(true);

            Statement statement =conn.createStatement();

            String query="SELECT item.item_id,item.item_name,item.description,IC.Categories, auction.current_price FROM ((SELECT item_id , GROUP_CONCAT(has_category.category_name  SEPARATOR ' ') AS Categories FROM has_category GROUP BY item_id) AS IC INNER JOIN item ON item.item_id=IC.item_id) INNER JOIN auction ON auction.item_id=IC.item_id";

            ResultSet items = statement.executeQuery(query);

	    Path path = Paths.get(indexPath);
	    System.out.println("Indexing to directory '" + indexPath + "'...\n");
	    Directory directory = FSDirectory.open(path);
	    IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
	    //	    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	    //IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
	    IndexWriter i = new IndexWriter(directory, config);
	    i.deleteAll();
	    //insertDoc(i, "1", "The old night keeper keeps the keep in the town");
	    while (items.next()){
    
                         insertDoc(i,items.getInt("item_id"),items.getString("item_name"),items.getString("Categories"),items.getString("description"),items.getDouble("current_price"));

            }
            conn.close();
	    i.close();
	    directory.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
