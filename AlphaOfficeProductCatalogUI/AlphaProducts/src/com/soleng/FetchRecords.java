package com.soleng;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;

import javax.naming.InitialContext;


import javax.sql.DataSource;


public class FetchRecords {
	ArrayList<Product> list=new ArrayList<Product>();  
	  
	public ArrayList<Product> getList() {  
	    return list;  
	}  
	public void setList(ArrayList<Product> list) {  
	    this.list = list;  
	}  
	public String execute(){  
	 try{  
	 
		  Context initContext = new InitialContext();
		  Context envContext  = (Context)initContext.lookup("java:/comp/env");
		  System.err.println("envContext Created: "+envContext);
		  DataSource ds = (DataSource)envContext.lookup("jdbc/Alpha01A-DBCS-ds-CanDo");
		  System.err.println("envContext Created: "+ds);
		  
		  Connection con = ds.getConnection();
		 
		 /* Non-data source method...
		  Class.forName("oracle.jdbc.driver.OracleDriver");  
		  Connection con=DriverManager.getConnection(  
		    "jdbc:oracle:thin:@140.86.12.47:1521/PDB1.gse00002055.oraclecloud.internal","alpha","oracle");  
		  */
		              
		  PreparedStatement ps=con.prepareStatement("select product_id, category_id, product_name, external_url, cost_price, list_price, min_price from PRODUCTS");  
		  ResultSet rs=ps.executeQuery();  
		  
		  while(rs.next()){  
		   Product product=new Product();  
		   product.setProductId(rs.getInt(1));  
		   product.setCategoryId(rs.getInt(2));  
		   product.setProductName(rs.getString(3));  
		   product.setExternalUrl(rs.getString(4));  
		   product.setCostPrice(rs.getDouble(5));  
		   product.setListPrice(rs.getDouble(6));  
		   product.setMinPrice(rs.getDouble(7));  
		   list.add(product);  
	  }  
	  
	  con.close(); 
	  
	 
	  
	  
	 }catch(Exception e){e.printStackTrace();}  
	          
	 return "success";  
	}  
}  