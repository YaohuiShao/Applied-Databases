

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;


public class MySAX extends DefaultHandler
{
	
	StringBuffer sb = new StringBuffer();  
	String IDtemp;
	int Cattemp=0;
    private static List<String> ItemList; 
    private static List<String> SellerList;
    private static List<String> BidList;
    private static List<String> BidderList;
    private static List<String> CategoryList;
    private static List<String> Buy_priceList;
    private static List<String> LocationList;
    private static List<String> BidderLocation;
    private static List<String> BidderCountry;
    private boolean ISName = false; 
    private boolean ISCurrently =false;
    private boolean ISFb=false;
    private boolean ISNob=false;
    private boolean ISLocation=false;
    private boolean ISStarted=false;
    private boolean ISEnds=false;
    private boolean ISDec=false;
    private boolean ISBidtime=false;
    private boolean ISAmount=false;
    private boolean ISBid=false;
    private boolean ISCountry=false;
    private boolean ISCategory=false;
    private boolean IS_buyprice=false;
    private boolean IS_duplicate=true;
    
    String BIDtemp;
    Map<String ,Integer> sellermap = new HashMap<String,Integer>(); 
    Map<String ,Integer> biddermap = new HashMap<String,Integer>(); 
    int document_count=0;
    
    
    
    
    public static void main (String args[])
	throws Exception
    {
    	
        ItemList = new ArrayList<String>(); 
        SellerList = new ArrayList<String>(); 
        BidList = new ArrayList<String>(); 
        BidderList = new ArrayList<String>(); 
        CategoryList = new ArrayList<String>();
        Buy_priceList= new ArrayList<String>();
        LocationList= new ArrayList<String>();
        BidderLocation=new ArrayList<String>();
        BidderCountry=new ArrayList<String>();
    
	XMLReader xr = XMLReaderFactory.createXMLReader();
	MySAX handler = new MySAX();
	xr.setContentHandler(handler);
	xr.setErrorHandler(handler);

				// Parse each file provided on the
				// command line.
	for (int i = 0; i < args.length; i++) {
	    FileReader r = new FileReader(args[i]);
	    
	    xr.parse(new InputSource(r));
	}  
	exportCsv(new File("Item.csv"), ItemList);
	exportCsv(new File("Seller.csv"), SellerList);
	exportCsv(new File("Bids.csv"), BidList);
	exportCsv(new File("Bidder.csv"), BidderList);
	exportCsv(new File("ItemCategory.csv"), CategoryList); 
	exportCsv(new File("Buy_Price.csv"), Buy_priceList); 
	exportCsv(new File("ItemLocation.csv"), LocationList);
	exportCsv(new File("BidderLocation.csv"), BidderLocation); 
	exportCsv(new File("BidderCountry.csv"), BidderCountry);
  
    }


    public MySAX ()
    {
	super();
    }

    

    @Override 
    public void startDocument()  { 
    	

    } 
    // Called at end of an XML document 
    @Override 
    public void endDocument()  { 
    
    } 
    
  
    @Override 
    public void startElement(String uri, String localName, String qName, 
	     Attributes atts)  { 
    	
       if(qName.equals("Item")){
     	   IDtemp=atts.getValue(0);
     	   ItemList.add(atts.getValue(0));
    	   
        }
     
       else  if (qName.equals("Name")) { 
    	 sb.delete(0, sb.length());
         ISName = true; 
       } 
       
       else if (qName.equals("Category")) { 
    	 sb.delete(0, sb.length());
         ISCategory = true; 
       } 
       
       else if(qName.equals("Currently")){
    	  ISCurrently= true;
       }
       
       else if(qName.equals("Buy_Price")){
    	   IS_buyprice= true;
    	   Buy_priceList.add(IDtemp);
       }
       
       else if(qName.equals("First_Bid")){
    	  ISFb= true;
       }
       else if(qName.equals("Number_of_Bids")){
    	  ISNob= true;
       }
       else if(qName.equals("Bids")){
    	   ISBid=true;
    	   
       }
       else if(qName.equals("Bidder")){
    	   BidList.add(IDtemp);
    	   BidList.add(atts.getValue(1));
    	   IS_duplicate=false;
    	   if(!biddermap.containsKey(atts.getValue(1))){
    		   BidderList.add(atts.getValue(1));
    		   BidderList.add(atts.getValue(0)+"\n");
    		   BIDtemp=atts.getValue(1);
    		   IS_duplicate=true;
    		   biddermap.put(atts.getValue(1),1);
    	   }
	   
       }
       else if(qName.equals("Location")){
    	   sb.delete(0, sb.length());
    	   if (atts.getLocalName(0)=="Latitude"){
    		   LocationList.add(IDtemp);
    		   LocationList.add( atts.getValue(0));
    		   LocationList.add( atts.getValue(1)+"\n");
    		}
    	   ISLocation=true;
       }
       
       else if(qName.equals("Country")){
    	   ISCountry=true;
       }
       
       else if(qName.equals("Time")){
    	   ISBidtime=true;
    	   
       }
       
       else if(qName.equals("Amount")){
    	   ISAmount=true;
    	   
       }
       
       else if(qName.equals("Started")){
    	  ISStarted= true;
       }
       
       else if(qName.equals("Ends")){
    	  ISEnds= true;
       }
       
       else if(qName.equals("Seller")){
    	   ItemList.add(atts.getValue(1));
    	   if(!sellermap.containsKey(atts.getValue(1))){
    	   SellerList.add(atts.getValue(1));
    	   SellerList.add(atts.getValue(0)+"\n");
    	   sellermap.put(atts.getValue(1),1);
    	   }
    	   
       }
       
       else if(qName.equals("Description")){
    	   sb.delete(0, sb.length());
    	   ISDec=true;
       }
       
    } 
    
    @Override 
    public void characters(char[] ch, int start, int length) { 
       // Processing character data inside an element 
       String str= new String(ch,start,length);
       if (ISName|| ISDec || ISCategory ||ISLocation) { 
    	   sb.append(str);  
       } 
       else if ( ISNob) { 
    	   ItemList.add(str);
       } 
       else if (ISStarted||ISEnds) { 
    	   ItemList.add(StringToTimestamp(str));
    	   
       } 
       
       else if (ISCurrently || ISFb ) { 
    	   str=strip(str);
    	   ItemList.add(str);
       } 
       
       else if (ISBidtime) { 
    	   BidList.add(StringToTimestamp(str).toString());
       } 
       else if (ISAmount) { 
    	   str=strip(str);
    	   BidList.add(str+"\n");
       } 
       
       else if (IS_buyprice) { 
    	   str=strip(str);
    	   Buy_priceList.add(str+"\n");
       } 
       
       else if (ISCountry) { 
    	   if(ISBid && IS_duplicate) {
    		  BidderCountry.add(BIDtemp);
    	   	  BidderCountry.add(str+"\n");
    	   }
    	   else if(!ISBid)
    		  ItemList.add(str);
    	   
       } 

    } 
 
    @Override 
    public void endElement(String namespaceURI, String localName, String qName) 
        { 
       // End of processing current element 
       if (qName.equals("Name")) { 
    	   
    	   String str=sb.toString(); 
           ItemList.add(str); 
           ISName = false; 
       } 
       else if (qName.equals("Category")) { 
    	   String str=sb.toString(); 
    	   CategoryList.add(IDtemp);
    	   CategoryList.add(str+"\n");
    	   ISCategory=false;
    		   
       } 
       else if(qName.equals("Currently")){
           ISCurrently = false;   	   
       }
       else if(qName.equals("Buy_Price")){
    	   IS_buyprice= false;
       }
       else if(qName.equals("First_Bid")){
    	   ISFb=false;
       }
       else if(qName.equals("Number_of_Bids")){
    	   ISNob=false;
       }
       
       else if(qName.equals("Country")){
    	   ISCountry=false;
       }
       
       else if(qName.equals("Bids")){
    	   ISBid=false;
       }
       
       else if(qName.equals("Location")){
    	   String str=sb.toString();
    	   if(ISBid && IS_duplicate)  {
    		  BidderLocation.add(BIDtemp);
    	   	  BidderLocation.add(str+"\n");
    	   	  }
    	   else if(!ISBid)
    		  ItemList.add(str); 
    	   ISLocation=false;
       }
       else if(qName.equals("Started")){
    	   ISStarted=false;
       }
       else if(qName.equals("Ends")){
    	   ISEnds=false;
       }

       else if(qName.equals("Time")){
    	   ISBidtime=false;
       }
       else if(qName.equals("Amount")){
    	   ISAmount=false;
       }
       else if(qName.equals("Description")){
    	   String str=sb.toString(); 
    	   if(str.length()>4000)
    	   {
    	        str=str.substring(0,4000);
    	   }

           ItemList.add(str+"\n");
    	   ISDec=false;
       }

    } 
 			

    
    
    
    public static boolean exportCsv(File file, List<String> dataList){
        boolean isSucess=false;
        
        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw =new BufferedWriter(osw);
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                	if(data.contains("\n"))
                		bw.append(data);
                	else
                		bw.append(data).append("|");
                		
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }
        
        return isSucess;
    }
		
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

  
    public static String StringToTimestamp(String time){
  	   String day=time.substring(4,6);
  	   String year=time.substring(7,9);
  	   String hour=time.substring(10,12);
  	   String min=time.substring(13,15);
  	   String s=time.substring(16,18);
  	   String newtime ;
 	   if(time.substring(0,3).equals("Dec")){
 		   newtime=("20"+year+"12"+day+hour+min+s);
 	   }
 	   else if(time.substring(0,3).equals("Nov")){
 		  newtime=("20"+year+"11"+day+hour+min+s);
 	   }
 	   else
 		   newtime="";

        return newtime; 
    }


}
