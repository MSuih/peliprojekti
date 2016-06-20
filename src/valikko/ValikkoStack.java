package valikko;

import java.util.ArrayDeque;
import java.util.Deque;
import valikko.ValikkoToiminto.Toiminto;

/** Valikkojärjestelmä, jonka sisällä yksittäiset valikon sivut ovat. */
public class ValikkoStack {
    private final Deque<Valikko> stack = new ArrayDeque<>();
    private final boolean voikoPalataPeliin;
    private boolean takaisinValittu = false;
    
    /** Luo uusi valikkojärjestelmä. Valikko aloittaa päävalikosta.
     * @param onkoPeliAuki Onko peli auki eli voiko siihen palata. */
    public ValikkoStack(boolean onkoPeliAuki) {
        voikoPalataPeliin = onkoPeliAuki;
        stack.push(new PaaValikko());
    }
    /** Onko painike valittu
     * @param i painike, jonka tila tarkistetaan.
     * @return true, jos painike on valittu. */
    public boolean onkoValittu(int i) {
        return !takaisinValittu && stack.getFirst().getValittu() == i;
    }
    /** Palauttaa nykyisen valitun painikkeen.
     * @return Valittu painike tai -1 jos yksikään painikkeista ei ole valittu. */
    public int getValittuPainike() {
        if (takaisinValittu) return -1;
        return stack.getFirst().getValittu();
    }
    /** Asettaa valikon painikkeen valituksi.
     * @param i Painike, joka asetetaan valituksi.*/
    public void setValittuPainike(int i) {
        stack.getFirst().setValittu(i);
    }
    /** Liikutetaan valittua painiketta.
     * @param i Suunta, johon valintaa liikutetaan. Negatiivinen luku tarkoittaa ylöspäin ja positiivinen alaspäin. */
    public void liikutaValintaa(int i) {
        stack.getFirst().liiku(i);
    }
    /** Asettaa takaisin-painikkeen valinnan tilan.
     * @param b Onko takaisin valittu. */
    public void setTakaisinValittu(boolean b) {
        takaisinValittu = b;
    }
    /** Palauttaa takaisin-painikkeen valinnan tilan.
     * @return true jos takaisin on valittu.*/
    public boolean getTakaisinValittu() {
        return takaisinValittu;
    }
    
    /** Palauttaa näkyvissä olevien painikkeiden lukumäärän.
     * @return Painikkeiden määrä. */
    public int getPainikeLkm() {
        return stack.getFirst().getPainikeLkm();
    }
    /** Palauttaa yksittäisen painikkeen tekstin.
     * @param i Painike, jonka teksti halutaan.
     * @return Painikkeen teksti.
     */
    public String getPainikeTeksti(int i) {
        return stack.getFirst().getTeksti(i);
    }
    
    /** Voiko valikossa palata takaisin ylempään valikkoon.
     * @return true jos voi. */
    public boolean voikoPalataTaakse() {
        return stack.size() > 1;
    }
    /** Palaa valikossa takaisinpäin.
     */
    public void palaaTakaisin() {
        stack.removeFirst();
    }
    /** Voiko tästä valikosta palata takaisin peliin.
     * @return true jos peliin voi palata.*/
    public boolean voikoPalataPeliin() {
        return voikoPalataPeliin;
    }
    
    /** Painaa valikon painiketta.
     * @param i Painike, jota painetaan.
     * @return Toiminto, joka painamisesta aiheutui. */
    public ValikkoToiminto painaPainiketta(int i) {
        if (takaisinValittu) {
            if (voikoPalataTaakse()) stack.removeFirst();
            else if (voikoPalataPeliin()) return new ValikkoToiminto(ValikkoToiminto.Toiminto.takaisin);
            return null;
        }
        if (i >= stack.getFirst().getPainikeLkm() || i < 0) return null;
        ValikkoToiminto v = stack.getFirst().paina(i);
        if (v.toiminto == Toiminto.takaisin) {
            palaaTakaisin();
            return null;
        }
        else if (v.toiminto == Toiminto.v_aloita) {
            stack.push(new KenttaValikko());
            return null;
        }
        else if (v.toiminto == Toiminto.v_asetukset) {
            stack.push(new AsetusValikko());
            return null;
        }
        
        //Jos avataan uusi valikko, lisää se stackkiin ja palauta null
        return v;
    }
    /** Painaa tällä hetkellä valittuna olevaa painiketta.
     * @return Toiminto, joka painamisesta aiheutui. */
    public ValikkoToiminto painaValittuaPainiketta() {
        return painaPainiketta(stack.getFirst().getValittu());
    }
}