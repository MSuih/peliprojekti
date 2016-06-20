package valikko;

/** Valikkojärjestelmän yksittäinen valikkonäkymä. Valikoilla voi olla monta sivua, joten tämän luokan muuttuja i viittaa aina avoinna olevaan valikon sivuun ja siinä näkyviin painikkeisiin. */
public interface Valikko {
    /** Palauttaa painikkeiden lukumäärän.
     * @return Näkyvissä olevien painikkeiden lukumäärä. */
    public int getPainikeLkm();
    /** Valikon yksittäisen painikkeen teksti.
     * @param i Painike, jonka teksti halutaan.
     * @return Painikkeen teksti. */
    public String getTeksti(int i);
    /** Asettaa painikkeen valituksi.
     * @param i Painike, joka halutaan valituksi.*/
    public void setValittu(int i);
    /** Liiku valikossa ylös tai alas.
     * @param i Suunta, johon liikutaan. Negatiivinen luku tarkoittaa ylöspäin ja positiivinen alaspäin.*/
    public void liiku(int i);
    /** Palauttaa painikkeen, joka on valitt
     * @return Valitun painikkeen numero. */
    public int getValittu();
    
    /** Paina valikon painiketta.
     * @param i Painike, jota painettiin.
     * @return Toiminto, joka tapahtui kun tätä painettiin */
    public ValikkoToiminto paina(int i);
}
