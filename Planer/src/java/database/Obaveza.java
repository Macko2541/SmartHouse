/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author MARKO
 */

@Entity
public class Obaveza implements Serializable{
    @Id
    @Column(name = "id",nullable = false)
    private Integer id;
    
    @Column(name = "nazivObaveze", nullable = false)
    private String nazivObaveze;
    
     @Column(name = "nazivMestaObaveza", nullable = false)
    private String nazivMestaObaveze;
    
    @Column(name = "vremeObaveze",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vremeObaveze;
    
    @Column(name = "vremePolaska",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vremePolaska;
    
    @Column(name = "daLiJePodsetnik", nullable = false)
    private int daLiJePodsetnik;

    public Obaveza(Integer id, String nazivObaveze, String nazivMestaObaveze, Date vremeObaveze, Date vremePolaska, int daLiJePodsetnik) {
        this.id = id;
        this.nazivObaveze = nazivObaveze;
        this.nazivMestaObaveze = nazivMestaObaveze;
        this.vremeObaveze = vremeObaveze;
        this.vremePolaska = vremePolaska;
        this.daLiJePodsetnik = daLiJePodsetnik;
    }

    public Obaveza() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNazivObaveze() {
        return nazivObaveze;
    }

    public void setNazivObaveze(String nazivObaveze) {
        this.nazivObaveze = nazivObaveze;
    }

    public String getNazivMestaObaveze() {
        return nazivMestaObaveze;
    }

    public void setNazivMestaObaveze(String nazivMestaObaveze) {
        this.nazivMestaObaveze = nazivMestaObaveze;
    }

    public Date getVremeObaveze() {
        return vremeObaveze;
    }

    public void setVremeObaveze(Date vremeObaveze) {
        this.vremeObaveze = vremeObaveze;
    }

    public Date getVremePolaska() {
        return vremePolaska;
    }

    public void setVremePolaska(Date vremePolaska) {
        this.vremePolaska = vremePolaska;
    }

    public int getDaLiJePodsetnik() {
        return daLiJePodsetnik;
    }

    public void setDaLiJePodsetnik(int daLiJePodsetnik) {
        this.daLiJePodsetnik = daLiJePodsetnik;
    }
   
    
}
