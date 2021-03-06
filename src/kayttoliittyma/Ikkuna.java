package kayttoliittyma;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.DataFormatException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import kayttoliittyma.TekstuuriVarasto.Tyyppi;
import logiikka.KoordSuunnalla;
import peli.Peli;
import valikko.ValikkoStack;
import valikko.ValikkoToiminto;

/** Pelin ikkuna, jossa pelitapahtumat pyörivät. Ikkuna ottaa vastaan pelaajan näppäinpainallukset, huolehtii niiden välittämisestä pelille, päivittää peliä tasaisin välein, piirtää ruutuun tulevat grafiikat ja huolehtii vielä valikoistakin. */
public class Ikkuna extends JFrame {
    private enum IkkunanNakyma {
        //TODO: Tarvitaanko tosiaan kansi muuttujaa? Tuleeko näitä enemmänkin vai miten tän on tarkoitus toimia
        peli_nakyvissa(true, true),
        paavalikko(false, false);

        private final boolean piirretaanko_peli;
        private final boolean piirretaanko_hud;

        private IkkunanNakyma(boolean piirraPeli, boolean piirraHUD) {
            piirretaanko_peli = piirraPeli;
            piirretaanko_hud = piirraHUD;
        }
        public boolean piirretaankoPeli() {
            return piirretaanko_peli;
        }
        public boolean piirretaankoAmmukset( ) {
            return piirretaanko_hud;
        }
        public boolean piirretaankoKuolemaTeksti() {
            return !piirretaanko_hud && piirretaanko_peli;
        }
    }

    private IkkunanNakyma nakyma = IkkunanNakyma.paavalikko;
    private Peli peli = null;
    private ValikkoStack valikko = new ValikkoStack(false);
    private final NappainKasittelija nappaimet = new NappainKasittelija();

    private double ikkunan_keskusx = 0;
    private double ikkunan_keskusy = 0;

    private Ikkuna() {
        //alustetaan ikkuna
        this.setSize(900, 700);
        //Custom cursor
        Cursor hiiri_taht = this.getToolkit().createCustomCursor(
                TekstuuriVarasto.haeTekstuuri(Tyyppi.yleinen, "tahtain"),
                new Point(15, 15),
                "CROSSHAIR_CURSOR");
        this.setCursor(hiiri_taht);
        this.setBackground(Color.BLACK);
        this.setLocationRelativeTo(null);
        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Pelituotannon projekti");
        //päivitämme itse ikkunamme, javan ei tarvitse tehdä mitään
        this.setIgnoreRepaint(true);
        //näppäimenpainallukset ja vastaavat välitetään tälle JFramelle
        this.setFocusable(true);

        //näppäinkäsittelijät
        this.addKeyListener(new NappainTapahtumat());
        this.addMouseListener(new HiiriTapahtumat());
    }

    /** Avaa uusi ikkuna. Ikkuna avataan tällä metodilla, muuten se ei toimi oikein. */
    public static void uusiIkkuna() {
        //luodaan uusi ikkuna ja pistetään se näkyviin
        Ikkuna i = new Ikkuna();
        i.setVisible(true);

        Thread piirto = new Thread("PiirtoThread") {
            @Override
            public void run() {
                i.piirtoLoop();
            }
        };
        piirto.setDaemon(true);
        Thread liike = new Thread("LiikeThread") {
            @Override
            public void run() {
                i.liikeLoop();
            }
        };
        liike.setDaemon(true);

        piirto.setPriority(Thread.MAX_PRIORITY);
        piirto.start();
        liike.setPriority(Thread.MAX_PRIORITY - 1);
        liike.start();
    }

    private double val_nappainalkux = 0;//valikon näppäinten aloituskohta
    private double val_nappainalkuy = 0;
    private double val_nappainkokox = 0;//valikon yksittäisen näppäimen koko
    private double val_nappainkokoy = 0;

    @SuppressWarnings("SleepWhileInLoop")
    private void piirtoLoop() {
        //Ensimmäisenä pitää luoda bufferointistrategia
        //kaksi bufferia, molemmissa käytetään kiihdytystä
        //bufferia vaihtaessa se väritetään JFramen taustavärillä jotta vanhat piirrot tyhjenee
        BufferCapabilities bufcap = new BufferCapabilities(
                new ImageCapabilities(true),
                new ImageCapabilities(true),
                BufferCapabilities.FlipContents.BACKGROUND);

        BufferStrategy strategia = luoBufferointiStrategia(bufcap);

        final RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        //piirtämisessä käytetyt grafiikat
        long edell_sekunti = System.nanoTime();
        int paivitetyt_ruudut = 0;
        int ruutulaskuri = 0;
        while (true) {
            do {
                do {
                    //Hae piirtämiseen sopivat grafiikat
                    Graphics2D g2d = (Graphics2D) strategia.getDrawGraphics();
                    g2d.setRenderingHints(hints);

                    // Lasketaan ruudun päivitysnopeus
                    final long aika_nyt = System.nanoTime();
                    if (aika_nyt - edell_sekunti > 1000000000L) {
                        //jos edellisestä sekuntimerkistä on kulunut yksi sekunti
                        edell_sekunti = aika_nyt;
                        paivitetyt_ruudut = ruutulaskuri;
                        ruutulaskuri = 0;
                    }
                    else ruutulaskuri++;

                    //MUUTTUJAT
                    //ikkunan reunojen koko
                    //Java piirtää JFramen päälle käyttöjärjestelmän ikkunan reunat.
                    //Eli osa pelistä jäisi piirtämättä jollei tätä tehtäisi.
                    final Insets ikkunanreunat = this.getInsets();
                    //ikkunan piirtoalueen sivun mitta
                    final int p_sivux = this.getWidth() - ikkunanreunat.left - ikkunanreunat.right;
                    final int p_sivuy = this.getHeight() - ikkunanreunat.top - ikkunanreunat.bottom;
                    //piirtämisalueen aloituskohta
                    final int p_alkux = 0 + ikkunanreunat.left;
                    final int p_alkuy = 0 + ikkunanreunat.top;
                    //piirtämisalueen keskikohta
                    ikkunan_keskusx = p_alkux + p_sivux / 2d;
                    ikkunan_keskusy = p_alkuy + p_sivuy / 2d;
                    //skaalauskerroin, jotta kuva pysyisi samanlaisena eri kokoisissa ikkunoissa
                    final double p_skaalaus = Math.max(p_sivux, p_sivuy) / Asetukset.ikkunan_oletuskoko;

                    //PIIRTÄMINEN
                    if (nakyma.piirretaankoPeli()) {
                        piirraPeli(g2d, p_skaalaus, p_alkux, p_alkuy, p_sivux, p_sivuy);

                        if (peli.onkoPelaajaElossa()) {
                            piirraUI(g2d, p_skaalaus, p_alkux, p_alkuy, p_sivux, p_sivuy);
                        } else {
                            //pelaaja kuoli, piirretään ohjeet sitä varten
                            g2d.setColor(Color.WHITE);
                            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
                            g2d.drawString(
                                "Paina R ja yritä uudestaan.",
                                p_alkux + p_sivux * 0.2f,
                                p_alkuy + p_sivuy * 0.9f);
                        }
                    }
                    else {
                        piirraValikko(g2d, p_alkux, p_alkuy, p_sivux, p_sivuy);
                    }

                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
                    //ruudun päivitysnopeus
                    if (Asetukset.piirra_paivitysnopeus) {
                        //g2d.drawString("FPS: " +paivitetyt_ruudut +  " Liike: " + liikkeen_paivitysnopeus, 50, 50);
                        g2d.drawString(
                            String.format(
                                "FPS: %d Liike: %d",
                                paivitetyt_ruudut,
                                liikkeen_paivitysnopeus),
                            50, 50);
                    }
                    //Pelaajan sijainti
                    if (Asetukset.piirra_sijainti && nakyma.piirretaankoPeli()){
                        g2d.drawString(String.format("X=%f Y=%f", peli.getPelaajaX(), peli.getPelaajaY()), 50, 70);
                    }

                    //piirtäminen valmista
                    g2d.dispose();
                } while (strategia.contentsRestored());

                strategia.show();
            } while (strategia.contentsLost());

            //odotetaan hetki ennen seuraavaa päivitystä, jottei prosessorinkäyttö nouse liikaa
            if (Asetukset.rajoita_paivitysnopeutta) {
                try {
                    Thread.sleep(Asetukset.paivitysnopeuden_rajoitin);
                }
                catch (InterruptedException ie) {
                    //ei tehdä mitään
                }
            }
        }
    }

    private BufferStrategy luoBufferointiStrategia(BufferCapabilities bufcap) {
        int i = 0;
        while(true) {
            try {
                //tehdään edellisten capabilities-määritelmän mukainen strategia
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        this.createBufferStrategy(2, bufcap);
                    } catch (AWTException ex) {
                        //Yritetään uudestaan
                    }
                });
                break;
            } catch (IllegalStateException | InvocationTargetException | InterruptedException ex) {
                //kiihdytys ei jostain syystä onnistunut
                //yritetään uudestaan
                System.out.println("Kiihdytys ei onnistunut! " + ex.getMessage());
                if (i > 30) throw new ExceptionInInitializerError("Buffereiden luominen ei onnistunut ollenkaan!");
            }
        }
        //palautetaan äsken luotu strategia
        return this.getBufferStrategy();
    }

    private void piirraPeli(Graphics2D g2d, double skaalaus, double alkux, double alkuy, double sivux, double sivuy) {
        //pelaajan sijainti
        final double pelaajax = peli.getPelaajaX();
        final double pelaajay = peli.getPelaajaY();
        //Piirrä tausta
        final int ruutujenMaara = 5;
        piirraTausta(ruutujenMaara, g2d, skaalaus);

        //Consumer muuttujana, jottei tätä tarvitsisi toistaa joka välissä
        final Consumer<Map.Entry<KoordSuunnalla, String>> haeJaPiirra = (x) -> {
            Image img = TekstuuriVarasto.haeTekstuuri(Tyyppi.yleinen, x.getValue());
            piirraGrafiikka(g2d, img, x.getKey().x, x.getKey().y, x.getKey().suunta, pelaajax, pelaajay, skaalaus);
        };

        //Piirrä kuolleet viholliset
        peli.getKuolleetVihollset(Asetukset.naytettava_alue).entrySet().forEach(haeJaPiirra);
        //piirrä "kenttä on ohi, täältä pääset pois" -nuoli
        if (!peli.onkoVihollisiaJaljella()) {
            Image img = TekstuuriVarasto.haeTekstuuri(Tyyppi.yleinen, "nuoli");
            AffineTransform aff = new AffineTransform();
            aff.translate(
                    ikkunan_keskusx - img.getWidth(null) / 2 * skaalaus,
                    ikkunan_keskusy - img.getHeight(null) / 2  * skaalaus);
            aff.scale(skaalaus, skaalaus);
            aff.rotate(Math.atan2(peli.getLopetusAlueY() - pelaajay, peli.getLopetusAlueX() - pelaajax), img.getWidth(null) / 2, img.getHeight(null) / 2);
            g2d.drawImage(img, aff, null);
        }
        //Piirrä tavarat
        peli.getTavarat(Asetukset.naytettava_alue).entrySet().forEach(haeJaPiirra);
        //Piirrä ammukset
        peli.getAmmukset(Asetukset.naytettava_alue).entrySet().forEach(haeJaPiirra);
        //Piirrä viholliset
        peli.getViholliset(Asetukset.naytettava_alue).entrySet().forEach(haeJaPiirra);
        //piirrä vihollisten tavarat
        peli.getVihollistenTavarat(Asetukset.naytettava_alue).entrySet().forEach(haeJaPiirra);
        //piirrä pelaaja
        Image img = TekstuuriVarasto.haeTekstuuri(Tyyppi.yleinen, peli.getPelaajaTekstuuri());
        AffineTransform aff = new AffineTransform();
        aff.rotate(peli.getPelaajaSuunta(), ikkunan_keskusx, ikkunan_keskusy);
        aff.translate(
                ikkunan_keskusx - img.getWidth(null) / 2 * skaalaus,
                ikkunan_keskusy - img.getHeight(null) / 2 * skaalaus);
        aff.scale(skaalaus, skaalaus);
        g2d.drawImage(img, aff, null);
        //piirrä pelaajan ase
        if (peli.onkoPelaajaElossa()) {
            String s = peli.getAseTekstuuri();
            if (s != null) {
                img = TekstuuriVarasto.haeTekstuuri(Tyyppi.yleinen, peli.getAseTekstuuri());
                aff = new AffineTransform();
                aff.rotate(peli.getPelaajaSuunta(), ikkunan_keskusx, ikkunan_keskusy);
                aff.translate(
                        ikkunan_keskusx - img.getWidth(null) / 2 * skaalaus,
                        ikkunan_keskusy - img.getHeight(null) / 2 * skaalaus);
                aff.scale(skaalaus, skaalaus);
                g2d.drawImage(img, aff, null);
            }
        }
        //himmennetään kuvaa kun peli on loppumassa
        if (peli.onkoPelaajaLopetusAlueella() && !peli.onkoVihollisiaJaljella()) {
            final Rectangle2D pimennys = new Rectangle2D.Double(0, 0, alkux + sivux, alkuy + sivuy);
            g2d.setColor(new Color(0, 0, 0, (int) (255 * peli.getLopetusPimennys())));
            g2d.fill(pimennys);
        }
    }

    private void piirraTausta(int ruutujenMaara, Graphics2D g2d, double skaalaus) {
        final double siirtox = peli.getPelaajaSiirtoX();
        final double siirtoy = peli.getPelaajaSiirtoY();
        peli.getTaustaRuudut(ruutujenMaara).entrySet().stream()
            .filter(x -> x.getValue() != null).forEach((ruutu) -> {
                String s = ruutu.getValue();
                Image img = TekstuuriVarasto.haeTekstuuri(Tyyppi.kentat, s);
                AffineTransform aff = new AffineTransform();
                aff.translate(
                        ikkunan_keskusx + ruutu.getKey().getXInt() * Asetukset.ruudun_koko * skaalaus - siirtox * Asetukset.ruudun_osa * skaalaus,
                        ikkunan_keskusy + ruutu.getKey().getYInt() * Asetukset.ruudun_koko * skaalaus - siirtoy * Asetukset.ruudun_osa * skaalaus);
                aff.scale(skaalaus, skaalaus);
                g2d.drawImage(img, aff, null);
            }
        );
    }

    private void piirraGrafiikka(Graphics2D g2d, Image img, double x, double y, double suunta, double pelaajax, double pelaajay, double skaalaus) {
        AffineTransform aff = new AffineTransform();
        aff.translate(
            ikkunan_keskusx + (x - pelaajax) * Asetukset.ruudun_osa * skaalaus - img.getWidth(null) / 2 * skaalaus,
            ikkunan_keskusy + (y - pelaajay) * Asetukset.ruudun_osa * skaalaus - img.getHeight(null) / 2 * skaalaus);
        aff.scale(skaalaus, skaalaus);
        aff.rotate(suunta, img.getWidth(null) / 2, img.getHeight(null) / 2);
        g2d.drawImage(img, aff, null);
    }

    private void piirraValikko(Graphics2D g2d, int p_alkux, int p_alkuy, int p_sivux, int p_sivuy) {
        //muuttujat
        final double p_vsivux = p_sivuy * 0.5; //valikon sivun mitta
        final double p_vsivuy = p_sivuy * 0.9;
        final double p_pohjax = p_alkux + p_sivux / 2 - p_vsivux / 2; //mistä valikko alkaa
        final double p_pohjay = p_alkuy + p_sivuy * 0.05;
        val_nappainalkux = p_pohjax; //mistä näppäimet alkaa
        val_nappainalkuy = p_pohjay + p_vsivuy * 0.27;
        val_nappainkokox = p_vsivux; //valikon näppäinten koko
        val_nappainkokoy = p_vsivuy * 0.08;
        g2d.setColor(Color.red);

        //piirrä pohja
        final Rectangle2D pohja = new Rectangle2D.Double(p_pohjax, p_pohjay, p_vsivux, p_vsivuy);
        g2d.draw(pohja);

        //piirrä logo
        Image logo = TekstuuriVarasto.haeTekstuuri(Tyyppi.yleinen, "otsikko");
        AffineTransform aff = new AffineTransform();
        aff.translate(p_pohjax + p_vsivux * 0.01, p_pohjay + p_vsivuy * 0.01);
        final double kerroin = p_vsivux / logo.getWidth(null);
        aff.scale(kerroin, kerroin);
        g2d.drawImage(logo, aff, null);

        //Piirrä valikon painikkeet
        final Font valittu = new Font(Font.DIALOG, Font.BOLD, 16);
        final Font normaali = new Font(Font.DIALOG, Font.PLAIN, 16);
        for (int i = 0; i < valikko.getPainikeLkm() ; i++) {
            if (valikko.onkoValittu(i)) {
                g2d.setFont(valittu);
            }
            else {
                g2d.setFont(normaali);
            }
            //laske tekstin koko
            final String painiketeksti = valikko.getPainikeTeksti(i);
            FontMetrics fm = g2d.getFontMetrics();
            final double korkeus = fm.getAscent() + fm.getLeading();
            final double leveys = fm.stringWidth(painiketeksti);
            //piirrä näppäimen reunat
            g2d.draw(new Rectangle2D.Double(
                val_nappainalkux * 1.02, val_nappainalkuy + val_nappainkokoy * i,
                p_vsivux * 0.96, val_nappainkokoy * 0.96));
            //piirrä teksti
            g2d.drawString(
                painiketeksti,
                (float) (val_nappainalkux + val_nappainkokox / 2 - leveys / 2),
                (float) (val_nappainalkuy + val_nappainkokoy * i + val_nappainkokoy * 0.5 + korkeus / 2));
        }
        //Piirrä takaisin-painike
        if (valikko.voikoPalataTaakse() || valikko.voikoPalataPeliin()) {
            if (valikko.getTakaisinValittu()) g2d.setFont(valittu);
            else g2d.setFont(normaali);
            final String takaisin = "Takaisin";
            final double leveys = g2d.getFontMetrics().stringWidth(takaisin);
            g2d.drawString(
                    takaisin,
                    (float) (val_nappainalkux + p_vsivux / 2 - leveys / 2),
                    (float) (val_nappainalkuy + val_nappainkokoy * 8.9));
        }
    }

    public void piirraUI(Graphics2D g2d, double skaalaus, int alkux, int alkuy, int sivux, int sivuy) {
        //Elämät
        final Rectangle2D elama_pohja = new Rectangle2D.Double(
                alkux + 40 * skaalaus,
                alkuy + sivuy - 80 * skaalaus,
                400 * skaalaus, 40 * skaalaus);
        final Color c = new Color(50, 50, 50, 180);
        g2d.setColor(c);
        g2d.fill(elama_pohja);
        final Rectangle2D elama_palkki = new Rectangle2D.Double(
                alkux + (40 + 4) * skaalaus,
                alkuy + sivuy - (80 - 4) * skaalaus,
                ((400 - 8) * skaalaus) * peli.getPelaajaElamat(), (40 - 8) * skaalaus);
        g2d.setColor(Color.red);
        g2d.fill(elama_palkki);
        //ammukset
        if (peli.piirretaankoAmmusMaara()) {
            int ammuksetlkm = peli.getAmmustenMaara();
            int lippaatlkm = peli.getLippaidenMaara();
            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
            if (peli.onkoAmmuksetVahissa()) g2d.setColor(Color.RED);
            else g2d.setColor(Color.WHITE);
            Image img = TekstuuriVarasto.haeTekstuuri(Tyyppi.yleinen, peli.getLipasIkoni());
            AffineTransform aff = new AffineTransform();
            aff.translate(
                alkux + sivux - 90 - img.getWidth(null) / 2,
                alkuy + sivuy - 50 - img.getHeight(null) / 2);
            aff.scale(skaalaus, skaalaus);
            for (int i = 0; i < lippaatlkm; i++) {
                aff.translate(23, 0);
                g2d.drawImage(img, aff, null);
            }
            g2d.drawString(
                ammuksetlkm + " / " + peli.getAmmustenMaaraMax(),
               (float) (alkux + sivux - 150 * skaalaus),
               (float) (alkuy + sivuy - 40 * skaalaus));
        }
    }

    private int liikkeen_paivitysnopeus = 0;
    @SuppressWarnings("SleepWhileInLoop")
    private void liikeLoop() {
        long aika_edellinen = System.nanoTime();
        int ruutulaskuri = 0;
        double aikalaskuri = 0;
        while (true) {
            //lasketaan kuinka paljon aikaa on kulunut edellisestä päivityskerrasta
            long aika_nyt = System.nanoTime();
            double siirtyma = (double) (aika_nyt - aika_edellinen) * 0.000000001d;
            aika_edellinen = aika_nyt;
            aikalaskuri += siirtyma;
            if (aikalaskuri > 1 ) {
                liikkeen_paivitysnopeus = ruutulaskuri;
                ruutulaskuri = 0;
                aikalaskuri = 0;
            }
            else ruutulaskuri++;
            // Jos valikko on auki
            if (!nakyma.piirretaankoPeli()) {
                final Point hiiri = this.getMousePosition();
                //siirretään valintaa sen perusteella minkä päällä hiiri on
                if (hiiri != null) {
                    final int hiirix = hiiri.x;
                    final int hiiriy = hiiri.y;

                    //Jos hiiri on valikkkoalueen sisällä
                    if (hiirix > val_nappainalkux
                            && hiiriy > val_nappainalkuy
                            && hiirix < val_nappainalkux + val_nappainkokox ) {
                        //laske mitä painiketta painetaan
                        final double painettu_nappain = (hiiriy - val_nappainalkuy) / val_nappainkokoy;
                        //jos hiiri on takaisin-painikkeen kohdalla
                        if (painettu_nappain > 8.5 && painettu_nappain < 9) valikko.setTakaisinValittu(true);
                        //muussa tapauksessa se on jonkun toisen painikkeen kohdalla
                        else {
                            valikko.setValittuPainike((int) painettu_nappain);
                            valikko.setTakaisinValittu(false);
                        }
                    }
                    //hanki jostain tiedot siitä mitkä näppäinalueen reunat on
                    //jos hiiri on niiden sisällä -> laske monesko näppäin
                    //muuta tulos intiksi ja pistä se valikkoon
                }
                //Katsotaan näppäinpainallukset
                if (li_pyorahda || li_ammu) {
                    ValikkoToiminto v = valikko.painaValittuaPainiketta();
                    if (v != null) {
                        if (v.toiminto == ValikkoToiminto.Toiminto.sulje) System.exit(0);
                        else if (v.toiminto == ValikkoToiminto.Toiminto.uusipeli) {
                            try {
                                peli = new Peli("kentta" + v.lisatieto + ".knt");
                                nakyma = IkkunanNakyma.peli_nakyvissa;
                            }
                            catch (DataFormatException ex) {
                                System.out.println("Kentta on virheellinen" + ex.getMessage());
                            }
                        }
                        else if (v.toiminto == ValikkoToiminto.Toiminto.takaisin) {
                            nakyma = IkkunanNakyma.peli_nakyvissa;
                        }
                    }
                }
                else if (li_valikko) {
                    if (valikko.voikoPalataTaakse()) {
                        valikko.palaaTakaisin();
                    }
                    else if (valikko.voikoPalataPeliin()) {
                        nakyma = IkkunanNakyma.peli_nakyvissa;
                    }
                }
                else {
                    if (li_ylos) {
                        valikko.liikutaValintaa(-1);
                    }
                    if (li_alas) {
                        valikko.liikutaValintaa(1);
                    }
                }
                nollaaMuuttujat();
            }
            //jos peli on käynnissä
            else {
                if (li_valikko) {
                    //pelaaja haluaa pysäyttää pelin ja avata valikon
                    valikko = new ValikkoStack(true);
                    nakyma = IkkunanNakyma.paavalikko;
                    nollaaMuuttujat();
                    continue;
                }
                //käännetään pelaajan hahmoa
                final Point hiiri = this.getMousePosition();
                if (hiiri != null) {
                    final int hiirix = hiiri.x;
                    final int hiiriy = hiiri.y;

                    final double kulma = Math.atan2(hiiriy - ikkunan_keskusy, hiirix - ikkunan_keskusx);
                    peli.kaannaPelaajaa(kulma);
                }
                //Tarkistetaan, mihinkä suuntaan pelaajaa pitää liikuttaa
                int liikey = 0;
                if (li_ylos) liikey--;
                if (li_alas) liikey++;
                int liikex = 0;
                if (li_vasen) liikex--;
                if (li_oikea) liikex++;
                //liikutetaan pelaajaa
                if (!(liikex == 0 && liikey == 0)) {
                    peli.liikutaPelaajaa(liikex, liikey, siirtyma);
                }
                peli.paivita(siirtyma);
                peli.liikutaAmmuksia(siirtyma);
                //tarkastetaan onko pelaaja lopetusalueella ja onko vihollisia jäljellä
                if (!peli.onkoVihollisiaJaljella() && peli.onkoPelaajaLopetusAlueella()) {
                    //jos pimennys on valmis, siirry takaisin valikkoon
                    if (peli.getOnkoPimennysValmis()) {
                        nollaaMuuttujat();
                        valikko = new ValikkoStack(false);
                        nakyma = IkkunanNakyma.paavalikko;
                    }
                }
                else {
                    if (!Asetukset.jaadyta_kaikki_viholliset) peli.liikutaVihollisia(siirtyma);
                    if (peli.onkoPelaajaElossa()) {
                        //Aseiden pomiminen tai poisheittäminen
                        if (li_pyorahda) {
                            peli.pyorahda(liikex, liikey);
                            li_pyorahda = false;
                        }
                        else if (li_poimi) {
                            peli.poimiTaiHeitaAse();
                            li_poimi = false;
                        }
                        else if (li_lataa) {
                            li_lataa = false;
                            peli.lataaAse();
                        }
                        else if (li_ammu) {
                            peli.kaytaAsetta();
                            //Jos ase ei ole sarjatuliase -> pelaajan pitää painaa hiiren näppäintä uudestaan
                            if (!peli.getOnkoSarjatuliase()) li_ammu = false;
                        }
                    }
                }
            }
            //Pelaaja kuoli -> latauspainikkeella aloitetaan alusta
            if (nakyma.piirretaankoPeli() && !peli.onkoPelaajaElossa() && li_lataa) {
                li_lataa = false;
                peli.aloitaAlusta();
            }

            //Rajoitetaan päivittämistä, jottei prosessorinkäyttö nouse liikaa
            try {
                Thread.sleep(Asetukset.paivitysnopeuden_rajoitin);
            } catch (InterruptedException ie) {
                // Ei tehdä mitään
            }
        }
    }
    private void nollaaMuuttujat() {
        li_ylos = false;
        li_alas = false;
        li_vasen = false;
        li_oikea = false;
        li_lataa = false;
        li_ammu = false;
        li_pyorahda = false;
        li_valikko = false;
    }
    private boolean li_ylos = false;
    private boolean li_alas = false;
    private boolean li_vasen = false;
    private boolean li_oikea = false;
    private boolean li_lataa = false;
    private boolean li_pyorahda = false;
    private boolean li_valikko = false;
    private class NappainTapahtumat extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            NappainKasittelija.Toiminto t = nappaimet.kasittele(e.getKeyCode());
            switch (t) {
                case ylos:
                    li_ylos = true;
                    break;
                case alas:
                    li_alas = true;
                    break;
                case vasen:
                    li_vasen = true;
                    break;
                case oikea:
                    li_oikea = true;
                    break;
                case lataa:
                    li_lataa = true;
                    break;
                case kayta:
                    li_pyorahda = true;
                    break;
                case valikko:
                    li_valikko = true;
                    break;
                case debug_jaadyta:
                    Asetukset.jaadyta_kaikki_viholliset = !Asetukset.jaadyta_kaikki_viholliset;
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            NappainKasittelija.Toiminto t = nappaimet.kasittele(e.getKeyCode());
            switch (t) {
                case ylos:
                    li_ylos = false;
                    break;
                case alas:
                    li_alas = false;
                    break;
                case vasen:
                    li_vasen = false;
                    break;
                case oikea:
                    li_oikea = false;
                    break;
            }
        }
    }
    private boolean li_ammu = false;
    private boolean li_poimi = false;
    private class HiiriTapahtumat extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) li_ammu = true;
            else if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) li_poimi = true;
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) li_ammu = false;
        }
    }

    /** Käynnistää uuden pelin.
     * @param args "-edit" avaa kenttäeditorin, "-nogl" poistaa opengl:n käytöstä. */
    public static void main(String[] args) {
        boolean editor = false;
        boolean opengl = true;
        for (String s : args) {
            if (s.equalsIgnoreCase("-edit")) editor = true;
            if (s.equalsIgnoreCase("-nogl")) opengl = false;
        }
        final boolean avaa_editori = editor;
        if (opengl) System.setProperty("sun.java2d.opengl", "true");
        Thread.setDefaultUncaughtExceptionHandler(new VirheidenHallinta());
        SwingUtilities.invokeLater(() -> {
            //Todo: Switch-rakenne ja lisää argumentteja
            if (avaa_editori) Editori.uusiIkkuna();
            else uusiIkkuna();
        });
    }
}