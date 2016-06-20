package kayttoliittyma;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/** Tekstuurivarasto. Tämä yhdistää pelin luokkien käyttämät sprite-nimet Image-kuvatiedoistoihin. Jos tekstuuria ei löydy, konsoleen tulostetaan puuttuva tekstuurin nimi ja pelille palautetaan oletusteksuuri. Peli ei siis kaadu tekstuurin puutteeseen vaan pelaajalle näkyy (ruma) virhekuva. */
public class TekstuuriVarasto {
    //Yläkansio, jonka sisällä kaikki alakansiot on.
    private static final String ylakansio = "tekstuurit";
    //Jokaisen kuvatiedoston pääte.
    private static final String tiedostopaate = ".png";
    
    //kansio, jossa tekstuurit on
    private final File kansio;
    //kartta, johon ladatut tekstuurit laitetaan
    private final HashMap<String, Image> map = new HashMap<>();
    //virhekuva
    private final Image error;
    
    //Varasto, jossa tekstuurit on
    //tämä ja kaikki sen metodit on private-muodossa
    //varastoa käytetään vain staattisten metodien kautta
    private TekstuuriVarasto(String kansio) {
        this.kansio = new File(ylakansio, kansio);
        //Final-muuttujaa ei voi asettaa try-lauseessa
        //tästä syystä se tallennetaan ensin img-muuttujaan ja siirretään siitä error-muuttujaan
        @SuppressWarnings("UnusedAssignment")
        Image img = null;
        try {
            File tiedosto = new File(this.kansio, "virhe" + tiedostopaate);
            img = ImageIO.read(tiedosto);
            
        }
        catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Tekstuurikansiota ei löydy tai sen sisältö on virheellinen \n Java: " + ioe.getMessage() + "\n" + ioe.getLocalizedMessage(), "Virhe", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        error = img;
    }
    //Lataa uusi tekstuuri varastoon
    //Jottei tekstuuria tarvitsisi latailla kesken pelin
    private void lataa(String s) {
        try {
            File tiedosto = new File(kansio, s + tiedostopaate);
            BufferedImage img = ImageIO.read(tiedosto);
            /*map.put(s, i);*/
            //ImageIO:n palauttaa BufferedImagen, jota ei ole vielä ladattu näytönohjaimen muistiin
            //Jottei kuvaa tarvitsisi latailla kesken pelaamisen, tehdään volatileImage ja piirretään siihen meidän kuvamme
            VolatileImage v = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(img.getWidth(), img.getHeight());
            Graphics2D g2d = (Graphics2D) v.getGraphics();
            //Kuva on aluksi valkoinen. Tyhjennetään kuva
            g2d.setBackground(new Color(0, 0, 0, 0));
            g2d.clearRect(0, 0, img.getWidth(), img.getHeight());
            //Ja lopuksi piirretään juuri luotuun tyhjään kuvaan meidän BufferedImage
            g2d.drawImage(img, null, null);
            g2d.dispose();
            //nyt valmiiksi muistissa oleva kuva voidaan pistää talteen.
            map.put(s, v);
        }
        catch (IOException ioe) {
            System.out.println("Tekstuuria \"" + s + "\" ei löytynyt");
            map.put(s, error);
        }
    }
    //Hae tekstuuri varastosta 
    //jos sitä ei ole, se etsitään
    //Tämä on bufferedImage, eli tätä ei tallenneta näytönohjaimen muistiin
    private Image hae(String s) {
        if (map.containsKey(s)) return map.get(s);
        try {
            File tiedosto = new File(kansio, s + tiedostopaate);
            Image img = ImageIO.read(tiedosto);
            map.put(s, img);
            return img;
        }
        catch (IOException ioe) {
            System.out.println("Tekstuuria \"" + s + "\" ei löytynyt");
            map.put(s, error);
            return error;
        }
    }
    //Tyhjennä varasto.
    //muuten muistinkäyttö voi nousta liikaa
    private void tyhjenna() {
        map.clear();
        System.gc();
    }
    
    /* * * * * * * * * * * * * * *
     *       Vakiovarastot       *
     * * * * * * * * * * * * * * */
    
    //Varasto kentän tekstuureille
    private static final TekstuuriVarasto v_kentat = new TekstuuriVarasto("kentat");
    //Varasto yleisille tekstuureille
    private static final TekstuuriVarasto v_yleinen = new TekstuuriVarasto("yleinen");
    //Todo: Pelaajavarasto? Ase/ammusvarasto? Vihollisvarasto?
    
    /** Tekstuurivaraston tyyppi. */
    public enum Tyyppi {
        /** Kenttien käyttämä varasto. */
        kentat,
        /** Yleinen varasto. */
        yleinen;
    }
    
    /** Hakee tekstuurin varastosta. Jos tekstuuria ei ole ladattu valmiiksi, sen lataamisessa saattaa kestää hetki.
     * @param tyyppi Varasto, josta tekstuuri haetaan.
     * @param s Sprite, jota haetaan.
     * @return Kuva.
     */
    public static Image haeTekstuuri(Tyyppi tyyppi, String s) {
        switch (tyyppi) {
            case kentat:
                return v_kentat.hae(s);
            case yleinen:
                return v_yleinen.hae(s);
            default:
                throw new RuntimeException("Varastoa ei ole");
        }
    }

    /** Lataa tekstuurin varastoon valmiiksi, jotta sen hakeminen on nopeampaa. Tekstuuri ladataan myös näytönohjaimen tai vastaavan muistiin, jotta sen käyttö olisi nopeaa.
     * @param tyyppi Varasto, johon tekstuuri pistetään.
     * @param s Sprite, joka ladataan.
     */
    public static void lataaTekstuuri(Tyyppi tyyppi, String s) {
        switch (tyyppi) {
            case kentat:
                v_kentat.lataa(s);
                return;
            case yleinen:
                v_yleinen.lataa(s);
                return;
            default:
                throw new RuntimeException("Varastoa ei ole");
        }
    }
    /** Tyhjentää tekstuurivaraston. Hyödyllinen, jos pelissä tapahtuu jotain (esim. kentän vaihto), jonka takia suuri osa varastosta "vanhenee" eikä siinä olevia kuvia välttämättä enää tarvita. Tämä myös käskee javaa suorittamaan GC:n, joka saattaa aiheuttaa lyhyen lagipiikin.
     * @param tyyppi Varasto, joka tyhjennetään. */
    public static void tyhjennaTekstuurit(Tyyppi tyyppi) {
        switch (tyyppi) {
            case kentat:
                v_kentat.tyhjenna();
                return;
            case yleinen:
                v_yleinen.tyhjenna();
                return;
            default:
                throw new RuntimeException("Varastoa ei ole");
        }
    }
}