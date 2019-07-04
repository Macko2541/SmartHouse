package planer;


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

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import database.Obaveza;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;

/**
 *
 * @author MARKO
 */
public class Main {
    
    private static boolean radi=true;
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory c;
    
    @Resource(lookup = "RedZaOperacijePlanera")
    static javax.jms.Queue redZaOperacijePlanera;
    @Resource(lookup = "RedZaOdgovorePlanera")
    static javax.jms.Queue redZaOdgovorePlanera;
    @Resource(lookup = "RedZaOperacijeAlarma")
    static javax.jms.Queue redZaOperacijeAlarma;
    static JMSContext context;
    static JMSProducer producer;
    static JMSConsumer consumer;
    static EntityManagerFactory emf;
    static EntityManager ENTITY_MANAGER;
    
   
    
    public static void main(String[] args) {
        context = c.createContext();
        producer = context.createProducer();
        consumer = context.createConsumer(redZaOperacijePlanera);
        emf=Persistence.createEntityManagerFactory("PlanerPU");
        ENTITY_MANAGER=emf.createEntityManager();
        
         while (radi){  
            TextMessage poruka = (TextMessage)consumer.receive();
            
            try {
                String tekstPoruke=poruka.getText();
                String komande[]=tekstPoruke.split("-");
                switch(komande[0]){
                    case "A_DO_B":
                        
                        izracunajVremeOdAdoB(komande);
                        break;
                    case "DO_B":
                        izracunajVremeOdTrenutneLokacije(komande);
                        break;
                    case "DODAJ_OBAVEZU":
                        dodajObavezu(komande);
                        break;
                    case "IZMENI_OBAVEZU":
                        izmeniObavezu(komande);
                        break;
                    case "OBRISI_OBAVEZU":
                        obrisiObavezu(komande);
                        break;
                    case "IZLISTAJ_OBAVEZE":
                        izlistajObaveze(komande);
                        break;
                    
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
    }

    private static void izracunajVremeOdAdoB(String[] komande) {
        String lokacijaA=komande[1];
        String lokacijaB=komande[2];
        int idKorisnika=Integer.parseInt(komande[3]);
        //PozoviApi
        String distanca=apiRazdaljina(lokacijaA,lokacijaB);
        TextMessage textMessage=context.createTextMessage();
        try {
            textMessage.setIntProperty("Id", idKorisnika);
        } catch (JMSException ex) {
            
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            textMessage.setText(distanca);
        } catch (JMSException ex) {
            
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(redZaOdgovorePlanera, textMessage);
    }

    private static void izracunajVremeOdTrenutneLokacije(String[] komande) {
        String lokacijaA=komande[1];
        String lokacijaB=komande[2];
        int idKorisnika=Integer.parseInt(komande[3]);
        //PozoviApi
        String distanca=apiRazdaljina(lokacijaA,lokacijaB);
        
        TextMessage textMessage=context.createTextMessage();
        try {
            textMessage.setIntProperty("Id", idKorisnika);
        } catch (JMSException ex) {
            
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            textMessage.setText(distanca);
        } catch (JMSException ex) {
           
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(redZaOdgovorePlanera, textMessage);
    }

    private static void dodajObavezu(String[] komande) {
        Integer id = (Integer)ENTITY_MANAGER.createQuery("SELECT MAX(i.id) from Obaveza AS i").getSingleResult();
        if (id==null) id=1;
        else id+=1;
        String nazivObaveze=komande[1];
        String nazivMestaObaveze=komande[2];
        int dan=Integer.parseInt(komande[3]);
        int mesec=Integer.parseInt(komande[4]);
        int godina=Integer.parseInt(komande[5]);
        int sat=Integer.parseInt(komande[6]);
        int minut=Integer.parseInt(komande[7]);
        int sekund=Integer.parseInt(komande[8]);
        String trenutnoMesto=komande[9];
        int daLiJePodsetnik=Integer.parseInt(komande[10]);
        Date vremeObaveze=new Date();
        vremeObaveze.setYear(godina-1900);
        vremeObaveze.setMonth(mesec-1);
        vremeObaveze.setDate(dan);
        vremeObaveze.setHours(sat);
        vremeObaveze.setMinutes(minut);
        vremeObaveze.setSeconds(sekund);
        long razdaljina;
        Date vremePolaska;
        int provera=1;
        try{
        razdaljina=Long.parseLong(apiRazdaljina(trenutnoMesto,nazivMestaObaveze));
        vremePolaska = new Date(vremeObaveze.getTime()-razdaljina*1000);
        }catch(NumberFormatException e){
        vremePolaska =new Date();
        provera=0;
        }
        
       /* vremePolaska.setYear(vremePolaska.getYear()-1900);
        vremePolaska.setMonth(vremePolaska.getMonth()-1);*/
        Obaveza obaveza= new Obaveza(id,nazivObaveze,nazivMestaObaveze,vremeObaveze,vremePolaska,daLiJePodsetnik);
        ENTITY_MANAGER.getTransaction().begin();
        ENTITY_MANAGER.persist(obaveza);
        ENTITY_MANAGER.getTransaction().commit();
        if (daLiJePodsetnik==1 && provera==1){
        TextMessage msg=context.createTextMessage();
            try {
                msg.setText("KREIRAJ_ALARM-"+nazivObaveze+"-Ringispil-"+vremePolaska.getDate()+"-"+(vremePolaska.getMonth()+1)+"-"+(vremePolaska.getYear()+1900)+"-"+vremePolaska.getHours()+"-"+vremePolaska.getMinutes()+"-"+vremePolaska.getSeconds()+"-0");
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        producer.send(redZaOperacijeAlarma, msg);
        }
    }

    private static void izmeniObavezu(String[] komande) {
        String nazivObaveze=komande[1];
        String nazivMestaObaveze=komande[2];
        int dan=Integer.parseInt(komande[3]);
        int mesec=Integer.parseInt(komande[4]);
        int godina=Integer.parseInt(komande[5]);
        int sat=Integer.parseInt(komande[6]);
        int minut=Integer.parseInt(komande[7]);
        int sekund=Integer.parseInt(komande[8]);
        String trenutnoMesto=komande[9];
        int id=Integer.parseInt(komande[10]);
        //NEMA DA LI JE PODSETNIK
        Date vremeObaveze=new Date();
        vremeObaveze.setYear(godina-1900);
        vremeObaveze.setMonth(mesec-1);
        vremeObaveze.setDate(dan);
        vremeObaveze.setHours(sat);
        vremeObaveze.setMinutes(minut);
        vremeObaveze.setSeconds(sekund);
        long razdaljina;
        Date vremePolaska;
        int provera=1;
        try{
        razdaljina=Long.parseLong(apiRazdaljina(trenutnoMesto,nazivMestaObaveze));
        vremePolaska = new Date(vremeObaveze.getTime()-razdaljina*1000);
        }catch(NumberFormatException e){
        vremePolaska =new Date();
        provera=0;
        }
        Obaveza o=ENTITY_MANAGER.find(Obaveza.class, id);
        ENTITY_MANAGER.getTransaction().begin();
        o.setNazivMestaObaveze(nazivMestaObaveze);
        String nazivStareObaveze=o.getNazivObaveze();
        int daLiJePodsetnik=o.getDaLiJePodsetnik();
        o.setNazivObaveze(nazivObaveze);
        o.setVremeObaveze(vremeObaveze);
        o.setVremePolaska(vremePolaska);
        ENTITY_MANAGER.getTransaction().commit();
         if (daLiJePodsetnik==1 && provera==1){
        TextMessage msg=context.createTextMessage();
            try {
                msg.setText("KREIRAJ_ALARM-"+nazivObaveze+"-Ringispil-"+vremePolaska.getDate()+"-"+(vremePolaska.getMonth()+1)+"-"+(vremePolaska.getYear()+1900)+"-"+vremePolaska.getHours()+"-"+vremePolaska.getMinutes()+"-"+vremePolaska.getSeconds()+"-0");
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        producer.send(redZaOperacijeAlarma, msg);
        }
         
    }

    private static void obrisiObavezu(String[] komande) {
        int id=Integer.parseInt(komande[1]);
        Obaveza o = ENTITY_MANAGER.find(Obaveza.class, id);
        ENTITY_MANAGER.getTransaction().begin();
        ENTITY_MANAGER.remove(o);
        ENTITY_MANAGER.getTransaction().commit();
    }

    private static void izlistajObaveze(String[] komande) {
        int p=Integer.parseInt(komande[1]);
        Query query= ENTITY_MANAGER.createQuery("SELECT i FROM Obaveza AS i");
        List<Obaveza> obaveze= query.getResultList();
        String s="";
        for (Obaveza iter:obaveze){
            s+="id:"+iter.getId()+"\nnaziv obaveze:"+iter.getNazivObaveze()+"\nmesto obaveze:"+iter.getNazivMestaObaveze()+ "\nvreme:"+iter.getVremeObaveze()+"\n\n\n";
        
        }
        TextMessage textMessage = context.createTextMessage();
        try {
            textMessage.setIntProperty("Id", p);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {       
                    textMessage.setText(s);     
                    producer.send(redZaOdgovorePlanera, textMessage);
                } catch (JMSException ex) {  }
    }

    
    
    
    private static String apiRazdaljina(String A,String B){
    try {
            GeoApiContext distCalcer = new GeoApiContext.Builder().apiKey("AIzaSyCWzFYcQ9avpZqS241ulO2m-lohahO1nbc").build();

            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer);
            DistanceMatrix result = req.origins(A).destinations(B).mode(TravelMode.DRIVING).await();
            System.out.println(""+result.rows[0].elements[0].duration.inSeconds);
            if (result.rows[0] == null) {
                return "";
            }
            return ""+result.rows[0].elements[0].duration.inSeconds;
        } catch (ApiException ex) {
            
            System.err.println(ex.toString());
        } catch (InterruptedException ex) {
            
        } catch (IOException ex) {
           
        } catch (Exception e) {
            
        }
        return "Previse je udaljeno";
    
    
    }
    
    
    
    
    
}
    
    
    
   
    
   
    
    
    
    
    
  
   
    


    
    

