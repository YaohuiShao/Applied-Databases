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
import java.util.HashMap;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.apache.lucene.search.Explanation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;

public class Searcher {
    public static double x=0;
    public static double y=0;
    public static HashMap<String,Double> items = new HashMap<String,Double>();


    public Searcher() {}
    public static void main(String[] args) throws Exception {
        int flag=0;
        double w=0;
	String usage = "java Searcher";
        double bounding[] =new double[4];
        //System.out.println(args[1]+args[2]);

        if (args.length==4){
        x= Double.parseDouble(args[1]);
        y= Double.parseDouble(args[2]);
        w= Double.parseDouble(args[3]);
        flag=1;
        }
        if(args.length<4){
        flag=0 ;
        }
        bounding=compute_bounding(x,y,w);
	search(args[0], "indexes",bounding,flag);
    }    

    private static double compute_dist(double y1, double x1, double y2, double x2){
   
         return (1.60934*((Math.acos(
                          Math.sin(x1*Math.PI/180)*Math.sin(x2*Math.PI/180)+
                    Math.cos(x1*Math.PI/180)*Math.cos(x2*Math.PI/180)*Math.cos((y1-y2)*Math.PI/180))*180/Math.PI)*60*1.1515));
}
    //private static void
    
    private static TopDocs search(String searchText, String p, double[] bound,int flag) {   
	System.out.println("Running search(" + searchText + ")");
        Connection conn =null;
	try {   
  
            double MINLAT=bound[0];
            double MAXLAT=bound[1];
            double MINLON=bound[2];
            double MAXLON=bound[3];
            
	    //HashMap<String,Double> items = new HashMap<String,Double>();

            conn=DbManager.getConnection(false);

            if(flag==1){
            String set="GeomFromText('polygon(("+MINLON+" "+MINLAT+", "+MINLON+" "+MAXLAT+", "+MAXLON+" "+MAXLAT+", "+MAXLON+" "+MINLAT+", "+MINLON+" "+MINLAT+"))') ";

            Statement statement =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String search="SELECT item_id, X(xy), Y(xy) from Item_xy where MBRWithin(xy,"+set+")";
            
            ResultSet item_in_box = statement.executeQuery(search);  
          
            while (item_in_box.next()){
                        
                        double x1= Double.parseDouble(item_in_box.getString("X(xy)"));
                        double y1= Double.parseDouble(item_in_box.getString("Y(xy)"));
                        double dist=compute_dist(x1,y1,x,y);
                        if (dist<10) { 
                        items.put(item_in_box.getString("item_id"),dist);
                        
                      }
                 
             } 
           }


           Statement create =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

           create.executeUpdate("CREATE table Item_out(item_id INTEGER NOT NULL, item_name CHAR(80) NOT NULL,item_score DECIMAL(10,9) NOT NULL,item_distance DECIMAL(10,2) NOT NULL, item_price DECIMAL(10,2) NOT NULL, PRIMARY KEY(item_id));");

	    Path path = Paths.get(p);
	    Directory directory = FSDirectory.open(path);       
	    IndexReader indexReader =  DirectoryReader.open(directory);
	    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	    QueryParser queryParser = new QueryParser("content", new SimpleAnalyzer());  
	    Query query = queryParser.parse(searchText);
	    TopDocs topDocs = indexSearcher.search(query,10000);
	    //System.out.println("Number of Hits: " + topDocs.totalHits);
             
            Statement insert =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
 
            int count=0 ;           
	    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {           
		Document document = indexSearcher.doc(scoreDoc.doc);
                if(items.containsKey(document.get("id"))&&flag==1){
                  count++;
                  int ID=Integer.parseInt(document.get("id"));
                  String name="\""+replace(document.get("name"))+"\"";
                  double score=scoreDoc.score;
                  double distance=items.get(document.get("id"));
                  double price=Double.parseDouble(document.get("price"));
                  insert.executeUpdate("insert into Item_out (item_id,item_name,item_score,item_distance,item_price) values("+ID+","+name+","+score+","+distance+","+price+");");



                }
               if (flag==0){
                  count++;
                  int ID1=Integer.parseInt(document.get("id"));
                  String name1="\""+replace(document.get("name"))+"\"";
                  double score1=scoreDoc.score;
                  double distance1=0;
                  double price1=Double.parseDouble(document.get("price"));
                  insert.executeUpdate("insert into Item_out (item_id,item_name,item_score,item_distance,item_price) values("+ID1+","+name1+","+score1+","+distance1+","+price1+");");
              }

	  }
            System.out.println("Number of Hits: " + count);




            Statement rank =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            if(flag==1){
            ResultSet ordered=rank.executeQuery("select * from Item_out order by item_score desc,item_distance asc, item_price asc");
             while (ordered.next()){
                      System.out.println(ordered.getString("item_id")+","+ordered.getString("item_name")+","+"score:"+ordered.getString("item_score")+","+"dist:"+ordered.getString("item_distance")+","+"price"+ordered.getString("item_price"));
                  }
}
             if (flag==0){
             ResultSet ordered1=rank.executeQuery("select * from Item_out order by item_score desc, item_price asc");
             while (ordered1.next()){
                      System.out.println(ordered1.getString("item_id")+","+ordered1.getString("item_name")+","+"score:"+ordered1.getString("item_score")+","+"price"+ordered1.getString("item_price"));
                  }

}

            Statement drop =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            drop.executeUpdate("drop table if exists Item_out;");
            conn.close();
	    return topDocs;

	} catch (Exception e) {
	    e.printStackTrace();
	    return null;}
}

   // public static String

    public static double[] compute_bounding(double longitude, double latitude, double distance){
    
              double radLat=Math.toRadians(latitude);
              double radLon=Math.toRadians(longitude);

              double radDist= distance/6371.01;

              double minLat=radLat-radDist;
              double maxLat=radLat+radDist;
              double minLon;
              double maxLon;

              
              double MIN_LAT =Math.toRadians(-90d);
              double MAX_LAT =Math.toRadians(90d);
              double MIN_LON =Math.toRadians(-180d);
              double MAX_LON =Math.toRadians(180d);

              double[] bounding=new double[4];
 
              if(minLat>MIN_LAT && maxLat<MAX_LAT){
                    double deltaLon =Math.asin(Math.sin(radDist)/Math.cos(radLat));
                    
                    minLon=radLon-deltaLon;
                    if(minLon<MIN_LON) minLon+=2d*Math.PI;
                     maxLon=radLon+deltaLon;

                    if(maxLon>MAX_LON) maxLon-=2d*Math.PI;
              }else{
                    minLat=Math.max(minLat,MIN_LAT);
                    maxLat=Math.min(maxLat,MAX_LAT);
                    minLon=MIN_LON;
                    maxLon=MAX_LON;
              }
              bounding[0]=Math.toDegrees(minLat);
              bounding[1]=Math.toDegrees(maxLat);
              bounding[2]=Math.toDegrees(minLon);
              bounding[3]=Math.toDegrees(maxLon);
              return bounding;
          }

      public static String  replace(String old){
           String newstring=old.replaceAll("\"","\\\\\"");
           String new1string=newstring.replaceAll("\'","\\\\\'");
           return new1string;}

   
}

