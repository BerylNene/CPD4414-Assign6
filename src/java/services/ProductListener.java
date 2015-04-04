/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

import static com.sun.org.apache.bcel.internal.Repository.instanceOf;
import static com.sun.org.apache.bcel.internal.Repository.instanceOf;
import entities.ProductList;
import entities.Products;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonObject;



/**
 *
 * @author c0641046
 */
@MessageDriven (mappedName = "jms/Queue")
public class ProductListener implements MessageListener{
    @EJB
    ProductList products;
    
    @Override
    public void onMessage(Message msg){
        try {
           if(msg instanceof TextMessage  ){
            String jsonString = ((TextMessage) msg).getText();
            JsonObject json = Json.createReader(
                        new StringReader(jsonString)).readObject();
                        products.add(new Products(json));
        }
        }
        catch (JMSException ex) {
            System.err.println("JMS Failure");

        } catch (Exception ex) {
            Logger.getLogger(ProductListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    }

