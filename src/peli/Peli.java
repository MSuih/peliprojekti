package peli;

import aseet.Ammus;
import aseet.Ase;
import aseet.Haulikko;
import aseet.Lyonti;
import aseet.Rynnakkokivaari;
import java.awt.geom.Arc2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import kayttoliittyma.TekstuuriVarasto;
import logiikka.Koord;
import logiikka.KoordSuunnalla;
import objektit.Efekti;
import objektit.OdottavaVihollinen;
import objektit.Pelaaja;
import objektit.Piirrettava;
import objektit.Tavara;
import objektit.TavaraAse;
import objektit.Vihollinen;

/** Pelin tapahtumista vastaava luokka.
 * <br><br>
    Peli-luokan käyttämä [i]siirtymä[/i]-muuttuja kertoo kuinka paljon edellisestä päivityskerrasta on kulunut. Aikayksikkönä on sekunti; 1d = 1 sekunti.
    <br><br>
    Peli-luokan käyttämä suunta/kulma on aina radiaaneina.*/
public class Peli {
    private Pelaaja pelaaja = new Pelaaja(0.1, 0.1);
    private final Kentta kentta;
    private final List<OdottavaVihollinen> kentan_vihollisasettelu = new ArrayList<>();
    private final List<Vihollinen> viholliset = Collections.synchronizedList(new ArrayList<>());
    private final List<Efekti> kuolleetViholliset = Collections.synchronizedList(new ArrayList<>());
    private final List<Ammus> ammukset = Collections.synchronizedList(new ArrayList<>());
    private final List<Tavara> kentan_tavarat = new ArrayList<>();
    private final List<Tavara> tavarat = Collections.synchronizedList(new ArrayList<>());
    
    private double lopetusGrafiikka = 0;
    
    /** Aloittaa uuden pelin tyhjällä 5x5 kentällä. */
    public Peli() {
        this.kentta = new Kentta(5,5);
        pelaaja.setX(kentta.getAloitusX());
        pelaaja.setY(kentta.getAloitusY());
    }
    /** Aloittaa uuden pelin käyttäen pohjana kenttätiedostoa.
     * @param tiedostonimi Tiedosto, josta kenttä haetaan. Koko tiedoston nimi tiedostopäätteellä.
     * @throws java.util.zip.DataFormatException Jos kenttää ei ole, sitä ei voi lukea tai se on väärässä muodossa. */
    public Peli(String tiedostonimi) throws DataFormatException {
        Kentta k = null; // final-muuttujaa ei voi asettaa try-osassa
        try (ObjectInputStream stream = 
                new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File("kentat", tiedostonimi))))){
            //luetaan
            k = (Kentta) stream.readObject(); 
            kentan_vihollisasettelu.addAll((Collection<OdottavaVihollinen>) stream.readObject());
            kentan_tavarat.addAll((Collection<Tavara>) stream.readObject());
            
            //asetetaan
            pelaaja.setX(k.getAloitusX());
            pelaaja.setY(k.getAloitusY());
            kentan_vihollisasettelu.stream().forEach((v) -> {
                viholliset.add(OdottavaVihollinen.muutaViholliseksi(v));
            });
            tavarat.addAll(kentan_tavarat);
        }
        catch (IOException | ClassNotFoundException ex) {
            //Todo: Heitä virhe joka vaatii catch-osion kun tätä kutsutaan
            throw new DataFormatException("Kenttä ei kelpaa\n" + ex.getMessage());
        }
        kentta = k; //nyt asetetaan final-muuttuja
        //ladataan kentän tekstuurit yksi kerrallaan muistiin, jottei niiden latautumista tarvitse odottaa kesken pelin
        kentta.getLadattavat().stream().forEach((s) -> {
            TekstuuriVarasto.lataaTekstuuri(TekstuuriVarasto.Tyyppi.kentat, s);
        });
    }
    
    /** Palauttaa pelaajan sijainnin x-akselilla.
     * @return Pelaajan sijainti. */
    public double getPelaajaX() {
        return pelaaja.getX();
    } 
    /** Palauttaa pelaajan sijainnin y-akselilla.
     * @return Pelaajan sijainti. */
    public double getPelaajaY() {
        return pelaaja.getY();
    }
    /** Päivittää pelaajaa. 
     * @param siirtyma Kuinka paljon aikaa on kulunut edellisestä päivityksestä. */
    public void paivita(double siirtyma) {
        //päivitetään lopetuksen fadeout-efektiä
        if (!onkoVihollisiaJaljella() && this.onkoPelaajaLopetusAlueella()) {
            lopetusGrafiikka = Math.min(lopetusGrafiikka + siirtyma / 3, 1);
        }
        //päivitetään vihollisia
        viholliset.stream().forEach((v) -> {
            v.paivita(siirtyma);
        });
        //poistetaan kuolleet viholliset
        List<Vihollinen> poistettavatViholliset = new ArrayList<>();
        viholliset.stream().filter((v) -> (!v.onkoElossa())).forEach((v) -> {
            poistettavatViholliset.add(v);
            kuolleetViholliset.add(v.getKuollutVihollinen());
        });
        //tiputetaan aseet
        poistettavatViholliset.stream().forEach((v) -> {
            if (v.tiputtaakoAseen()) {
                //luodaan tavara vihollisen alle
                Tavara t = Tavara.luoTavara(v.getAse());
                t.setX(v.getX());
                t.setY(v.getY());
                //lisätään se kentälle
                lisaaTavara(t);
            }
            if (Math.random() < 0.2) {
                Tavara t = new Tavara(v.getX(), v.getY(), Tavara.TavaraTyyppi.elamat, (byte) 30);
                //lisätään se kentälle
                lisaaTavara(t);
                
            }
        });
        viholliset.removeAll(poistettavatViholliset);
        //loppuosaa ei suoriteta jos pelaaja on kuollut
        if (!pelaaja.onkoElossa()) {
            return;
        }
        //päivitetään pelaajaa
        pelaaja.paivita(siirtyma);
        //poimitaan lähellä olevat tavarat
        List<Tavara> poistettavatTavarat = new ArrayList<>();
        tavarat.stream().filter((t) -> (Math.abs(pelaaja.getX() - t.getX()) < 0.5 && Math.abs(pelaaja.getY() - t.getY())< 0.5)).forEach((t) -> {
            //Tavaroiden poimiminen automaattisesti.
            switch (t.getTyyppi()) {
                case elamat:
                    pelaaja.paranna(t.getArvo());
                    poistettavatTavarat.add(t);
                    break;
                //todo: tarvitaanko näitä lisää? 
            }
        });
        tavarat.removeAll(poistettavatTavarat);
        //päivitetään pelaaja oikeeseen huoneeseen
        if (!pelaaja.onkoSamassaHuoneessa()) {
            Huone h = kentta.getHuone(pelaaja.getX(), pelaaja.getY());
            if (h != null) {
                pelaaja.setHuone(h);
                //herätetään kaikki kyseisessä huoneessa olevat viholliset
                viholliset.stream().filter((v) -> (v.getHuone() == h)).forEach((v) -> {
                    v.setTekeminen(Vihollinen.Tekeminen.hyokkaa);
                });
            }
        }
    }
    /** Voiko pelaajaa liikuttaa. Tätä kannattaa kysyä ennen kun kutsuu liikutaPelaaja(int, int, double) metodia.
     * @return true jos pelaajaa voi liikuttaa. */
    public boolean liikutetaankoPelaajaa() {
        return pelaaja.onkoElossa();
    }
    /** Liikuttaa pelaajan hahmoa. Muuttujat suuntax ja suuntay kuvaavat suuntaa, syötä niihin -1 tai 1 jos haluat liikkua kyseisellä akselilla ja 0 jos et halua. Älä kutsu tätä metodia jos pelaajaa ei tarvitse siirtää; jos sekä x- että y-siirto on 0, pelaajaa liikutellaan silti.
     * @param suuntax Mihin suuntaan siirrytään x-akselilla. Numero väliltä -1 ja 1.
     * @param suuntay Mihin suuntaan siirrytään y-akselilla. Numero väliltä -1 ja 1.
     * @param siirtyma Kuinka kauan on kulunut edellisestä siirrosta.*/
    public void liikutaPelaajaa(int suuntax, int suuntay, double siirtyma) {
        //Tulevaisuutta varten: Intit voisi muuttaa doubleiksi, jolloin metodi voisi hallita myös analogisen ohjaimen liikkeen.
        //kuollutta pelaajaa ei liikuteta, eikä myöskään jos hän on lopetusalueella
        if (!pelaaja.onkoElossa() || (onkoPelaajaLopetusAlueella() && !onkoVihollisiaJaljella())) return;
        //pelaajan nykyinen sijainti
        final double pelaajax = pelaaja.getX();
        final double pelaajay = pelaaja.getY();
        //lasketaan suunta, mihin pelaaja liikkuu
        final double kulma = Math.atan2(suuntay, suuntax);
        //lasketaan, mihin pelaaja siirtyisi jos liike onnistuisi
        final double uusix = pelaajax + (pelaaja.getNopeus() * siirtyma) * Math.cos(kulma);
        final double uusiy = pelaajay + (pelaaja.getNopeus() * siirtyma) * Math.sin(kulma);
        //liikutetaan tähän uuteen sijaintiin
        tarkistaJaLiikuta(pelaaja, uusix, uusiy);
    }
    /** Käännä pelaajaa tietyn kulman suuntaan.
     * @param kulma Pelaajan katseen uusi kulma. */
    public void kaannaPelaajaa(double kulma) {
        if (!pelaaja.onkoElossa()) return;
        pelaaja.setSuunta(kulma);
    }
    /** Palauttaa pelaajan elämäprosentin.
     * @return Pelaajan elämäprosentti, väliltä 0 - 1. */
    public double getPelaajaElamat() {
        return pelaaja.getElamaProsentti();
    }
    /** Palauttaa, onko pelaajalla elämää jäljellä.
     * @return true jos pelaaja on yhä elossa.*/
    public boolean onkoPelaajaElossa() {
        return pelaaja.onkoElossa();
    }
    /** Palauttaa suunnan, johon pelaaja katsoo.
     * @return Kulma, johon pelaaja katsoo. */
    public double getPelaajaSuunta() {
        return pelaaja.getSuunta();
    }
    /** Palauttaa tekstuurin nimen, jota pelaaja käyttää.
     * @return  Sprite, jota pelaaja käyttää. */
    public String getPelaajaTekstuuri() {
        return pelaaja.getSprite();
    }
    /** Palauttaa aseen tekstuurin, jota pelaaja käyttää.
     * @return Pelaajan aseen tekstuuri. */
    public String getAseTekstuuri() {
        return pelaaja.getAseSprite();
    }
    
    /** Onko pelissä vihollisia jäljellä.
     * @return true, jos edes yksi vihollinen on jäljellä.*/
    public boolean onkoVihollisiaJaljella() {
        return !viholliset.isEmpty();
    }
    /** Onko pelaaja lopetusalueella.
     * @return true, jos pelaaja on lopetusalueella. */
    public boolean onkoPelaajaLopetusAlueella() {
        return kentta.getOnkoLopetusAlueella(pelaaja.getX(), pelaaja.getY());
    }
    
    /** Palauttaa lopetusalueen keskipisteen x-akselilla.
     * @return Lopetusalueen koordinaatti x-akselilla */
    public double getLopetusAlueX() {
        return kentta.getLopetusAlueX() + kentta.getLopetusAlueSivuX() / 2;
    }
    /** Palauttaa lopetusalueen keskipisteen y-akselilla.
     * @return Lopetusalueen koordinaatti y-akselilla */
    public double getLopetusAlueY() {
    /** Palauttaa lopetusalueen keskipisteen y-akselilla.
     * @return Lopetusalueen koordinaatti y-akselilla */
        return kentta.getLopetusAlueY() + kentta.getLopetusAlueSivuY() / 2;
    }
    /** Palauttaa prosentin, jonka mukaan ruutua pimennetään.
     * @return Prosentti, jossa 1 = täysi pimeys ja 0 = ei pimennystä. */
    public double getLopetusPimennys() {
        return lopetusGrafiikka;
    }
    /** Onko ruutu täysin pimennetty.
     * @return true, jos ruudun pimennys on valmis. */
    public boolean getOnkoPimennysValmis() {
        return lopetusGrafiikka > 0.99;
    }
    
    /** Palauttaa ympärillä olevat ruudut. Ei palauta joka kerralla saman verran ruutuja; jos pelaaja on kentän reunan lähellä tai osa ruuduista on tyhjinä, ei niiden kohdalla palauteta mitään.
     * @param alue Alue, jonka verran ympäristöä näytetään. Käytä aina parittomia lukuja.
     * @return Map, jossa ruudut sekä niiden sijainti.
     * @throws IllegalArgumentException alue on parillinen luku tai pienempi kun yksi.
     */
    public Map<Koord, String> getTaustaRuudut(int alue) {
        if (alue % 2 == 0 && alue < 1) throw new IllegalArgumentException("Käytä vain parittomia lukuja ruutuja hakiessa");
        final Map<Koord, String> map = new HashMap();
        final int pelaajanruutux = kentta.getRuutuX(pelaaja.getX());
        final int pelaajanruutuy = kentta.getRuutuY(pelaaja.getY());
        /*  Alue = se ruutualue, jonka tekstit näytetään. Esim. 5 = näytetään ympäriltä 5x5 alue.
            Pelaajanruutu = se ruutu, jossa pelaaja on tällä hetkellä.
            fx ja fy = muuttujat, jolla haetaan 5 ruutua
            siirtyma = Muutetaan alue sellaiseen muotoon että keskimmäinen ruutu on 0. 
                Siitä vasempaan tai ylös on -1, -2 jne ja alas/oikealle +1, +2 jne.
        */
        final int siirtyma = alue / 2;
        for (int fx = 0; fx < alue; fx++) {
            for (int fy = 0; fy < alue; fy++) {
                map.put(
                        new Koord.Int(fx - siirtyma, fy - siirtyma),
                        kentta.getRuutuKuva(
                                pelaajanruutux + (fx - siirtyma), 
                                pelaajanruutuy + (fy - siirtyma)));
            }
        }
        return map;
    }
    /** Kuinka kaukana pelaajan sijainti on kenttäruudun yläreunasta.
     * @return Etäisyys x-koordinaattiakselilla kenttäruudun yläreunasta */
    public double getPelaajaSiirtoX(){
        return pelaaja.getX() % 10;
    }
    /** Kuinka kaukana pelaajan sijainti on kenttäruudun yläreunasta.
     * @return Etäisyys y-koordinaattiakselilla kenttäruudun yläreunasta. */
    public double getPelaajaSiirtoY(){
        return pelaaja.getY() % 10;
    }
    /** Onko pelaajan käyttämä ase sarjatuliase. Jos on, niin ampumispainiketta ei tarvitse painaa jokaisen laukauksen yhteydessä.
     * @return true jos pelaajalla on sarjatuliase. */
    public boolean getOnkoSarjatuliase() {
        return pelaaja.getOnkoSarjatuliase();
    }
    /** Käyttää asetta, jos se on mahdollista. Ampuu tai lyö aseella, riippuen siitä mitä pelaajalla on käytössä. Jos aseen käyttäminen ei ole mahdollista, ei tee mitään. */
    public void kaytaAsetta() {
        //tarkistetaan, voiko vielä käyttää asetta
        if (!pelaaja.getVoikoKayttaaAsetta()) return;
        if (pelaaja.getOnkoAmpumaAse()) {
            ammukset.addAll(pelaaja.ammuAseella());
            //laukauksen ääni herättää kaikki, jopa viereisissä huoneissa olevat
            if (pelaaja.getHuone() != null) pelaaja.getHuone().getYhteydet().stream().forEach((h) -> {
                this.herataViholliset(h);
            });
        }
        else {
            Lyonti lyonti = pelaaja.lyoAseella();
            //Lasketaan lyönnille V:n muotoinen alue jonka sisällä olevia vihollisia vahingoitetaan
            final Arc2D alue = new Arc2D.Double(
                    //ensin "piirretään" neliö pelaajan ympärille
                    pelaaja.getX() - 2, pelaaja.getY() - 2,
                    4, 4,
                    //sitten lasketaan sen keskipisteestä piirakan sivu
                    Math.toDegrees(-getPelaajaSuunta()) - 50, 100,
                    Arc2D.PIE);
            viholliset.stream().filter((v) -> (alue.contains(v.getX(), v.getY()))).forEach((v) -> {
                v.vahingoita(lyonti.getVahinko());
            });
        }
    }
    /** Lataa pelaajan aseen tai aloittaa kentän alusta. Aloitetaan alusta vain, jos pelaaja on kuollut. */
    public void lataaAse() {
        if (pelaaja.onkoElossa()) {
            pelaaja.lataaAse();
        }
        else {
            aloitaAlusta();
        }
    }
    /** Heittää nykyisen aseen pois ja/tai poimii maassa olevan. Jos lähellä ei ole käyttökelpoista asetta, heitää vain nykyisen pois poimimatta mitään. Jos pelaajalla ei ole aikaisemmin poimittua asetta, oletusasetta ei heitetä pois. */
    public void poimiTaiHeitaAse() {
        Tavara lahin_tavara = null;
        //tarkistetaan, mikä ase olisi lähellä
        for (Tavara t : tavarat) {
            if (t.getTyyppi() != Tavara.TavaraTyyppi.ase) continue;
            if (Math.abs(t.getX() - pelaaja.getX()) < 1 && Math.abs(t.getY() - pelaaja.getY()) < 1) {
                lahin_tavara = t;
                break;
            }
        }
        //jos pelaajalla oli ase kädessä
        if (pelaaja.getOnkoPoimittuaAsetta()) {
            Ase ase = pelaaja.tiputaAse();
            if (lahin_tavara != null) {
                //lähellä oli tavara, joten laitetaan se käteen
                pelaaja.setAse(lahin_tavara.getAse());
                tavarat.remove(lahin_tavara);
            }
            ase.nollaaAnimaatio();
            //luodaan tavara pelaajan alle
            Tavara t = Tavara.luoTavara(ase);
            t.setX(pelaaja.getX());
            t.setY(pelaaja.getY());
            //lisätään se kentälle
            lisaaTavara(t);
        }
        //jos ei -> poimi silti
        else{
            if (lahin_tavara == null) {
                //ei poimittavaa asetta
                return;
            }
            pelaaja.setAse(lahin_tavara.getAse());
            tavarat.remove(lahin_tavara);
        }
    }
    /** Palauttaa pelaajan aseen ammusten määrän.
     * @return Jäljellä olevien ammusten määrä. */
    public int getAmmustenMaara() {
        return pelaaja.getAmmusMaara();
    }
    /** Palauttaa pelaajan aseessa olevien ammusten maksimimäärän.
     * @return Ammusten maksimimäärä.*/
    public int getAmmustenMaaraMax() {
        return pelaaja.getLippaanKoko();
    }
    /** Palauttaa pelaajan aseessa jäljellä olevien lippaiden määrän.
     * @return Lippaiden määrä*/
    public int getLippaidenMaara() {
        return pelaaja.getLipasMaara();
    }
    /** Pitääkö pelaajan ammusten määrä piirtää.
     * @return true jos pitää. */
    public boolean piirretaankoAmmusMaara() {
        return pelaaja.getOnkoAmpumaAse();
    }
    /** Onko pelaajan ammukset vähissä.
     * @return true jos näin on.*/
    public boolean onkoAmmuksetVahissa() {
        return pelaaja.onkoAmmuksetVahissa();
    }
    /** Palauttaa pelaajan lippaiden ikonin.
     * @return Lippaan ikoni, käyttöliittymää varten.*/
    public String getLipasIkoni() {
        return pelaaja.getLipasIkoni();
    }
    /** Tekee pyörähdysliikeen.
     * @param x Suunta x-akselilla.
     * @param y Suunta y-akselilla
     */
    public void pyorahda(int x, int y) {
        //tarkista onko edellinen kesken
        final double kulma = Math.atan2(y, x);
        //set pelaajan animaatio
        //jotain jotain
        //jotain
        throw new UnsupportedOperationException("TODO!");
    }
    /** Palauttaa kaikki kentällä olevat viholliset.
     * @param alue Vihollisten maksimietäisyys pelaajasta. Tämän ulkopuolella olevia vihollisia ei haeta.
     * @return Kartta, jossa vihollisten spritet sekä niiden sijainti ja suunta. */
    public Map<KoordSuunnalla, String> getViholliset(double alue) {
        Map<KoordSuunnalla, String> map = new LinkedHashMap<>();
        synchronized (viholliset) {
            viholliset.stream().filter((v) -> (Math.abs(v.getX() - pelaaja.getX()) < alue && Math.abs(v.getX() - pelaaja.getX()) < alue)).forEach((v) -> {
                map.put(new KoordSuunnalla(v.getX(), v.getY(), v.getSuunta()),
                        v.getSprite());
            });
        }
        return map;
    }
    /** Palauttaa vihollisten tavarat. Palauttaa vain niiden vihollisten kohdalla joilla on tavaroita.
     * @param alue Maksimietäisyys pelaajasta.
     * @return  Map, jossa koordinaatti, suunta ja spritet.*/
    public Map<KoordSuunnalla, String> getVihollistenTavarat(double alue) {
        Map<KoordSuunnalla, String> map = new LinkedHashMap<>();
        synchronized (viholliset) {
            viholliset.stream().filter((v) -> (v.onkoPiirrettavaaTavaraa() && Math.abs(v.getX() - pelaaja.getX()) < alue && Math.abs(v.getX() - pelaaja.getX()) < alue)).forEach((v) -> {
                map.put(new KoordSuunnalla(v.getX(), v.getY(), v.getSuunta()),
                        v.getTavaraSprite());
            });
        }
        return map;
    }
    /** Palauttaa kaikki kentällä olevat ruumiit eli kuolleet viholliset.
     * @param alue Maksimietäisyys pelaajasta, jonka ulkopuolisia vihollisia ei haeta.
     * @return Map, jossa kuolleiden vihollisten spritet sekä niiden sijainti ja suunta. */
    public Map<KoordSuunnalla, String> getKuolleetVihollset(double alue) {
        Map<KoordSuunnalla, String> map = new LinkedHashMap<>();
        synchronized (kuolleetViholliset) {
            kuolleetViholliset.stream().filter((v) -> (Math.abs(v.getX() - pelaaja.getX()) < alue && Math.abs(v.getX() - pelaaja.getX()) < alue)).forEach((v) -> {
                map.put(new KoordSuunnalla(v.getX(), v.getY(), v.getSuunta()),
                        v.getSprite());
            });
        }
        return map;
    }
    /** Liikuttaa kentällä olevia vihollisia. Viholliset tekee sitä, mitä niiden "tekoäly" käskee. Ne voivat jopa hyökätä pelaajan kimppuun. Tämä metodi myös automaattisesti "tappaa" viholliset eli poistaa ja lisää ruumiit kentälle mikäli ne ovat kuolleet.
     * @param siirtyma Kuinka kauan edellisestä siirrosta on kulunut.*/
    public void liikutaVihollisia(double siirtyma) {
        if (!pelaaja.onkoElossa()) return;
        for (Vihollinen v : viholliset) {
            //nykyinen sijainti
            final double sijaintix = v.getX();
            final double sijaintiy = v.getY();
            if (!v.onkoSamassaHuoneessa()) {
                Huone h = kentta.getHuone(sijaintix, sijaintiy);
                if (h != null) v.setHuone(h);
            }
            switch (v.getTekeminen()) {
                case harhaile:
                    //Varmista että vihollinen on jossain huoneessa
                    if (!v.onkoSamassaHuoneessa()) {
                        v.setHuone(kentta.getHuone(v.getX(), v.getY()));
                    }
                    //tarkisteaan, mihin seuraavaksi
                    Koord k = v.getKohdeSijainti();
                    if (k == null) continue;
                    //jos harhailun kohde on tarpeeksi lähellä, ei enää tarvitse liikkua
                    if (Math.abs(k.getXDouble() - sijaintix) < 0.5d && Math.abs(k.getYDouble() - sijaintiy) < 0.5d) continue;
                    //lasketaan kulma
                    final double kulma = Math.atan2(k.getYDouble() - sijaintiy, k.getXDouble() - sijaintix);
                    //lasketaan uusi kohde
                    double uusix = sijaintix + (v.getNopeus() * siirtyma) * Math.cos(kulma);
                    double uusiy = sijaintiy + (v.getNopeus() * siirtyma) * Math.sin(kulma);
                    //liikutetaan kohteen suuntaan
                    tarkistaJaLiikuta(v, uusix, uusiy);
                    v.setSuunta(kulma);
                    break;
                case hyokkaa:
                    Huone pelaajanHuone = pelaaja.getHuone();
                    if (pelaajanHuone == v.getHuone() || v.getHuone() == null || pelaajanHuone == null)  {
                        //Vihollinen joka ampuu aseella
                        if (v.kayttaakoAsetta()) {
                            //ampuva vihollinen katsoo aina pelaajaa päin
                            v.setSuunta(Math.atan2(pelaaja.getY() - v.getY(), pelaaja.getX() - v.getX()));
                            //liikutetaan vihollista
                            Koord kohde = v.getKohdeSijainti();
                            if (kohde == null || !v.getHuone().onkoSisalla(kohde.getXDouble(), kohde.getYDouble())) {
                                v.arvoKohdeSijainti();
                            }
                            kohde = v.getKohdeSijainti();
                            if (Math.abs(kohde.getXDouble() - sijaintix) > 0.5 
                                    && Math.abs(kohde.getYDouble() - sijaintiy) > 0.5) {
                                double suunta = Math.atan2(kohde.getYDouble() - sijaintiy, kohde.getXDouble() - sijaintix);
                                double kohdex = sijaintix + (v.getNopeus() * siirtyma) * Math.cos(suunta);
                                double kohdey = sijaintiy + (v.getNopeus() * siirtyma) * Math.sin(suunta);
                                tarkistaJaLiikuta(v, kohdex, kohdey);
                            }
                            //yritetään ampua aseella
                            List l = v.ammu();
                            if (l != null) ammukset.addAll(l);
                        }
                        //lähitaisteluvihollinen
                        else {
                            if (Math.abs(pelaaja.getX() - sijaintix) < 1 && Math.abs(pelaaja.getY() - sijaintiy) < 1) {
                                //Vihollinen on tarpeeksi lähellä, ei liikuta vaan hyökätään
                                pelaaja.vahingoita(v.hyokkaa());
                            }
                            else {
                                double suunta = Math.atan2(pelaaja.getY() - sijaintiy, pelaaja.getX() - sijaintix);
                                double kohdex = sijaintix + (v.getNopeus() * siirtyma) * Math.cos(suunta);
                                double kohdey = sijaintiy + (v.getNopeus() * siirtyma) * Math.sin(suunta);

                                tarkistaJaLiikuta(v, kohdex, kohdey);
                                v.setSuunta(suunta);
                            }
                        }
                    }
                    else {
                        //Yritetään löytää reitti pelaajan huoneeseen. 
                        Huone kohde = null; //nykyisen huoneen vieressö olevista huoneista paras
                        int paras_reitti = 99999; //kuinka monen huoneen kautta paras yhteys kulkee
                        int laskuri; //kuinka monta hyppyä tämänhetkinen on joutunut tekemään
                        /* Käydään läpi ne huoneet joihin tästä huoneesta voi liikkua. 
                         * Esim. jos huoneita on kolme, käydään ensin huone nro 1 läpi.
                         * Jos ykkösessä ei ollut suoraa yhteyttä pelaajan huoneeseen, lisätään läpikäymislistaan sen yhteyshuoneet
                         * Lopulta reitti löytyy -> pistetään muistiin kuinka monen hypyn kautta se löytyi
                         * Se huone jonka kautta liikkumiseen tarvittiin vähiten hyppyjä on paras 
                         *
                         * Tiedossa olevat virheet: 
                         * Laskuria lisätään jokaisen huoneen läpikäynnin jälkeen, joten se ei kerro tarkasti kuinka pitkä matka todellisuudessa oli. Tämän ei pitäisi haitata paljoa, koska pelaajan on vaikea vahingoittaa vihollista monen huoneen takaa.
                         * Lisäksi koodi ei tarkista kuinka isoja yksittäiset huoneet ovat. Vihollinen valitsee pidemmän reitin, jos siinä on vähemmän huoneita kun toisessa. */
                        kohteen_etsinta:
                        for (Huone h : v.getHuone().getYhteydet()) {
                            laskuri = 0;
                            //jos tästä huoneesta on yhteys, valitaan tämä eikä mietitä enempää.
                            if (h.onkoYhteytta(pelaajanHuone) || h == pelaajanHuone) {
                                kohde = h;
                                break;
                            }
                            // Käydään yhteyksiä pitkin koko kenttä läpi, kunnes löydetään pelaajan huone
                            List<Huone> lista = new ArrayList<>(h.getYhteydet());
                            for (int i = 0 ; i < lista.size() ; i++) {
                                laskuri++;
                                if (lista.get(i).onkoYhteytta(h)) {
                                    if (laskuri < paras_reitti) {
                                        kohde = h;
                                        paras_reitti = laskuri;
                                    }
                                    //Reitti löytyi jo, ei tarvita enempää yhteyksiä.
                                    //katsotaan seuraava mahdollisista huoneista, jotta 
                                    continue kohteen_etsinta; 
                                }
                                for (Huone seur : lista.get(i).getYhteydet() ) {
                                    //jos tätä ei tarkistettaisi niin tulisi infinite loop.
                                    //Muuten käyttäisin addAll-metodia.
                                    if (!lista.contains(seur)) lista.add(seur);
                                }
                            }
                        }
                        if (kohde != null) {
                            v.setTekeminen(Vihollinen.Tekeminen.etsi_pelaaja, v.getHuone().getYhteysHuoneeseen(kohde));
                        }
                    }
                    break;
                case etsi_pelaaja:
                    if (Math.abs(pelaaja.getX() - sijaintix) < 1 && Math.abs(pelaaja.getY() - sijaintiy) < 1) {
                        v.setSuunta(Math.atan2(pelaaja.getY() - v.getY(), pelaaja.getX() - v.getX()));
                        //Vihollinen on tarpeeksi lähellä, ei liikuta vaan hyökätään
                        pelaaja.vahingoita(v.hyokkaa());
                    }
                    else {
                        if (v.onkoSamassaHuoneessa() || v.getHuone() != pelaaja.getHuone()) {
                            final double x = v.getKohdeSijainti().getXDouble();
                            final double y = v.getKohdeSijainti().getYDouble();
                            double suunta = Math.atan2(y - v.getY(), x - v.getX());
                            double kohdex = sijaintix + (v.getNopeus() * siirtyma) * Math.cos(suunta);
                            double kohdey = sijaintiy + (v.getNopeus() * siirtyma) * Math.sin(suunta);

                            tarkistaJaLiikuta(v, kohdex, kohdey);
                            v.setSuunta(suunta);
                            if (!v.getHuone().onkoSisalla(kohdex, kohdey)) v.setTekeminen(Vihollinen.Tekeminen.hyokkaa);
                        }
                        else {
                            v.setHuone(kentta.getHuone(v.getX(), v.getY()));
                            v.setTekeminen(Vihollinen.Tekeminen.hyokkaa);
                        }
                    }
                    //jos ollaan ihan pelaajan vieressä -> hyökätään suoraan
                    //katsotaan ollaanko vielä samassa huoneessa -> jos ei, vaihdetaan tekeminen ja päivitetään huone
                    //liikutaan kohti nykyistä kohdetta.
                    break;
                default:
                    break;
            }
        }
    }
    /** Palauttaa kentällä tällä hetkellä liikkuvat ammukset.
     * @param alue Etäisyys pelaajasta, jonka ulkopuolella olevia ammuksia ei haeta.
     * @return Map, jossa ammusten spritet ja niiden sijainti suunnalla. */
    public Map<KoordSuunnalla, String> getAmmukset(double alue) {
        Map<KoordSuunnalla, String> map = new LinkedHashMap<>();
        synchronized (ammukset) {
            ammukset.stream().filter((a) -> (Math.abs(a.getX() - pelaaja.getX()) < alue && Math.abs(a.getX() - pelaaja.getX()) < alue)).forEach((a) -> {
                map.put(
                        new KoordSuunnalla(a.getX(), a.getY(), a.getSuunta()),
                        a.getSprite());
            });
        }
        return map;
    }
    /** Liikuttaa kentällä olevia ammuksia. Jos ammus osuu johonkin, osuman kohdetta vahingoitetaan ja ammus poistetaan.
     * @param siirtyma Kuinka kauan edellisestä siirrosta on kulunut.  */
    public void liikutaAmmuksia(double siirtyma) {
        List poistettavat = new ArrayList();
        ammukset.stream().map((a) -> {
            final double uusix = a.getX() + (a.getNopeus() * siirtyma) * Math.cos(a.getSuunta());
            final double uusiy = a.getY() + (a.getNopeus() * siirtyma) * Math.sin(a.getSuunta());
            if (!kentta.voikoLiikkua(uusix, uusiy)) poistettavat.add(a);
            a.setX(uusix);
            a.setY(uusiy);
            return a;
        }).forEach((a) -> {
            if (a.onkoPelaajanAmpuma()) {
                viholliset.stream().forEach((v) -> {
                    if (Math.abs(v.getX() - a.getX()) < 0.5 && Math.abs(v.getY() - a.getY()) < 0.5) {
                        v.vahingoita(a.getVahinko());
                        v.setTekeminen(Vihollinen.Tekeminen.hyokkaa);
                        herataViholliset(v.getHuone());
                        if (!a.voikoLavistaa()) {
                            poistettavat.add(a);
                        }
                    }
                });
            }
            else {
                if (Math.abs(pelaaja.getX() - a.getX()) < 0.5 && Math.abs(pelaaja.getY() - a.getY()) < 0.5) {
                    pelaaja.vahingoita(a.getVahinko() * 0.5);
                    if (!a.voikoLavistaa()) {
                        poistettavat.add(a);
                    }
                }
            }
        });
        //poistetaan ammukset, joita ei tarvita.
        ammukset.removeAll(poistettavat);
    }
    private void herataViholliset(Huone h) {
        viholliset.stream().filter((v) -> (h.onkoSisalla(v.getX(), v.getY()))).forEach((v) -> {
            v.setTekeminen(Vihollinen.Tekeminen.hyokkaa);
        });
    }
    /** Palauttaa kaikki kentällä olevat tavarat.
     * @param alue Tavaroiden maksimietäisyys pelaajasta.
     * @return  Kaikki alueen sisällä olevat tavarat. */
    public Map<KoordSuunnalla, String> getTavarat(double alue) {
        Map<KoordSuunnalla, String> map = new HashMap<>();
        synchronized (tavarat) {
            tavarat.stream().filter((t) -> (Math.abs(t.getX() - pelaaja.getX()) < alue && Math.abs(t.getX() - pelaaja.getX()) < alue)).forEach((t) -> {
                map.put(new KoordSuunnalla(t.getX(), t.getY(), t.getSuunta()), t.getSprite());
            });
        }
        return map;
    }
    
    private void tarkistaJaLiikuta(Piirrettava p, double uusix, double uusiy) {
        if (!kentta.voikoLiikkua(uusix, uusiy)) {
            //katsotaan kumpi akseli meni pieleen ja poistetaan sen siirtyminen
            if (!kentta.voikoLiikkua(uusix, p.getY())) uusix = p.getX();
            if (!kentta.voikoLiikkua(p.getX(), uusiy)) uusiy = p.getY();
        }
        p.setX(uusix);
        p.setY(uusiy);
    }
    private void lisaaTavara(Tavara t) {
        /*
            //kulma, jonka suuntaan tavara heitetään
            final double kulma = Math.toRadians(360 * Math.random());
            //lasketaan satunnaispiste pelaajan lähelle
            final double x = pelaaja.getX() + (0.2 + Math.random() * 0.3) * 2 * Math.cos(kulma);
            final double y = pelaaja.getY() + (0.2 + Math.random() * 0.3) * 2 * Math.sin(kulma);
            //luodaan tavara pelaajan alle
            Tavara t = Tavara.luoTavara(ase);
            t.setX(pelaaja.getX());
            t.setY(pelaaja.getY());
            //liikutetaan sitä edelliseen satunnaispisteeseen
            tarkistaJaLiikuta(t, x, y);
            //lisätää tavara kentälle
            tavarat.add(t);
        */
        final double kulma = Math.toRadians(360 * Math.random());
        final double ux = t.getX() + (0.4 + Math.random() * 0.6) * Math.cos(kulma);
        final double uy = t.getY() + (0.4 + Math.random() * 0.6) * Math.sin(kulma);
        tarkistaJaLiikuta(t, ux, uy);
        tavarat.add(t);
    }
    
    /** Aloittaa kentän alusta. Poistaa kentällä olevat nykyiset asiat ja palauttaa kaiken ennalleen. */
    public void aloitaAlusta() {
        pelaaja = new Pelaaja(0, 0);
        pelaaja.setX(kentta.getAloitusX());
        pelaaja.setY(kentta.getAloitusY());
        viholliset.clear();
        ammukset.clear();
        kuolleetViholliset.clear();
        kentan_vihollisasettelu.stream().forEach((v) -> {
            viholliset.add(OdottavaVihollinen.muutaViholliseksi(v));
        });
        tavarat.clear();
        tavarat.addAll(kentan_tavarat);
        lopetusGrafiikka = 0;
    }
}