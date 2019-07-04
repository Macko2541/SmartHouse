
package database;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



/**
 *
 * @author MARKO
 */


/*
Mozda treba da bude serializabilna razmisli kasnije
*/

@Entity
@Table(name= "Alarm")
public class AlarmEntity implements Serializable{
    
@Id
@Column(name = "id",nullable = false)
private Integer id;

@Column(name = "nazivAlarma", nullable = false)
private String nazivAlarma;

@Column(name = "nazivZvona", nullable = false)
private String nazivZvona;

@Column(name = "vremeAlarma",nullable = false)
@Temporal(TemporalType.TIMESTAMP)
private Date vreme;

@Column(name="periodZvonjave",nullable = false)
private int periodZvonjave;   // ako je 0 ili manje od nule alarm zvoni samo jednom period se odnosi na dane

@Column(name="onoff")
private int onoff;   // Da li je ukljucen ili iskljucen alarm

    public AlarmEntity() {
    }

    public AlarmEntity(Integer id,String nazivAlarma, String nazivZvona, Date vreme, int period, int onoff) {
        this.id=id;
        this.nazivAlarma = nazivAlarma;
        this.nazivZvona = nazivZvona;
        this.vreme = vreme;
        this.periodZvonjave = period;
        this.onoff=onoff; 
    }

    public int getOnoff() {
        return onoff;
    }

    public void setOnoff(int onoff) {
        this.onoff = onoff;
    }

    public Integer getId() {
        return id;
    }

    public String getNazivAlarma() {
        return nazivAlarma;
    }

    public String getNazivZvona() {
        return nazivZvona;
    }

    public Date getVreme() {
        return vreme;
    }

    public int getPeriodZvonjave() {
        return periodZvonjave;
    }

    public void setNazivAlarma(String nazivAlarma) {
        this.nazivAlarma = nazivAlarma;
    }

    public void setNazivZvona(String nazivZvona) {
        this.nazivZvona = nazivZvona;
    }

    public void setVreme(Date vreme) {
        this.vreme = vreme;
    }

    public void setPeriod(int periodZvonjave) {
        this.periodZvonjave = periodZvonjave;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        hash = 59 * hash + Objects.hashCode(this.nazivAlarma);
        hash = 59 * hash + Objects.hashCode(this.nazivZvona);
        hash = 59 * hash + Objects.hashCode(this.vreme);
        hash = 59 * hash + this.periodZvonjave;
        hash = 59 * hash + this.onoff;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AlarmEntity other = (AlarmEntity) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.periodZvonjave != other.periodZvonjave) {
            return false;
        }
        if (this.onoff != other.onoff) {
            return false;
        }
        if (!Objects.equals(this.nazivAlarma, other.nazivAlarma)) {
            return false;
        }
        if (!Objects.equals(this.nazivZvona, other.nazivZvona)) {
            return false;
        }
        if (!Objects.equals(this.vreme, other.vreme)) {
            return false;
        }
        return true;
    }


    
    

   

   

    

    
}
