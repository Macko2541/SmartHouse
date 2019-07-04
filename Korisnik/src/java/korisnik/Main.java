/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnik;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.spi.Context;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 *
 * @author MARKO
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory connectionFactory;
    @Resource(lookup = "RedZaOperacijeAlarma")
    static javax.jms.Queue redZaOperacijeAlarma;
    @Resource(lookup = "RedZaOdgovoreAlarma")
    static javax.jms.Queue redZaOdgovoreAlarma;
    @Resource(lookup = "RedZaPustacPesme")
    static javax.jms.Queue redZaPustacPesme;
    @Resource(lookup = "RedZaOdgovorePustaca")
    static javax.jms.Queue redZaOdgovorePustaca;
    @Resource(lookup = "RedZaOperacijePlanera")
    static javax.jms.Queue redZaOperacijePlanera;
    @Resource(lookup = "RedZaOdgovorePlanera")
    static javax.jms.Queue redZaOdgovorePlanera;
    
    static JMSContext context;
    static JMSProducer producer;
    static JMSConsumer consumer1;
    static JMSConsumer consumer2;
    static JMSConsumer consumer3;
    static int idKorisnika;
    static String imeKorisnika;
    static String trenutnaLokacija="Zemun";
    
    public static void main(String[] args) {   //ZA KREACIJU KORISNIKA POTREBAN JE ID KORISNIKA I IME KORISNIKA
        idKorisnika=Integer.parseInt(args[0]);
        imeKorisnika=args[1];
        context=connectionFactory.createContext();
        consumer1 = context.createConsumer(redZaOdgovorePustaca, "Id="+idKorisnika);
        consumer2=context.createConsumer(redZaOdgovoreAlarma,"Id="+idKorisnika);
        consumer3=context.createConsumer(redZaOdgovorePlanera,"Id="+idKorisnika);
        producer = context.createProducer();
        // ISPRAZNI REDOVE
        /*for (int i=0;i<100;i++)
            consumer2.receive();*/
        /*for (int i=0;i<100;i++)
            consumer3.receive();*/
        /*for (int i=0;i<100;i++)
            consumer1.receive();*/
        
//        JMSConsumer consumer4=context.createConsumer(redZaOperacijeAlarma,"Id="+idKorisnika);
//        JMSConsumer consumer5=context.createConsumer(redZaOperacijeAlarma);
//        JMSConsumer consumer6=context.createConsumer(redZaOperacijePlanera,"Id="+idKorisnika);
//        JMSConsumer consumer7=context.createConsumer(redZaOperacijePlanera);
//        JMSConsumer consumer8=context.createConsumer(redZaPustacPesme,"Id="+idKorisnika);
//        JMSConsumer consumer9=context.createConsumer(redZaPustacPesme);
//        
//        for (int i=0;i<100;i++){
//            consumer5.receive();
//        }
        boolean stani=false;
        Scanner sc=new Scanner(System.in);
        while (true){
        if (stani==true) break;
        System.out.println("[1]--PUSTI PESMU NA YOUTUBU");
        System.out.println("[2]--DOHVATI ISTORIJU");
        System.out.println("[3]--NAVIJ ALARM");
        System.out.println("[4]--NAVIJ PONUDJENI ALARM");
        System.out.println("[5]--PROMENI ZVUK ZVONA ALARMA");
        System.out.println("[6]--IZRACUNAJ RAZDALJINU DVA MESTA");
        System.out.println("[7]--IZRACUNAJ RAZDALJINU OD DRUGOG MESTA");
        System.out.println("[8]--DODAJ OBAVEZU");
        System.out.println("[9]--IZLISTAJ OBAVEZE");
        System.out.println("[10]--POSTAVI TRENUTNU LOKACIJU");
        System.out.println("[11]--OBRISI OBAVEZU");
        System.out.println("[12]--IZMENI OBAVEZU");
        System.out.println("[0]--UGASI APLIKACIJU");
        int opcija=sc.nextInt();
            switch(opcija){
                case 1:
                    pustiPesmuNaYouTubu();
                    break;
                case 2:
                    dohvatiIstorju();
                    break;
                case 3:
                    navijAlarm();
                    break;
                case 4:
                    navijPonudjeniAlarm();
                    break;
                case 5:
                    promeniZvukZvonaAlarma();
                    break;
                case 6:
                    razdaljinaOdAdoB();
                    break;
                case 7:
                    razdaljinaOdTrenutneLokacije();
                    break;
                case 8:
                    dodajObavezu();
                    break;
                case 9:
                    izlistajObaveze();
                    break;
                case 10:
                    postaviTrenutnuLokaciju();
                    break;
                case 11:
                    obrisiObavezu();
                    break;
                case 12:
                    izmeniObavezu();
                    break;
                case 0:
                    stani=true;
                    break;
                default:
                    stani=true;
                    break; 
            }    
        
        }
    }

    private static void pustiPesmuNaYouTubu() {
        Scanner skener = new Scanner(System.in);
        String tekstPoruke;
        System.out.println("Unesite pesmu koju zelite da pretrazite: ");
        tekstPoruke = skener.nextLine();
        TextMessage textMessage = context.createTextMessage();
        try {       
                    textMessage.setText("PUSTI_PESMU"+"-"+idKorisnika+"-"+imeKorisnika+"-"+tekstPoruke);     
                    producer.send(redZaPustacPesme, textMessage);
                } catch (JMSException ex) {  }
    }

    private static void dohvatiIstorju() {
        TextMessage textMessage = context.createTextMessage();
        try {       
                    textMessage.setText("POSALJI_ISTORIJU"+"-"+idKorisnika);     
                    producer.send(redZaPustacPesme, textMessage);
                } catch (JMSException ex) {  }
        System.out.println("CEKAAA");
        TextMessage istorija=(TextMessage) consumer1.receive();
        try {
            System.out.println(istorija.getText());
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void navijAlarm() {
        Scanner skener = new Scanner(System.in);
        
        System.out.println("Unesite naziv alarma");
        String nazivAlarma = skener.nextLine();
        System.out.println("Unesite pesmu zvona alarma");
        String nazivPesme = skener.nextLine();
        System.out.println("Unesite dan alarma");
        String dan = skener.nextLine();
        System.out.println("Unesite mesec alarma");
        String mesec = skener.nextLine();
        System.out.println("Unesite godinu alarma");
        String godina = skener.nextLine();
        System.out.println("Unesite sat alarma");
        String sat = skener.nextLine();
        System.out.println("Unesite minut alarma");
        String minut = skener.nextLine();
        System.out.println("Unesite sekund alarma");
        String sekund = skener.nextLine();
        System.out.println("Da li zelite da se alarm ponavlja? 1-DA,0-NE");
        String odluka = skener.nextLine();
        String period="0";
        if (odluka.equals("1")){
            System.out.println("Unesite broj dana za period");
            period = skener.nextLine();
        }
        TextMessage msg=context.createTextMessage();
            try {
                msg.setText("KREIRAJ_ALARM-"+nazivAlarma+"-"+nazivPesme+"-"+dan+"-"+mesec+"-"+godina+"-"+sat+"-"+minut+"-"+sekund+"-"+period);
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        producer.send(redZaOperacijeAlarma, msg);
    }

    

    private static void navijPonudjeniAlarm() {
        TextMessage msg1= context.createTextMessage();
        try {
            msg1.setText("PONUDI_ALARME"+"-"+idKorisnika);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
       producer.send(redZaOperacijeAlarma, msg1);
        System.out.println("POSTOJECI ALARMI:");
        TextMessage receive = (TextMessage) consumer2.receive();
        try {
            System.out.println(receive.getText());
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Unesite id alarma koji zelite ponovo ukljuciti");
       Scanner skener = new Scanner(System.in);
       String idAlarma = skener.nextLine();
       TextMessage msg2= context.createTextMessage();
        try {
            msg2.setText("NAVIJ_PONUDJENI"+"-"+idAlarma);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
       producer.send(redZaOperacijeAlarma, msg2);
       
    }

    private static void promeniZvukZvonaAlarma() {
        Scanner skener = new Scanner(System.in);
        System.out.println("Unesite id alarma kojeg zelite promeniti zvono");
        String idAlarma = skener.nextLine();
        System.out.println("Unesite novi naziv pesme");
        String noviNaziv = skener.nextLine();
        TextMessage textMessage = context.createTextMessage();
        try {       
                    textMessage.setText("PROMENI_ZVONO"+"-"+idAlarma+"-"+noviNaziv);     
                    producer.send(redZaOperacijeAlarma, textMessage);
                } catch (JMSException ex) {  }
    }

    private static void postaviTrenutnuLokaciju() {
       System.out.println("Unesite trenutnu lokaciju:");
       Scanner skener = new Scanner(System.in);
       String lokacija = skener.nextLine();
       trenutnaLokacija=lokacija;
    }

    private static void razdaljinaOdAdoB() {
       System.out.println("Unesite pocetnu lokaciju:");
       Scanner skener = new Scanner(System.in);
       String lokacijaA = skener.nextLine();
       System.out.println("Unesite destinaciju:");
       String lokacijaB = skener.nextLine();
       TextMessage textMessage = context.createTextMessage();
       try { 
                    textMessage.setText("A_DO_B"+"-"+lokacijaA+"-"+lokacijaB+"-"+idKorisnika);     
                    producer.send(redZaOperacijePlanera, textMessage);
                } catch (JMSException ex) {  }
        TextMessage vremePotrebno=(TextMessage) consumer3.receive();
        
        try {
            long vreme=Long.parseLong(vremePotrebno.getText());
            long vremeSati=vreme/3600;
            long vremeMinuti=(vreme%3600)/60;
            long vremeSekunde=(vreme%60);
            System.out.println("Vreme potrebno: "+vremeSati+":"+vremeMinuti+":"+vremeSekunde);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (NumberFormatException nf) {
            System.out.println("Previse je udaljeno");
        }
       
    }

    private static void razdaljinaOdTrenutneLokacije() {
       
       String lokacijaA = trenutnaLokacija;
       System.out.println("Unesite destinaciju:");
       Scanner skener = new Scanner(System.in);
       String lokacijaB = skener.nextLine();
       TextMessage textMessage = context.createTextMessage();
       try { 
                    textMessage.setText("DO_B"+"-"+lokacijaA+"-"+lokacijaB+"-"+idKorisnika);     
                    producer.send(redZaOperacijePlanera, textMessage);
                } catch (JMSException ex) {  }
        TextMessage vremePotrebno=(TextMessage) consumer3.receive();
        try {
            long vreme=Long.parseLong(vremePotrebno.getText());
            long vremeSati=vreme/3600;
            long vremeMinuti=(vreme%3600)/60;
            long vremeSekunde=(vreme%60);
            System.out.println("Vreme potrebno: "+vremeSati+":"+vremeMinuti+":"+vremeSekunde);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
    }

    private static void dodajObavezu() {
        Scanner skener = new Scanner(System.in);
        System.out.println("Unesite naziv obaveze");
        String nazivObaveze = skener.nextLine();
        System.out.println("Unesite naziv mesta obaveze");
        String nazivMestaObaveze = skener.nextLine();
        System.out.println("Unesite dan obaveze");
        String dan = skener.nextLine();
        System.out.println("Unesite mesec obaveze");
        String mesec = skener.nextLine();
        System.out.println("Unesite godinu obaveze");
        String godina = skener.nextLine();
        System.out.println("Unesite sat obaveze");
        String sat = skener.nextLine();
        System.out.println("Unesite minut obaveze");
        String minut = skener.nextLine();
        System.out.println("Unesite sekund obaveze");
        String sekund = skener.nextLine();
        System.out.println("Da li zelite da bude ima podsetnik za ovu obavezu? 1-DA, 0-NE");
        String podsetnik = skener.nextLine();
        String s="DODAJ_OBAVEZU"+"-"+nazivObaveze+"-"+nazivMestaObaveze+"-"+dan+"-"+mesec+"-"+godina+"-"+sat+"-"+minut+"-"+sekund+"-"+trenutnaLokacija+"-"+podsetnik;
        TextMessage textMessage = context.createTextMessage();
        try {
            textMessage.setText(s);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(redZaOperacijePlanera, textMessage);   
    }

    private static void izlistajObaveze() {
         TextMessage msg1= context.createTextMessage();
        try {
            msg1.setText("IZLISTAJ_OBAVEZE"+"-"+idKorisnika);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
       producer.send(redZaOperacijePlanera, msg1);
        System.out.println("OBAVEZE:");
        
       TextMessage receive = (TextMessage) consumer3.receive(); 
        try {
            System.out.print(receive.getText());
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void obrisiObavezu() {
        System.out.println("Unesite id obaveze koju zelite obrisati");
        Scanner skener = new Scanner(System.in);
        String idObaveze = skener.nextLine();
        TextMessage msg1= context.createTextMessage();
        try {
            msg1.setText("OBRISI_OBAVEZU"+"-"+idObaveze);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
       producer.send(redZaOperacijePlanera, msg1);
    }

    private static void izmeniObavezu() {
        Scanner skener = new Scanner(System.in);
        System.out.println("Unesite id obaveze koju zelite izmeniti");
        String idObaveze = skener.nextLine();
        System.out.println("Unesite naziv obaveze");
        String nazivObaveze = skener.nextLine();
        System.out.println("Unesite naziv mesta obaveze");
        String nazivMestaObaveze = skener.nextLine();
        System.out.println("Unesite dan obaveze");
        String dan = skener.nextLine();
        System.out.println("Unesite mesec obaveze");
        String mesec = skener.nextLine();
        System.out.println("Unesite godinu obaveze");
        String godina = skener.nextLine();
        System.out.println("Unesite sat obaveze");
        String sat = skener.nextLine();
        System.out.println("Unesite minut obaveze");
        String minut = skener.nextLine();
        System.out.println("Unesite sekund obaveze");
        String sekund = skener.nextLine();
        
        String s="IZMENI_OBAVEZU"+"-"+nazivObaveze+"-"+nazivMestaObaveze+"-"+dan+"-"+mesec+"-"+godina+"-"+sat+"-"+minut+"-"+sekund+"-"+trenutnaLokacija+"-"+idObaveze;
        TextMessage textMessage = context.createTextMessage();
        try {
            textMessage.setText(s);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(redZaOperacijePlanera, textMessage);   
    }
        
       

}
