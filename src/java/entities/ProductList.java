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
        try (Connection conn = getConnection()){
            String query = "SELECT * FROM products";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Products p = new Products (
                rs.getInt("ProductId"),
                rs.getString("Name"),
                rs.getString("Description"),
                rs.getInt("Quantity"));
                productList.add(p);
            }
            }
            catch (SQLException ex){
                    Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
                    }
        }
    
    public JsonArray toJSON(){
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Products p : productList)
            json.add(p.toJSON());
        return json.build();
    }
    
    public Products get(int productId){
        Products result = null;
        for (int i = 0; i < productList.size() && result == null; i++){
        Products p = productList.get(i);
            if (p.getProductId() == productId){
                result = p;
            }
        }          
        return result;
    }
    
    public void add(Products p) throws Exception {
        int result = doUpdate("INSERT INTO Products (ProductID, Name, Description, Quantity",
        String.valueOf(p.getProductId()),
        p.getName(),
        p.getDescription(),
        String.valueOf(p.getQuantity()));
        if (result > 0){
            productList.add(p);
        }
        else throw new Exception("Error Insertion");
    }
    
   public void remove(Products p) throws Exception{
       remove(p.getProductId());
  }
    
    public void remove (int productId){
        int result = doUpdate("DELETE FROM Products WHERE ProductID = ?",
                String.valueOf(productId));
        if (result > 0){
            Products original = get(productId);
            productList.remove(original); 
        }
    }
    
    public void Set(int productId, Products products){
        int result = doUpdate("UPDATE Products SET Name = ?, Description = ?, Quantity = ?",
              products.getName(),
                products.getDescription(),
                String.valueOf(products.getQuantity()),
                String.valueOf(productId)
                        );
                
        Products original = get(productId);
        original.setName(products.getName());
        original.setDescription(products.getDescription());
        original.setQuantity(products.getQuantity());
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
                       rs.getInt("id"),
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


        
    
    