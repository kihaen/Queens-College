import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class GetURLInfo {
	
	private String ISBN;

	
	public GetURLInfo(String isbn) throws MalformedURLException, IOException {
			ISBN = isbn;
			String Input =("https://www.amazon.com/dp/"+isbn);
			String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0";
			String Output ="savedSearches";
			URL url = new URL(Input); // currentLink -> Input
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", USER_AGENT);
			printURLinfo(connection,Output); // only system outs it....
			saveText(connection,Output);
			Filter filtered = new Filter();
			filtered.biblioNarrowFilter();
			filtered.biblioSecondFilter();
			filtered.PriceNarrowFilter();
			filtered.PriceSecondFilter();
	} 
	public static void saveImage(URL link,String outname){
		//handles jpeg, jpg, gif.
		try{
		  BufferedImage image = null;
          image = ImageIO.read(link);
          
          //System.out.println(System.getProperty("user.dir"));
          ImageIO.write(image,"jpeg",new File(System.getProperty("user.dir")+"/"+ outname+".jpeg"));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void saveText(URLConnection Site,String outname){
		try{
			//URL Site = new URL(url);
	        BufferedReader inside = new BufferedReader(new InputStreamReader(Site.getInputStream()));
	        BufferedWriter outside = new BufferedWriter(new FileWriter( outname+".txt"));// may need correction
	        String Line;
	        while ((Line = inside.readLine())!=null){
	            outside.write(Line +"\n");
	        }
	        inside.close();
	        outside.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void printURLinfo(URLConnection uc,String OutName) throws IOException {
		// Display the URL address, and information about it.
		String o = uc.getContentType(); // required for compare
		System.out.println(uc.getURL().toExternalForm() + ":");
		System.out.println(" Content Type: " + uc.getContentType());
		System.out.println(o);
			if(o.equals("image/jpeg")||o.equals("image/jpg")||o.equals("image/gif")){
				//System.out.println("content match found!! \n");
				saveImage(uc.getURL(),OutName); //sends link to method
			}
			if(o.equals("text/html")||o.equals("text/html;charset=UTF-8")){
				//System.out.println("success");
				saveText(uc,OutName);
			}
		System.out.println(" Content Length: " + uc.getContentLength());
		System.out.println(" Last Modified: " + new Date(uc.getLastModified()));
		System.out.println(" Expiration: " + uc.getExpiration());
		System.out.println(" Content Encoding: " + uc.getContentEncoding());
		
	}

}