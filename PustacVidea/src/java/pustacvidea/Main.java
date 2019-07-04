/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pustacvidea;

import database.Istorija;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author vm160089
 */
public class Main {
    
    public static final String kreirajAlarm="PUSTI_PESMU";
    public static final String promeniZvono="POSALJI_ISTORIJU";
    
    
   @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory cf;
    
    @Resource(lookup = "RedZaPustacPesme")
    static javax.jms.Queue operationsQueue;
    
    @Resource(lookup = "RedZaOdgovorePustaca")
    static javax.jms.Queue answersQueue;        //Kada treba da se aktivira pesma
    
   
    
    private static boolean radi=true;
    static EntityManagerFactory emf;
    static EntityManager ENTITY_MANAGER;
    static JMSContext c;
    static JMSConsumer consumer;
    static JMSProducer producer;
    
    public static void main(String[] args) {
        
        emf=Persistence.createEntityManagerFactory("PustacVideaPU");
        ENTITY_MANAGER=emf.createEntityManager();
        
        c=cf.createContext();
        consumer=c.createConsumer(operationsQueue);
        producer=c.createProducer();
        
       while (radi){  
            TextMessage poruka = (TextMessage)consumer.receive();
            
            try {
                String tekstPoruke=poruka.getText();
                String komande[]=tekstPoruke.split("-");
                switch(komande[0]){
                    case "PUSTI_PESMU":
                        pustiPesmu(komande);
                        break;
                    case "POSALJI_ISTORIJU":
                        posaljiIstoriju(komande);
                        break;
                     
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
         
        
        }
    }

    private static void pustiPesmu(String[] komande) {
        Integer id = (Integer)ENTITY_MANAGER.createQuery("SELECT MAX(i.id) from Istorija AS i").getSingleResult();
        if (id==null) id=1;
        else id+=1;
        int idKorisnika=Integer.parseInt(komande[1]);
        String nazivKorisnika=komande[2];
        String nazivPesme=komande[3];
        if (!nazivKorisnika.equals("0")){
        ENTITY_MANAGER.getTransaction().begin();
        Istorija istorija=new Istorija(id,nazivPesme,idKorisnika,nazivKorisnika);
        ENTITY_MANAGER.persist(istorija);
        ENTITY_MANAGER.getTransaction().commit();
        }
        ProcessBuilder processBuild= new ProcessBuilder("java","-jar","C:\\Users\\MARKO\\Documents\\NetBeansProjects\\SmartHouse\\YouTube\\dist\\YouTube.jar",nazivPesme);
        System.out.println("PUSTIO PESMU:"+nazivPesme);
        try {
            processBuild.start();
        } catch (IOException ex) {
            System.out.println("*****************CATCH****************************");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void posaljiIstoriju(String[] komande) {
        int idKorisnik=Integer.parseInt(komande[1]);
        Query query= ENTITY_MANAGER.createQuery("SELECT i FROM Istorija AS i");
        List<Istorija> istorija =query.getResultList();
        String porukaIstorije="";
        for (Istorija i:istorija){
            porukaIstorije+=""+i.getKorisnika()+"-"+i.getNazivKorisnika()+"-"+i.getNazivPesme()+"\n";
        }
        TextMessage textMessage=c.createTextMessage();
        try {
            textMessage.setIntProperty("Id", idKorisnik);
            textMessage.setText(porukaIstorije);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(answersQueue, textMessage);
    }
    
}
