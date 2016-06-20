package kayttoliittyma;

/** Pelin asetukset. */
public class Asetukset {
    private Asetukset() {} //ei konstruktoria
    
    /** Kuinka suuri "normaalin" kokoinen ikkuna on. Muiden ikkunoiden koko skaalataan tämän mukaan. */
    public static final double ikkunan_oletuskoko = 1370d;
    /** Kuinka suuri "normaalin" kokoinen editori-ikkuna on. Muiden ikkunoiden koko skaalataan tämän mukaan. */
    public static final double ikkunan_oletuskoko_editori = 1500d;
    
    /** Kuinka monen yksikön päässä olevia tavaroita/vihollisia näytetään. */
    public static final double naytettava_alue = 17.5;
    
    /** Yksittäisen ruudun koko.  */
    public static final double ruudun_koko = 400;
    /** Yhden kenttäyksikön koko. */
    public static final double ruudun_osa = ruudun_koko / 10;
    
    /** Piirretäänkö ruudun ja pelin päivitysnopeudet. */
    public static boolean piirra_paivitysnopeus = false;
    /** Piirretäänkö pelaajan sijainti. */
    public static boolean piirra_sijainti = false;
    /** Piirretäänkö lyöntialueen testikuvio. */
    public static boolean piirra_lyontitesti = false;
    /** Kuinka monta millisekuntia odotetaan päivityskertojen välillä. */
    public static int paivitysnopeuden_rajoitin = 3;
    /** Rajoitetaanko päivitysnopeutta. Jos ei, ruutua ja peliä päivitetään niin nopeasti kun pystytään. Liian nopea päivittäminen saattaa aiheuttaa koneen ylikuumentumisen. */
    public static boolean rajoita_paivitysnopeutta = true;
    
    // Onko pelaajalla loputtomasti ammuksia. */
    //public static boolean loputtomasti_ammuksia = false;
    /** Viholliset eivät liiku, käänny tai hyökkää. Huomaavat kyllä pelaajan yhä. */
    public static boolean jaadyta_kaikki_viholliset = false;
}