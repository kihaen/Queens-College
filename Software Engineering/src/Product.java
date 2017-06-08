import java.util.Stack;

public class Product {
	private String AuthorFirstname = "";
	private String AuthorLastname = "";// might not use...
	private String Title ="";
	private String yearPublished;//implicit
	private int ISBN;
	private String DatePurchased = "";
	private String DateEntered = ""; // implicit 
	private String DateLastModified = ""; //implicit
	private int Quantity = 1; // implicit
	private String Publisher; // implicit
	private String KindlePrice;
	private String HUsedPrice; // hardcover used and new prices
	private String HNewPrice;
	private String HrealPrice;
	private String PUsedPrice;// paper back used and new prices
	private String PNewPrice;
	private String PrealPrice;
	
	public void setrealPPrice(String price){
		PrealPrice = price;
	}
	public String getrealPPrice(){
		return PrealPrice ;
	}
	public void setrealHPrice(String price){
		HrealPrice = price;
	}
	public String getrealHPrice(){
		return HrealPrice ;
	}
	public void setUsedPPrice(String price){
		PUsedPrice = price;
	}
	public String getUsedPPrice(){
		return PUsedPrice;
	}
	public void setNewPPrice(String price){
		PNewPrice = price;
	}
	public String getNewPPrice(){
		return PNewPrice;
	}
	public void setPublisher(String pub){
		Publisher = pub;
	}
	public String getPublisher(){
		return Publisher; 
	}
	public void setKindlePrice(String price){
		KindlePrice = price;
	}
	public String getKindlePrice(){
		return KindlePrice;
	}
	public void setUsedHPrice(String price){
		HUsedPrice = price;
	}
	public String getUsedHPrice(){
		return HUsedPrice;
	}
	public void setNewHPrice(String price){
		HNewPrice = price;
	}
	public String getNewHPrice(){
		return HNewPrice;
	}
	public String toString(){
		if(HrealPrice!=null&&PrealPrice==null){
			return ISBN +"|"+ AuthorFirstname +"|"+ Title +"|"+ yearPublished +"|"+ Publisher +"|"+ 
					HrealPrice +"|"+ Quantity;
		}
		else if(PrealPrice!=null&&HrealPrice==null){
			return ISBN +"|"+ AuthorFirstname +"|"+ Title +"|"+ yearPublished +"|"+ Publisher +"|"+ 
					PrealPrice +"|"+ Quantity;
		}
		else if(HrealPrice!=null&&PrealPrice!=null){
			return ISBN +"|"+ AuthorFirstname +"|"+ Title +"|"+ yearPublished +"|"+ Publisher +"|"+ 
					PrealPrice +"|"+ HrealPrice +"|"+ Quantity;
		}
		else{
			return ISBN +"|"+ AuthorFirstname +"|"+ Title +"|"+ yearPublished +"|"+ Publisher +"|"+ 
					  Quantity;
		}
	}

	public String getFirstName(){
		return AuthorFirstname;
	}
	public String getLastName(){
		
		return AuthorLastname;
	}
	public int getQ(){
		return Quantity;
	}
	public String getTitle(){
		return Title;
	}
	public String getYearPub(){
		return yearPublished;
	}
	public int getISBN(){
		return ISBN;
	}
	public String getDatePurchased(){
		return DatePurchased;
	}
	public String getDateEntered(){
		return DateEntered;
	}
	public String getDateLastModified(){
		return DateLastModified;
	}
	public void setFirstName(String fname){
		AuthorFirstname = fname;
	}
	public void setLastName(String lname){
		AuthorLastname = lname;
	}
	public void setTitle(String title){
		Title = title;
	}
	public void setYearPub(String i){
		yearPublished = i;
	}
	public void setISBN(int isbn){
		ISBN = isbn;
	}
	public void setDatePurchased(String dp){
		DatePurchased = dp;
	}
	public void setDateEntered(String de){
		DateEntered = de;
	}
	public void setDateLastModified(String dm){
		DateLastModified = dm;
	}
	public void incQ(){
		Quantity++;
	}
	public void decQ(){
		Quantity--;
	}
}
