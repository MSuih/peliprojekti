package kayttoliittyma;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/** Luokka, joka muuttaa annetut näppäinpainallukset toiminnoiksi. */
public class NappainKasittelija {
    /** Toiminto, joka on yhdistetty tähän näppäimeen */
    public enum Toiminto {
        /** Tällä näppäimellä ei ollut toimintoa. */
        ei_mitaan,
        /** Liiku vasemmalle. Oletus: A tai Vasen nuolinäppäin. */
        vasen,
        /** Liiku oikealle. Oletus: D tai Oikea nuolinäppäin.*/
        oikea,
        /** Liiku ylös. Oletus: W tai Ylös-nuolinäppäin.*/
        ylos,
        /** Liiku alas. Oleus: S tai Alas-nuolinäppäin.*/
        alas,
        /** Käytä asetta tai avaa. Oletus: Välilyönti tai Enter.*/
        kayta,
        /** Avaa valikko. Oletus: ESC*/
        valikko,
        /** Lataa ase. Oletus: R */
        lataa,
        /** DEBUG: Jäädytä viholliset. */
        debug_jaadyta;
    }
    private final Map<Integer, Toiminto> nappainkartta = new HashMap<>();
    
    /** Luo uuden näppäinkäsittelijän oletusnäppäimillä. */
    public NappainKasittelija() {
        //valikko
        nappainkartta.put(KeyEvent.VK_ESCAPE, Toiminto.valikko);
        
        //liike
        nappainkartta.put(KeyEvent.VK_W, Toiminto.ylos);
        nappainkartta.put(KeyEvent.VK_UP, Toiminto.ylos);
        nappainkartta.put(KeyEvent.VK_S, Toiminto.alas);
        nappainkartta.put(KeyEvent.VK_DOWN, Toiminto.alas);
        nappainkartta.put(KeyEvent.VK_A, Toiminto.vasen);
        nappainkartta.put(KeyEvent.VK_LEFT, Toiminto.vasen);
        nappainkartta.put(KeyEvent.VK_D, Toiminto.oikea);
        nappainkartta.put(KeyEvent.VK_RIGHT, Toiminto.oikea);
        nappainkartta.put(KeyEvent.VK_O, Toiminto.debug_jaadyta);
        
        //toiminta
        nappainkartta.put(KeyEvent.VK_R, Toiminto.lataa);
        nappainkartta.put(KeyEvent.VK_SPACE, Toiminto.kayta);
        nappainkartta.put(KeyEvent.VK_ENTER, Toiminto.kayta);
    }
    /** Käsittelee annetun näppäinpainalluksen. Palauttaa sen toiminnon, joka on yhdistetty annettuun näppäimeen. Jos annetulla näppäimellä ei ollut toimintoa, palauttaa ei_mitään -toiminnon. 
     * @param i KeyEvent.getKeyCode() -metodilla saatava näppäinkoodi.
     * @return Toiminto, joka tapahtuu kun tätä näppäintä painetaan. */
    public Toiminto kasittele(int i) {
        return nappainkartta.getOrDefault(i, NappainKasittelija.Toiminto.ei_mitaan);
    }
    /** Lisää näppäimelle halutun toiminnon.
     * @param i KeyEvent.getKeyCode() -metodilla saatava näppäinkoodi.
     * @param t Toiminto, joka tehdään kun n */
    public void lisaaNappain(int i, Toiminto t) {
        if (t == Toiminto.ei_mitaan) return;
        nappainkartta.put(i, t);
    }
    /** Poistaa annettuun näppäimeen yhdistetyn toiminnon.
     * @param i KeyEvent.getKeyCode() -metodilla saatava näppäinkoodi.*/
    public void poistaNappain(int i) {
        nappainkartta.remove(i);
    }
}