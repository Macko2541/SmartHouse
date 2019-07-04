/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appalarm;



import database.AlarmEntity;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
 * @author MARKO
 */
public class Main {

    
   @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory cf;
    
    @Resource(lookup = "RedZaOperacijeAlarma")
    static javax.jms.Queue operationsQueue;
    
    @Resource(lookup = "RedZaOdgovoreAlarma")
    static javax.jms.Queue answersQueue;     //Kada treba da se aktivira pesma
    
    @Resource(lookup="RedZaPustacPesme")
    static javax.jms.Queue pustacQueue;
    
    private static boolean radi=true;
    static EntityManagerFactory emf;
    static EntityManager ENTITY_MANAGER;
    static JMSContext c;
    static JMSConsumer consumer;
    static JMSProducer producer;
    
    public static void main(String[] args) {
        
        emf=Persistence.createEntityManagerFactory("AppAlarmPU");
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
                    case "KREIRAJ_ALARM":
                        kreirajAlarm(komande);
                        break;
                    case "PROMENI_ZVONO":
                       promeniZvono(komande);
                        break;
                    case "NAVIJ_PONUDJENI":
                        navijPostojeciAlarm(komande);
                        break;
                    case "PONUDI_ALARME":
                        ponudiAlarme(komande);
                        break;
                    case "IZMENI_ALARM":
                        izmeniAlarm(komande);
                        break;
                     
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
         
        
        }
        
    }
    
    
    private static void kreirajAlarm(String[] komande) {
        Integer id = (Integer)ENTITY_MANAGER.createQuery("SELECT MAX(i.id) FROM AlarmEntity AS i").getSingleResult();
        if (id==null) id=1;
        else id+=1;
        String nazivAlarma=komande[1];
        String nazivZvona=komande[2];
        Date vreme=new Date();
        vreme.setDate(Integer.parseInt(komande[3]));
        vreme.setMonth(Integer.parseInt(komande[4])-1);
        vreme.setYear(Integer.parseInt(komande[5])-1900);
        vreme.setHours(Integer.parseInt(komande[6]));
        vreme.setMinutes(Integer.parseInt(komande[7]));
        vreme.setSeconds(Integer.parseInt(komande[8]));
        String period=komande[9];
        final int idAlarma=id;
        AlarmEntity noviAlarm=new AlarmEntity(id,nazivAlarma,nazivZvona,vreme,Integer.parseInt(period),1);
        ENTITY_MANAGER.getTransaction().begin();
        ENTITY_MANAGER.persist(noviAlarm);
        ENTITY_MANAGER.getTransaction().commit();
        System.out.println("KREIRAN ALARM U BAZI");
        Timer timer=new Timer();
        long per=Long.parseLong(period)*24*60*60*1000;
        TimerTask task=new TimerTask(){
            @Override
            public void run() {
                TextMessage msg=c.createTextMessage();
                if (per==0){
                ENTITY_MANAGER.getTransaction().begin();
                noviAlarm.setOnoff(0);
                ENTITY_MANAGER.getTransaction().commit();
                }
                try {
                    Query query=ENTITY_MANAGER.createQuery("SELECT i FROM AlarmEntity i WHERE i.id=:p");
                    AlarmEntity a=(AlarmEntity) query.setParameter("p", idAlarma).getSingleResult();
                    String s="PUSTI_PESMU-"+5+"-0-"+a.getNazivZvona();
                    msg.setText(s);
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                producer.send(pustacQueue, msg);
            }
        
        };
        if(per>0){
            timer.schedule(task, vreme, per);
        } 
        else{
            try{
            timer.schedule(task, vreme);
            }catch(IllegalArgumentException i){System.out.println("Ne mozete naviti alarm za nesto sto je proslo");}
        }
    }
    
    private static void promeniZvono(String[] komande) {
        Query query= ENTITY_MANAGER.createQuery("SELECT a FROM AlarmEntity AS a WHERE a.id=:p");
        AlarmEntity alarm=(AlarmEntity) query.setParameter("p", Integer.parseInt(komande[1])).getSingleResult();   
        ENTITY_MANAGER.getTransaction().begin();
        alarm.setNazivZvona(komande[2]);
        ENTITY_MANAGER.getTransaction().commit();
    }
    
     private static void promeniZvonoPoNazivu(String[] komande) {
        Query query= ENTITY_MANAGER.createQuery("SELECT a FROM AlarmEntity AS a WHERE a.nazivAlarma=:p");
        AlarmEntity alarm=(AlarmEntity) query.setParameter("p", komande[1]).getSingleResult();
        ENTITY_MANAGER.getTransaction().begin();
        alarm.setNazivZvona(komande[2]);
        ENTITY_MANAGER.getTransaction().commit();
    }
    
     private static void navijPostojeciAlarm(String[] komande){
        String idOdabranog=komande[1];  
        Query query= ENTITY_MANAGER.createQuery("SELECT a FROM AlarmEntity AS a WHERE a.id=:p");
        AlarmEntity alarm=(AlarmEntity) query.setParameter("p", Integer.parseInt(idOdabranog)).getSingleResult();
        ENTITY_MANAGER.getTransaction().begin();
        alarm.setOnoff(1);
        alarm.setPeriod(0);
        alarm.setVreme(addDays(alarm.getVreme(),1));    //UKLJUCUJE ALARM ZA SUTRASNJI DAN VREMENA KOJE JE PONUDJENO.
        ENTITY_MANAGER.getTransaction().commit();
    }
     
     private static void ponudiAlarme(String komande[]) {
        int idKorisnika=Integer.parseInt(komande[1]);
        Query query= ENTITY_MANAGER.createQuery("SELECT a FROM AlarmEntity AS a WHERE a.onoff=0");
        List<AlarmEntity> alarmiKojiSeNude= query.getResultList();
        String s="";
        for (AlarmEntity iter:alarmiKojiSeNude){
            s+="id:"+iter.getId()+" vreme: "+iter.getVreme().getHours()+":"+iter.getVreme().getMinutes()+":"+iter.getVreme().getSeconds()+"\n";
        
        }
        TextMessage textMessage = c.createTextMessage();
       try {
           textMessage.setIntProperty("Id",idKorisnika);
       } catch (JMSException ex) {
           Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
       }
        try {       
                    textMessage.setText(s);     
                    producer.send(answersQueue, textMessage);
                } catch (JMSException ex) {  }
    }
    
    
    /*private static void proveriAktivacijeAlarma(){
        System.out.println("Usao u proveru aktivacije alarma");
        Query query= ENTITY_MANAGER.createQuery("SELECT a FROM AlarmEntity AS a WHERE a.onoff=1");
        List<AlarmEntity> alarmiKojiSeAktiviraju= query.getResultList();
        for (AlarmEntity iter:alarmiKojiSeAktiviraju){
            Date trenutnoVreme=new Date();
            long trenutnoVremeMillis=trenutnoVreme.getTime();
            if (trenutnoVremeMillis<=iter.getVreme().getTime() && trenutnoVremeMillis+100>iter.getVreme().getTime()){
            azurirajAlarm(iter);
            posaljiPoruku(iter);
            } 
        }
    }*/
    
   /* private static void azurirajAlarm(AlarmEntity alarm){
        
        if (alarm.getPeriod()>0){
        ENTITY_MANAGER.getTransaction().begin();
        alarm.setVreme(addDays(alarm.getVreme(),alarm.getPeriod()));
        ENTITY_MANAGER.getTransaction().commit();
        }
        else {
        System.out.println("Usao u gasenje alarma");
        ENTITY_MANAGER.getTransaction().begin();
        alarm.setOnoff(0);
        ENTITY_MANAGER.getTransaction().commit();
        }
    }*/
    
   /*private static void posaljiPoruku(AlarmEntity iter) {
       
        TextMessage textMessage = c.createTextMessage();
        try {       
            String s="PUSTI_PESMU"+"-"+iter.getId()+"-0-"+iter.getNazivZvona();
                    textMessage.setText(s);     
                    producer.send(pustacQueue, textMessage);
                    System.out.println("Poslao alarm poruku za zvonjavu");
                } catch (JMSException ex) {  }
    }*/
    
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    private static void izmeniAlarm(String[] komande) {
        Integer id = (Integer)ENTITY_MANAGER.createQuery("SELECT MAX(i.id) FROM AlarmEntity AS i").getSingleResult();
        if (id==null) id=1;
        else id+=1;
        String nazivAlarma=komande[1];
        String nazivZvona=komande[2];
        Date vreme=new Date();
        vreme.setDate(Integer.parseInt(komande[3]));
        vreme.setMonth(Integer.parseInt(komande[4])-1);
        vreme.setYear(Integer.parseInt(komande[5])-1900);
        vreme.setHours(Integer.parseInt(komande[6]));
        vreme.setMinutes(Integer.parseInt(komande[7]));
        vreme.setSeconds(Integer.parseInt(komande[8]));
        String period=komande[9];
        String nazivStareObaveze=komande[10];
        final int idAlarma=id;
        AlarmEntity noviAlarm=new AlarmEntity(id,nazivAlarma,nazivZvona,vreme,Integer.parseInt(period),1);
        ENTITY_MANAGER.getTransaction().begin();
        ENTITY_MANAGER.persist(noviAlarm);
        ENTITY_MANAGER.getTransaction().commit();
        System.out.println("KREIRAN ALARM U BAZI");
        Timer timer=new Timer();
        long per=Long.parseLong(period)*24*60*60*1000;
        TimerTask task=new TimerTask(){
            @Override
            public void run() {
                TextMessage msg=c.createTextMessage();
                if (per==0){
                ENTITY_MANAGER.getTransaction().begin();
                noviAlarm.setOnoff(0);
                ENTITY_MANAGER.getTransaction().commit();
                }
                try {
                    Query query=ENTITY_MANAGER.createQuery("SELECT i FROM AlarmEntity i WHERE i.id=:p");
                    AlarmEntity a=(AlarmEntity) query.setParameter("p", idAlarma).getSingleResult();
                    String s="PUSTI_PESMU-"+5+"-0-"+a.getNazivZvona();
                    msg.setText(s);
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                producer.send(pustacQueue, msg);
            }
        
        };
        if(per>0){
            timer.schedule(task, vreme, per);
        } 
        else{
            try{
            timer.schedule(task, vreme);
            }catch(IllegalArgumentException i){System.out.println("Ne mozete naviti alarm za nesto sto je proslo");}
        }
        
        
        
    }
    
   
}












    
    
 
    
   
    
    



