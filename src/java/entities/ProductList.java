/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author c0641046
 */
@ApplicationScoped
public class ProductList {
    private List <Products> productList;
    
    public ProductList(){
        productList=new ArrayList<>();
        try (Connection conn = getConnection()){
            String query = "SELECT * FROM products";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Products p = new Products (
                rs.getInt("productID"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("quantity"));
                productList.add(p);
            }
            }
            catch (SQLException ex){
                    Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
                    }
        }
    
    public JsonArray toJSON(){
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Products p : productList){
            json.add(p.toJSON());
        }
        return json.build();
    }
    
    public Products get(int productID){
        Products result = null;
       for(Products p : productList){
            if (p.getProductID() == productID){
                result = p;
            }
        }          
        return result;
    }
    
    public void add(Products p) throws Exception {
        int result = doUpdate("INSERT INTO products (productID, name, description, quantity",
        String.valueOf(p.getProductID()),
        p.getName(),
        p.getDescription(),
        String.valueOf(p.getQuantity()));
        if (result > 0){
            productList.add(p);
        }
        else {
            throw new Exception("Insertion Failed");
    }
}
    
   public void remove(Products p) throws Exception{
       remove(p.getProductID());
  }
    
    public void remove (int productID) throws Exception{
        int result = doUpdate("DELETE FROM products WHERE ProductID = ?",
                String.valueOf(productID));
        if (result > 0){
            Products original = get(productID);
            productList.remove(original); 
        }
          else {
            throw new Exception("Deletion Failed");
    }
    }
    
    public void Set(int productID, Products products) throws Exception{
        int result = doUpdate("UPDATE products SET name = ?, description = ?, quantity = ?",
              products.getName(),
                products.getDescription(),
                String.valueOf(products.getQuantity()),
                String.valueOf(productID)
                        );
        
        if(result>0){        
        Products original = get(productID);
        original.setName(products.getName());
        original.setDescription(products.getDescription());
        original.setQuantity(products.getQuantity());
    }
        else
        {
                throw new Exception("Update Failed");
                }
    }
    
    private Connection getConnection() {
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://localhost/beryl_products";
            String user = "root";
            String pass = "";
            conn = DriverManager.getConnection(jdbc, user, pass);
        }
        catch (ClassNotFoundException | SQLException ex){
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    
    private String getResults(String query, String... params){
        StringBuilder sb =new StringBuilder();
        try(Connection conn = getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++){
                pstmt.setString(i, params[i - 1]);
            }
           ResultSet rs = pstmt.executeQuery();
           
           while (rs.next()){
               sb.append(String.format("%s\t%s\t%s\n",
                       rs.getInt("productID"),
                       rs.getString("name"), 
                       rs.getString("description"), 
                       rs.getInt("quantity")));
           }
        }
           catch (SQLException ex){
                   Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
                   }
           return sb.toString();
        }
    
    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i =1; i <=params.length; i++){
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        }
        catch (SQLException ex){
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);   
        }
        return numChanges;
    }
    }


        
    
    
