/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author vm160089
 */
@Entity
public class Istorija {
    
@Id
@Column(name = "id",nullable = false)
private Integer id;

@Column(name = "nazivPesme", nullable = false)
private String nazivPesme;

@Column(name = "korisnik", nullable = false)
private int korisnik;

@Column(name = "nazivKorisnika", nullable = false)
private String nazivKorisnika;

    public Istorija() {
    }

    public Istorija(Integer id,String nazivPesme, int idKorisnika, String nazivKorisnika) {
        this.nazivPesme = nazivPesme;
        this.korisnik = idKorisnika;
        this.nazivKorisnika = nazivKorisnika;
        this.id=id;
    }

    public Integer getId() {
        return id;
    }
   

    public String getNazivPesme() {
        return nazivPesme;
    }

    public void setNazivPesme(String nazivPesme) {
        this.nazivPesme = nazivPesme;
    }

    public int getKorisnika() {
        return korisnik;
    }

    public void setIdKorisnika(int idKorisnika) {
        this.korisnik = idKorisnika;
    }

    public String getNazivKorisnika() {
        return nazivKorisnika;
    }

    public void setNazivKorisnika(String nazivKorisnika) {
        this.nazivKorisnika = nazivKorisnika;
    }
    


}
