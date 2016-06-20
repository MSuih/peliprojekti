//EI KÄYTÖSSÄ
//Suunnitteena oli, että tämä olisi huoneen "tunniste" jotta kenttävälikkoon saataisiin kentän nimi ja jotain muuta tietoa.
//tämä toteutettiin muilla keinoilla.

/*package peli;

import java.io.Serializable;

public class KentanTiedot implements Serializable, Comparable<KentanTiedot>{
    private String nimi = "Uusi kenttä";
    private int jarjestysluku = -1;
    /* TODO:
     * Onko kentät final-muodossa vai muutettavissa?
     * Kwntän kuvaus?
     * Vaikeustaso?
     * Miten liitetään mahdollisiin chapter-luokkaan? Ei todennäköisesti täältä.
     *
    
    public KentanTiedot() {
        
    }
    public KentanTiedot(String nimi, int luku) {
        this.nimi = nimi;
        this.jarjestysluku = luku;
    }
    
    public String getNimi() {
        return nimi;
    }
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public int getJarjestysLuku() {
        return jarjestysluku;
    }
    public void setJarjestysLuku(int luku) {
        jarjestysluku = luku;
    }

    @Override
    public int compareTo(KentanTiedot o) {
        // Ideana se, että ensin listassa on numeroidut kentät numerojärjestyksessä. Sen jälkeen tulee miinusyksi-kentät nimijärjestyksessä. 
        //vertaa järjestyslukua ensin
        if (this.jarjestysluku != o.jarjestysluku) {
            //jos jompi kumpi on -1, järjestetään se viimeiseksi
            if (this.jarjestysluku < 0) return -1;
            if (o.jarjestysluku < 0) return 1;
            //muuten pienimmän numeron saanut on ensin
            return o.jarjestysluku - this.jarjestysluku;
        }
        //jos sama numero -> nimi päättää järjestyksen
        return this.nimi.compareTo(o.nimi);
    }
}*/
