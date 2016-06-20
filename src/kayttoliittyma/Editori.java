package kayttoliittyma;

import aseet.AseHallinta;
import aseet.AseHallinta.AseTyyppi;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import objektit.OdottavaVihollinen;
import objektit.Tavara;
import peli.Huone;
import peli.Kentta;

        // Huomautus: editori on huonosti ohjelmoitu ja se vaatisi täyden uudelleenkirjoituksen. En kuitenkaan sitä jaksa enää tehdä, joten editori jää nykyiselleen.

/** Pelin kenttien editori. */
public class Editori extends JFrame {
    
    private final Kentta kentta;
    
    private enum Tilanne {
        ruutujen_muokkaus, pelaajan_sijainti, viholliset, huoneiden_muokkaus, lopetusalue_muokkaus, tavarat;
    }
    private Tilanne tilanne = Tilanne.ruutujen_muokkaus;
    
    //ruutujen muokkaukseen liittyvät
    private int kursorix = 0;
    private int kursoriy = 0;
    private boolean ruutu_valittu = false;
    private int ruutu_kursorix = 0;
    private int ruutu_kursoriy = 0;
    
    //vihollisiin liittyvät
    private final List<OdottavaVihollinen> viholliset;
    private boolean vihollinen_valittu = false;
    private volatile OdottavaVihollinen valittu_vihollinen = null;
    
    //Huoneisiin liittyvät
    private Huone valittu_huone = null;
    private boolean huone_valittu = false;
    private boolean huone_muokkaa_sivuja = false;
    private boolean huone_muokkaa_yhteyksia = false;
    
    //yhteys
    private Huone yhteys_valittu_huone = null;
    private boolean yhteys_on_muodostettu = false;
    
    //lopetusalueen
    private boolean lopetus_muokkaa_sivua = false;
    
    //tavarat
    private final List<Tavara> tavarat;
    private int valittu_tavara = 0;
    private boolean tavara_valittu = false;
    
    private Editori(int xsivu, int ysivu) {
        kentta = new Kentta(xsivu, ysivu);
        viholliset = new ArrayList<>();
        tavarat = new ArrayList<>();
        
        alustaIkkuna();
    }
    
    @SuppressWarnings("UnusedAssignment")
    private Editori(String s) {
        Kentta k = null;
        List<OdottavaVihollinen> v = null;
        List<Tavara> t = null;
        try (ObjectInputStream stream = 
                new ObjectInputStream( new BufferedInputStream(new FileInputStream(new File("kentat", s))))){
            k = (Kentta) stream.readObject();
            v = (List<OdottavaVihollinen>) stream.readObject();
            t = (List<Tavara>) stream.readObject();
        }
        catch (IOException | ClassNotFoundException | ClassCastException ex) {
            JOptionPane.showMessageDialog(
                    null, "Kenttätiedosto ei kelpaa!\n" + ex.getMessage(), "Virhe", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("", ex);
        }
        kentta = k;
        viholliset = v;
        tavarat = t;
        //viholliset = new ArrayList();
        //tavarat = new ArrayList();
        
        if (!viholliset.isEmpty()) {
            valittu_vihollinen = viholliset.get(1);
            vihollinen_valittu = true;
        }
        if (!kentta.getHuoneet().isEmpty()) {
            huone_valittu = true;
            valittu_huone = kentta.getSeuraavaHuone(null);
        }
        if (!tavarat.isEmpty()) {
            tavara_valittu = true;
        }
        kentta.getLadattavat().stream().forEach((str) -> {
            TekstuuriVarasto.lataaTekstuuri(TekstuuriVarasto.Tyyppi.kentat, str);
        });
        
        alustaIkkuna();
    }
    
    private void alustaIkkuna() {
        //alustetaan ikkuna
        this.setSize(900, 700);
        this.setBackground(Color.BLACK);
        this.setLocationRelativeTo(null);
        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Kenttäeditori");
        //päivitämme itse ikkunamme, javan ei tarvitse tehdä mitään
        this.setIgnoreRepaint(true);
        //näppäimenpainallukset ja vastaavat välitetään tälle JFramelle
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        
        this.addKeyListener(new NappainTapahtumat()); 
    }
    
    /** Luo uuden editori-ikkunan. Kysyy ensin, muokataanko edellistä kenttää vai luodaanko uusi. Tämän jälkeen avaa editorin. */
    public static void uusiIkkuna() {
        int x, y;
        final String s_uusikentta = "Luo uusi kenttä";
        final JComboBox<String> kenttalista = new JComboBox<>();
        final JSlider slider_x = new JSlider(2, 20, 10);
        slider_x.setMajorTickSpacing(2);
        slider_x.setMinorTickSpacing(1);
        slider_x.setPaintTicks(true);
        slider_x.setPaintLabels(true);
        final JSlider slider_y = new JSlider(2, 20, 10);
        slider_y.setMajorTickSpacing(2);
        slider_y.setMinorTickSpacing(1);
        slider_y.setPaintTicks(true);
        slider_y.setPaintLabels(true);
        
        JPanel kysymysikkuna = new JPanel() {
            private final JLabel teksti_kenttalista = new JLabel("Luo uusi kenttä tai valitse jokin aikaisemmista");
            private final JLabel teksti_x = new JLabel("Uusi kenttä: Sivun x (vasemmalta oikealla) pituus:");
            private final JLabel teksti_y = new JLabel("Uusi kenttä: Sivun y (ylhäältä alas) pituus:");
            
            {
                
                kenttalista.addItem(s_uusikentta);
                File kansio = new File("kentat");
                for (String s : kansio.list()) {
                    kenttalista.addItem(s);
                }
                this.setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                this.add(teksti_kenttalista, c);
                c.gridy = 1;
                this.add(kenttalista, c);
                c.gridy = 3;
                this.add(teksti_x, c);
                c.gridy = 4;
                this.add(slider_x, c);
                c.gridy = 5;
                this.add(teksti_y, c);
                c.gridy = 6;
                this.add(slider_y, c);
            }
        };
        JOptionPane.showMessageDialog(null, kysymysikkuna, "Tervetuloa käyttämään editoria", JOptionPane.PLAIN_MESSAGE);
        x = slider_x.getValue();
        y = slider_y.getValue();
        Editori e;
        if (kenttalista.getSelectedItem() == s_uusikentta) {
            e = new Editori(x, y);
        }
        else e = new Editori((String) kenttalista.getSelectedItem());
        e.setVisible(true);
        
        //Käynnistä threadit
        Thread piirto = new Thread("PiirtoThread"){
            @Override
            public void run() {
                e.piirtoLoop();
            }
        };
        piirto.setPriority(Thread.MAX_PRIORITY);
        piirto.start();
        
    }
    @SuppressWarnings("SleepWhileInLoop")
    private void piirtoLoop() {
        
        BufferCapabilities bufcap = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.BACKGROUND);
        try {
            this.createBufferStrategy(2, bufcap);
        } catch (AWTException ex) {
            this.createBufferStrategy(2);
        }
        BufferStrategy strategia = this.getBufferStrategy();
        
        final RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        Graphics2D g2d;
        while (true) {
            try {
                //Hae piirtämiseen sopivat grafiikat
                g2d = (Graphics2D) strategia.getDrawGraphics();
                g2d.setRenderingHints(hints);
            }
            catch (IllegalStateException ise) {
                //joskus bufferi voi tyhjentyä kesken piirtografiikan haun
                //javan directx-version virhe
                //jos näin tapahtuu, yritetään vaan uudestaan
                System.out.println(String.format("Buffer-virhe @ %d", System.currentTimeMillis()));
                continue;
            }
            final Insets ikkunanreunat = this.getInsets();
            //ikkunan piirtoalueen sivun mitta
            final int p_sivux = this.getWidth() - ikkunanreunat.left - ikkunanreunat.right;
            final int p_sivuy = this.getHeight() - ikkunanreunat.top - ikkunanreunat.bottom;
            //piirtämisalueen aloituskohta
            final int p_alkux = 0 + ikkunanreunat.left;
            final int p_alkuy = 0 + ikkunanreunat.top;
            //piirtämisalueen keskikohta
            final double p_keskusx = p_alkux + p_sivux / 2d;
            final double p_keskusy = p_alkuy + p_sivuy / 2d;
            //skaalauskerroin, jotta kuva pysyisi samanlaisena eri kokoisissa ikkunoissa
            final double p_skaalaus = Math.min(p_sivux, p_sivuy) / Asetukset.ikkunan_oletuskoko_editori;
            
            final double kamerax;
            final double kameray;
            if (tilanne == Tilanne.pelaajan_sijainti) {
                kamerax = kentta.getAloitusX();
                kameray = kentta.getAloitusY();
            }
            else if (tilanne == Tilanne.viholliset && valittu_vihollinen != null) {
                kamerax = valittu_vihollinen.getX();
                kameray = valittu_vihollinen.getY();
            }
            else if (tilanne == Tilanne.huoneiden_muokkaus && valittu_huone != null) {
                kamerax = valittu_huone.getKeskusX();
                kameray = valittu_huone.getKeskusY();
            }
            else if (tilanne == Tilanne.lopetusalue_muokkaus) {
                kamerax = kentta.getLopetusAlueX() + kentta.getLopetusAlueSivuX() / 2;
                kameray = kentta.getLopetusAlueY() + kentta.getLopetusAlueSivuY() / 2;
            }
            else if (tilanne == Tilanne.tavarat && tavara_valittu) {
                kamerax = tavarat.get(valittu_tavara).getX();
                kameray = tavarat.get(valittu_tavara).getY();
            }
            else {
                kamerax = kursorix * 10 + 5;
                kameray = kursoriy * 10 + 5;
            }
            
            //taustaruudut
            final int ruutujenmaara = 7;
            final int ruutux = (int) (kamerax / 10);
            final int ruutuy = (int) (kameray / 10);
            final double siirtox = kamerax % 10d;
            final double siirtoy = kameray % 10d;
            for (int fx = 0 ; fx < ruutujenmaara ; fx++ ) {
                for (int fy = 0 ; fy < ruutujenmaara ; fy++) {
                    final int nykyinenx = ruutux + (fx - ruutujenmaara / 2);
                    final int nykyineny = ruutuy + (fy - ruutujenmaara / 2);
                    
                    final String s = kentta.getRuutuKuva(nykyinenx, nykyineny);
                    Image img;
                    if (s != null) img = TekstuuriVarasto.haeTekstuuri(TekstuuriVarasto.Tyyppi.kentat, s);
                    else if (nykyinenx >= 0 && nykyineny >= 0 &&
                            nykyinenx < kentta.getRuutuMaaraX() && nykyineny < kentta.getRuutuMaaraY()){
                        img = TekstuuriVarasto.haeTekstuuri(TekstuuriVarasto.Tyyppi.kentat, "null");
                    }
                    else continue;
                    AffineTransform aff = new AffineTransform();
                    aff.translate(
                            p_keskusx + (fx - ruutujenmaara / 2) * 400 * p_skaalaus - siirtox * 40 * p_skaalaus, 
                            p_keskusy + (fy - ruutujenmaara / 2) * 400 * p_skaalaus - siirtoy * 40 * p_skaalaus);
                    aff.scale(p_skaalaus, p_skaalaus);
                    g2d.drawImage(img, aff, null);
                }
            }
            //Huoneet
            if (tilanne == Tilanne.huoneiden_muokkaus) {
                for (Huone huone : kentta.getHuoneet()) {
                    final Stroke s = new BasicStroke(2);
                    g2d.setStroke(s);
                    
                    final Color vari_huone = new Color(0, 60, 200, 80);
                    final Color vari_huone_valittu = new Color(40, 140, 200, 140);
                    if (huone == valittu_huone) g2d.setColor(vari_huone_valittu);
                    else g2d.setColor(vari_huone);
                    final Rectangle2D.Double huone_muoto = new Rectangle2D.Double(
                          p_keskusx + (huone.getX() - kamerax) * 40 * p_skaalaus, 
                          p_keskusy + (huone.getY() - kameray) * 40 * p_skaalaus,
                          huone.getSivuX() * 40 * p_skaalaus,
                          huone.getSivuY() * 40 * p_skaalaus);
                    
                    g2d.fill(huone_muoto);
                    if (huone == valittu_huone && huone_muokkaa_sivuja) g2d.setColor(Color.red);
                    else g2d.setColor(Color.black);
                    g2d.draw(huone_muoto);
                    final Color vari_viiva = new Color(255, 100, 100, 255);
                    g2d.setColor(vari_viiva);
                    for (Huone yhteys : huone.getYhteydet()) {
                        final Line2D.Double viiva = new Line2D.Double(
                                p_keskusx + (huone.getKeskusX() - kamerax) * 40 * p_skaalaus,
                                p_keskusy + (huone.getKeskusY() - kameray) * 40 * p_skaalaus,
                                p_keskusx + (huone.getYhteysHuoneeseen(yhteys).getXDouble() - kamerax) * 40 * p_skaalaus,
                                p_keskusy + (huone.getYhteysHuoneeseen(yhteys).getYDouble() - kameray) * 40 * p_skaalaus);
                        g2d.draw(viiva);
                    }
                }  
                if (huone_muokkaa_yhteyksia && !yhteys_on_muodostettu) {
                    g2d.setColor(new Color(255, 255, 100, 255));
                    g2d.draw(new Line2D.Double(
                            p_keskusx + (valittu_huone.getKeskusX() - kamerax) * 40 * p_skaalaus,
                            p_keskusy + (valittu_huone.getKeskusY() - kameray) * 40 * p_skaalaus,
                            p_keskusx + (yhteys_valittu_huone.getKeskusX() - kamerax) * 40 * p_skaalaus,
                            p_keskusy + (yhteys_valittu_huone.getKeskusY() - kameray) * 40 * p_skaalaus
                    ));
                }
            }
            //tavarat
            for (Tavara t : tavarat) {
                Image img = TekstuuriVarasto.haeTekstuuri(TekstuuriVarasto.Tyyppi.yleinen, t.getSprite());
                AffineTransform aff = new AffineTransform();
                aff.translate(
                        p_keskusx + (t.getX() - kamerax) * 40 * p_skaalaus - img.getWidth(null) / 2 * p_skaalaus,
                        p_keskusy + (t.getY() - kameray) * 40 * p_skaalaus - img.getHeight(null) / 2 * p_skaalaus);
                aff.scale(p_skaalaus, p_skaalaus);
                g2d.drawImage(img, aff, null);
            }
            //Viholliset
            for (OdottavaVihollinen v : viholliset) {
                Image img = TekstuuriVarasto.haeTekstuuri(TekstuuriVarasto.Tyyppi.yleinen, v.getSprite());
                AffineTransform aff = new AffineTransform();
                aff.translate(
                        p_keskusx + (v.getX() - kamerax) * 40 * p_skaalaus - img.getWidth(null) / 2 * p_skaalaus,
                        p_keskusy + (v.getY() - kameray) * 40 * p_skaalaus - img.getHeight(null) / 2 * p_skaalaus);
                aff.scale(p_skaalaus, p_skaalaus);
                if (tilanne == Tilanne.viholliset) {
                    //If tämä on valittu
                    if (v == valittu_vihollinen ) {
                        g2d.setComposite(AlphaComposite.SrcOver);
                    }
                    else {
                        final AlphaComposite ac = AlphaComposite.SrcOver.derive(0.5f);
                        g2d.setComposite(ac);
                    }
                } 
                g2d.drawImage(img, aff, null);
            }
            g2d.setComposite(AlphaComposite.SrcOver);
            //Pelaajan aloitussijainti
            Image img = TekstuuriVarasto.haeTekstuuri(TekstuuriVarasto.Tyyppi.yleinen, "pelaaja_aloitus");
            AffineTransform aff = new AffineTransform();
            aff.translate(
                    p_keskusx + (kentta.getAloitusX() - kamerax) * 40 * p_skaalaus - img.getWidth(null) / 2 * p_skaalaus,
                    p_keskusy + (kentta.getAloitusY() - kameray) * 40 * p_skaalaus - img.getHeight(null) / 2 * p_skaalaus);
            aff.scale(p_skaalaus, p_skaalaus);
            g2d.drawImage(img, aff, null);
            //lopetusalue
            if (tilanne == Tilanne.lopetusalue_muokkaus) {
                final Color vari_lopetus = new Color(200, 60, 0, 150);
                final Rectangle2D.Double lopetus_muoto = new Rectangle2D.Double(
                      p_keskusx + (kentta.getLopetusAlueX() - kamerax) * 40 * p_skaalaus, 
                      p_keskusy + (kentta.getLopetusAlueY() - kameray) * 40 * p_skaalaus,
                      kentta.getLopetusAlueSivuX() * 40 * p_skaalaus,
                      kentta.getLopetusAlueSivuY() * 40 * p_skaalaus);
                g2d.setColor(vari_lopetus);
                g2d.fill(lopetus_muoto);
            }
            
            //Valittu ruutu
            if (tilanne == Tilanne.ruutujen_muokkaus) {
                g2d.setColor(Color.GREEN);
                final Stroke s = new BasicStroke(3);
                g2d.setStroke(s);
                g2d.draw(new Rectangle2D.Double(
                            p_keskusx - (400 / 2 * p_skaalaus), p_keskusy - (400 / 2 * p_skaalaus),
                            400 * p_skaalaus, 400 * p_skaalaus));
                if (ruutu_valittu) {
                    final double aloitusx = p_keskusx - (400 / 2 * p_skaalaus);
                    final double aloitusy = p_keskusy - (400 / 2 * p_skaalaus);
                    final double seinansivu = 40 * p_skaalaus;
                    for (int x = 0; x < 10; x++){
                        for (int y = 0 ; y < 10; y++){
                            if (x == ruutu_kursorix  && y == ruutu_kursoriy) g2d.setColor(new Color(0, 255, 0, 150));
                            else g2d.setColor(new Color(255, 0, 0, 150));
                            if (kentta.voikoLiikkua(kursorix, kursoriy, x, y)) {
                                g2d.draw(new Rectangle2D.Double(
                                        aloitusx + seinansivu * x, aloitusy + seinansivu * y,
                                        seinansivu, seinansivu));
                            } 
                            else {
                                g2d.fill(new Rectangle2D.Double(
                                        aloitusx + seinansivu * x, aloitusy + seinansivu * y,
                                        seinansivu, seinansivu));
                            }
                        }
                    }
                }
            }
            //Ohjetekstit
            final Font f = new Font(Font.SERIF, Font.BOLD, 18);
            g2d.setFont(f);
            g2d.setColor(Color.red);
            g2d.drawString("F1 = Kenttäruudut, F2 = Pelaaja, F3 = Viholliset, F4 = Huoneet, F5 = lopetusalue, F6 = tavarat, F8 = tallenna", p_alkux + 20, p_alkuy + 20);
            if (tilanne == Tilanne.ruutujen_muokkaus) {
                final String s;
                if (kentta.onkoRuutua(kursorix, kursoriy) && !ruutu_valittu){
                    s = "WASD/nuolinäppäimet = siirry, 1 = poista käytöstä, 2 = muuta seiniä, 3 = muuta kuvaa";
                }
                else if (!ruutu_valittu)s = "WASD/nuolinäppäimet = siirry, 1 = ota käyttöön";
                else {
                    s = "WASD/nuolinäppäimet = siirry, Välilyönti = muuta seinän tilaa, 2 = lopeta seinien muokkaaminen";
                    g2d.drawString("Q = Muuta kaikkia pystysuunnassa, E = muuta kaikkia vaakasuunnassa", 50, p_alkuy + p_sivuy - 30);
                }
                g2d.drawString(s, 50, p_alkuy + p_sivuy - 50);
            }
            else if (tilanne == Tilanne.pelaajan_sijainti) {
                final String s = "WASD/nuolinäppäimet = Siirrä, Shift = hidasta";
                g2d.drawString(s, 50, p_alkuy + p_sivuy - 50);
            }
            else if (tilanne == Tilanne.viholliset) {
                //Valittu vihollinen
                //jos sitä ei ole, näytetään joku oletusroiskasu
                //Vihollisen sijainti, elämät, nopeus ja sprite
                //Lisäksi yleinen ohjeteksti
                final String s1 = "Nuolet/WASD = siirrä, 1 = elämät, 2 = sprite, 3 = nopeus, 4 = poista, 5 = ase"; 
                g2d.drawString(s1, 50, p_alkuy + p_sivuy - 50);
                final String s2 = "Tab = Seuraava vihollinen, väli = luo uusi";
                g2d.drawString(s2, 50, p_alkuy + p_sivuy - 30);
                
                if (valittu_vihollinen != null) {
                    OdottavaVihollinen v = valittu_vihollinen;
                    g2d.drawString(String.format("Elamät = %f", v.getElamat()),
                            (float) p_alkuy + p_sivuy - 60, (float) p_keskusy);
                    g2d.drawString(String.format("nopeus = %f", v.getNopeus()),
                            (float) p_alkuy + p_sivuy - 60, (float) p_keskusy + 20);
                }
            }
            else if (tilanne == Tilanne.huoneiden_muokkaus) {
                final String s1;
                if (!huone_muokkaa_yhteyksia) s1 = "1 = Muuta kokoa, 2 = muokkaa yhteyksiä, 3 = poista, väli = uusi huone";
                else s1 = "2 = peruuta";
                final String s2;
                if (!huone_muokkaa_yhteyksia) s2 = "Tab = Seuraava huone, väli = luo uusi";
                else s2 = "väli = luo/poista yhteys tähän huoneeseen, Tab = Seuraava huone";
                g2d.drawString(s1, 50, p_alkuy + p_sivuy - 60);
                g2d.drawString(s2, 50, p_alkuy + p_sivuy - 30);
            } 
            else if (tilanne == Tilanne.lopetusalue_muokkaus) {
                if (lopetus_muokkaa_sivua) g2d.drawString("WASD = muuta kokoa, väli = siirrä", 50, p_alkuy + p_sivuy - 30);
                else g2d.drawString("WASD = siirrä, väli = muuta kokoa", 50, p_alkuy + p_sivuy - 30);
            }
            else if (tilanne == Tilanne.tavarat) {
                final String s = "WASD/nuolinäppäimet = Siirrä, Shift = hidasta, Tab = seuraava, vali = uusi, 1 = poista";
                g2d.drawString(s, 50, p_alkuy + p_sivuy - 50);
            }
            
            //piirtäminen valmista
            g2d.dispose();
            strategia.show();
            
            try {
                Thread.sleep(20);
            }
            catch (InterruptedException ie) {
                //ei tehdä mitään
            }
            
        }
    }

    private class NappainTapahtumat extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                //Muokkaustilasta toiseen siirtyminen
                case KeyEvent.VK_F1:
                    tilanne = Tilanne.ruutujen_muokkaus;
                    ruutu_valittu = false;
                    break;
                case KeyEvent.VK_F2:
                    tilanne = Tilanne.pelaajan_sijainti;
                    break;
                case KeyEvent.VK_F3:
                    tilanne = Tilanne.viholliset;
                    break;
                case KeyEvent.VK_F4:
                    tilanne = Tilanne.huoneiden_muokkaus;
                    huone_muokkaa_sivuja = false;
                    break;
                case KeyEvent.VK_F5:
                    tilanne = Tilanne.lopetusalue_muokkaus;
                    break;
                case KeyEvent.VK_F6:
                    tilanne = Tilanne.tavarat;
                    break;
                //Tallentaminen
                case KeyEvent.VK_F8:
                    String s = JOptionPane.showInputDialog(
                            null,
                            "Anna kentän tiedoston nimi",
                            "Tallenna",
                            JOptionPane.PLAIN_MESSAGE);
                    if (s == null) return;
                    try (ObjectOutputStream stream = 
                            new ObjectOutputStream(new FileOutputStream(new File("kentat", s + ".knt")))){
                        stream.writeObject(kentta);
                        ArrayList a = (ArrayList) viholliset;
                        a.trimToSize();
                        stream.writeObject(viholliset);
                        a = (ArrayList) tavarat;
                        a.trimToSize();
                        stream.writeObject(tavarat);
                        //Todo: tallenna muuta? Efektit esim.
                        JOptionPane.showMessageDialog(rootPane, "Tallentaminen onnistui", "Ilmoitus", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(rootPane, "Virhe tallentaessa\n" + ioe.getMessage(), "Virhe", JOptionPane.ERROR_MESSAGE);
                        //throw new RuntimeException("");
                    }
                    break;
                // Ruudun valinta
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    //liikutaan ylöspäin
                    if (tilanne == Tilanne.ruutujen_muokkaus) {
                        if (!ruutu_valittu) kursoriy = Math.max(kursoriy - 1, 0);
                        else ruutu_kursoriy = Math.max(ruutu_kursoriy - 1, 0);
                    }
                    else if (tilanne == Tilanne.pelaajan_sijainti) {
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            kentta.setAloitusY(Math.max(0, kentta.getAloitusY() - 0.2));
                        }
                        else kentta.setAloitusY(Math.max(0, kentta.getAloitusY() - 1));
                    }
                    else if (tilanne == Tilanne.viholliset) {
                        if (!vihollinen_valittu) return;
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_vihollinen.setY(Math.max(0, valittu_vihollinen.getY() - 0.2));
                        }
                        else valittu_vihollinen.setY(Math.max(0, valittu_vihollinen.getY() - 1));
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus) {
                        if (!huone_valittu) return;
                        if (huone_muokkaa_yhteyksia) {
                            if (yhteys_valittu_huone != null && yhteys_on_muodostettu) {
                                valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).setY(valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).getYDouble() - 0.3);
                            }
                            return;
                        }
                        else if (huone_muokkaa_sivuja) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                valittu_huone.setSivuY(Math.max(0, valittu_huone.getSivuY() - 0.2));
                            }
                            else valittu_huone.setSivuY(Math.max(0, valittu_huone.getSivuY() - 1));
                            return;
                        }
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_huone.setY(Math.max(0, valittu_huone.getY() - 0.2));
                        }
                        else valittu_huone.setY(Math.max(0, valittu_huone.getY() - 1));
                    }
                    else if (tilanne == Tilanne.lopetusalue_muokkaus) {
                        if (lopetus_muokkaa_sivua) kentta.setLopetusSivuY(kentta.getLopetusAlueSivuY() - 1);
                        else kentta.setLopetusAlueY(kentta.getLopetusAlueY() - 1);
                    }
                    else if (tilanne == Tilanne.tavarat) {
                        if (tavara_valittu) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                tavarat.get(valittu_tavara).setY(Math.max(0, tavarat.get(valittu_tavara).getY() - 0.2));
                            }
                            else tavarat.get(valittu_tavara).setY(Math.max(0, tavarat.get(valittu_tavara).getY() - 1));
                        }
                    }
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    //liikutaan alaspäin
                    if (tilanne == Tilanne.ruutujen_muokkaus) {
                        if (!ruutu_valittu) kursoriy = Math.min(kursoriy +1, kentta.getRuutuMaaraY() - 1);
                        else ruutu_kursoriy = Math.min(ruutu_kursoriy +1, 9);
                    }
                    else if (tilanne == Tilanne.pelaajan_sijainti) {
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            kentta.setAloitusY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, kentta.getAloitusY() + 0.2));
                        }
                        else kentta.setAloitusY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, kentta.getAloitusY() + 1));
                    }
                    else if (tilanne == Tilanne.viholliset) {
                        if (!vihollinen_valittu) return;
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_vihollinen.setY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, valittu_vihollinen.getY() + 0.2));
                        }
                        else valittu_vihollinen.setY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, valittu_vihollinen.getY() + 1));
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus) {
                        if (!huone_valittu) return;
                        if (huone_muokkaa_yhteyksia) {
                            if (yhteys_valittu_huone != null && yhteys_on_muodostettu) {
                                valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).setY(valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).getYDouble() + 0.3);
                            }
                            return;
                        }
                        else if (huone_muokkaa_sivuja) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                valittu_huone.setSivuY(valittu_huone.getSivuY() + 0.2);
                            }
                            else valittu_huone.setSivuY(valittu_huone.getSivuY() + 1);
                            return;
                        }
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_huone.setY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, valittu_huone.getY() + 0.2));
                        }
                        else valittu_huone.setY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, valittu_huone.getY() + 1));
                    }
                    else if (tilanne == Tilanne.lopetusalue_muokkaus) {
                        if (lopetus_muokkaa_sivua) kentta.setLopetusSivuY(kentta.getLopetusAlueSivuY() + 1);
                        else kentta.setLopetusAlueY(kentta.getLopetusAlueY() + 1);
                    }
                    else if (tilanne == Tilanne.tavarat) {
                        if (tavara_valittu) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                tavarat.get(valittu_tavara).setY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, tavarat.get(valittu_tavara).getY() + 0.2));
                            }
                            else tavarat.get(valittu_tavara).setY(Math.min(kentta.getRuutuMaaraY() * 10 - 1, tavarat.get(valittu_tavara).getY() + 1));
                        }
                    }
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    //liikutaan vasemmalle
                    if (tilanne == Tilanne.ruutujen_muokkaus) {
                        if (!ruutu_valittu) kursorix = Math.max(kursorix - 1, 0);
                        else ruutu_kursorix = Math.max(ruutu_kursorix - 1, 0);
                    }
                    else if (tilanne == Tilanne.pelaajan_sijainti) {
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            kentta.setAloitusX(Math.max(0, kentta.getAloitusX() - 0.2));
                        }
                        else kentta.setAloitusX(Math.max(0, kentta.getAloitusX() - 1));
                    }
                    else if (tilanne == Tilanne.viholliset) {
                        if (!vihollinen_valittu) return;
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_vihollinen.setX(Math.max(0, valittu_vihollinen.getX() - 0.2));
                        }
                        else valittu_vihollinen.setX(Math.max(0, valittu_vihollinen.getX() - 1));
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus) {
                        if (!huone_valittu) return;
                        if (huone_muokkaa_yhteyksia) {
                            if (yhteys_valittu_huone != null && yhteys_on_muodostettu) {
                                valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).setX(valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).getXDouble() - 0.3);
                            }
                            return;
                        }
                        else if (huone_muokkaa_sivuja) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                valittu_huone.setSivuX(Math.max(0, valittu_huone.getSivuX() - 0.2));
                            }
                            else valittu_huone.setSivuX(Math.max(0, valittu_huone.getSivuX() - 1));
                            return;
                        }
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_huone.setX(Math.max(0, valittu_huone.getX() - 0.2));
                        }
                        else valittu_huone.setX(Math.max(0, valittu_huone.getX() - 1));
                    }
                    else if (tilanne == Tilanne.lopetusalue_muokkaus) {
                        if (lopetus_muokkaa_sivua) kentta.setLopetusSivuX(kentta.getLopetusAlueSivuX() - 1);
                        else kentta.setLopetusAlueX(kentta.getLopetusAlueX() - 1);
                    }
                    else if (tilanne == Tilanne.tavarat) {
                        if (tavara_valittu) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                tavarat.get(valittu_tavara).setX(Math.max(0, tavarat.get(valittu_tavara).getX() - 0.2));
                            }
                            else tavarat.get(valittu_tavara).setX(Math.max(0, tavarat.get(valittu_tavara).getX() - 1));
                        }
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    //liikutaan oikealle
                    if (tilanne == Tilanne.ruutujen_muokkaus) {
                        if (!ruutu_valittu) kursorix = Math.min(kursorix +1, kentta.getRuutuMaaraX() - 1);
                        else ruutu_kursorix = Math.min(ruutu_kursorix +1, 9);
                    }
                    else if (tilanne == Tilanne.pelaajan_sijainti) {
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            kentta.setAloitusX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, kentta.getAloitusX() + 0.2));
                        }
                        else kentta.setAloitusX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, kentta.getAloitusX() + 1));
                    }
                    else if (tilanne == Tilanne.viholliset) {
                        if (!vihollinen_valittu) return;
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_vihollinen.setX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, valittu_vihollinen.getX() + 0.2));
                        }
                        else valittu_vihollinen.setX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, valittu_vihollinen.getX() + 1));
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus) {
                        if (!huone_valittu) return;
                        if (huone_muokkaa_yhteyksia) {
                            if (yhteys_valittu_huone != null && yhteys_on_muodostettu) {
                                valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).setX(valittu_huone.getYhteysHuoneeseen(yhteys_valittu_huone).getXDouble() + 0.3);
                            }
                            return;
                        }
                        else if (huone_muokkaa_sivuja) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                valittu_huone.setSivuX(valittu_huone.getSivuX() + 0.2);
                            }
                            else valittu_huone.setSivuX(valittu_huone.getSivuX() + 1);
                            return;
                        }
                        if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                            valittu_huone.setX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, valittu_huone.getX() + 0.2));
                        }
                        else valittu_huone.setX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, valittu_huone.getX() + 1));
                    }
                    else if (tilanne == Tilanne.lopetusalue_muokkaus) {
                        if (lopetus_muokkaa_sivua) kentta.setLopetusSivuX(kentta.getLopetusAlueSivuX() + 1);
                        else kentta.setLopetusAlueX(kentta.getLopetusAlueX() + 1);
                    }
                    else if (tilanne == Tilanne.tavarat) {
                        if (tavara_valittu) {
                            if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
                                tavarat.get(valittu_tavara).setX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, tavarat.get(valittu_tavara).getX() + 0.2));
                            }
                            else tavarat.get(valittu_tavara).setX(Math.min(kentta.getRuutuMaaraX() * 10 - 1, tavarat.get(valittu_tavara).getX() + 1));
                        }
                    }
                    break;
                //Toimintonäppäimet
                case KeyEvent.VK_1:
                case KeyEvent.VK_NUMPAD1:
                    if (tilanne == Tilanne.ruutujen_muokkaus && !ruutu_valittu) {
                        //valittu ruutu otetaan käyttöön tai pois käytöstä (muuttuja asetetaan nulliksi tai uudeksi ruuduksi)
                        if (!kentta.onkoRuutua(kursorix, kursoriy)) kentta.otaRuutuKayttoon(kursorix, kursoriy);
                        else kentta.poistaRuutuKaytosta(kursorix, kursoriy);
                    }
                    else if (tilanne == Tilanne.viholliset && vihollinen_valittu) {
                        double d = valittu_vihollinen.getElamat();
                        String uudet_elamat = JOptionPane.showInputDialog(null, "Syötä uusi elämäpisteiden määrä", d);
                        try {
                            if (uudet_elamat != null)  {
                                valittu_vihollinen.setElamat(Double.parseDouble(uudet_elamat));
                            }
                        }
                        catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(null, "Numero ei kelpaa", "Virhe", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus && huone_valittu) {
                        huone_muokkaa_sivuja = !huone_muokkaa_sivuja;
                    }
                    else if (tilanne == Tilanne.tavarat && tavara_valittu) {
                        int i = valittu_tavara;
                        if (tavarat.size() == 1) tavara_valittu = false;
                        else if (i == tavarat.size() - 1) valittu_tavara--;
                        tavarat.remove(i);
                    }
                    break;
                case KeyEvent.VK_2:
                case KeyEvent.VK_NUMPAD2:
                    if (tilanne == Tilanne.ruutujen_muokkaus && kentta.onkoRuutua(kursorix, kursoriy)) {
                        //Aloitetaan valitun ruudun seinien muokkaus (jos ruutu on käytettävissä)
                        ruutu_valittu = !ruutu_valittu;
                        ruutu_kursorix = 0;
                        ruutu_kursoriy = 0;
                    }
                    else if (tilanne == Tilanne.viholliset && vihollinen_valittu) {
                        JFileChooser valintaruutu = new JFileChooser(new File("tekstuurit", "yleinen"));
                        valintaruutu.setFileFilter(new FileNameExtensionFilter("PNG Image" , "png"));
                        int i = valintaruutu.showOpenDialog(null);
                        if (i == JFileChooser.APPROVE_OPTION) {
                            String tiedosto = valintaruutu.getSelectedFile().getName();
                            tiedosto = tiedosto.substring(0, Math.max(tiedosto.length() - 1 - 3, 0));
                            valittu_vihollinen.setSprite(tiedosto);
                        }
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus) {
                        //muuta yhteyksiä
                        if (huone_muokkaa_sivuja) return;
                        if (huone_muokkaa_yhteyksia) {
                            huone_muokkaa_yhteyksia = false;
                        }
                        else {
                            if (valittu_huone == null) return;
                            yhteys_valittu_huone = kentta.getSeuraavaHuone(valittu_huone);
                            if (yhteys_valittu_huone == valittu_huone) return;
                            huone_muokkaa_yhteyksia = true;
                            yhteys_on_muodostettu = valittu_huone.onkoYhteytta(yhteys_valittu_huone);
                        }
                    }
                    break;
                case KeyEvent.VK_3:
                case KeyEvent.VK_NUMPAD3:
                    //Muutetaan valitun ruudun pohjana olevaa ruutukuvaa (jos ruutu on käytettävissä)
                    if (tilanne == Tilanne.ruutujen_muokkaus && !ruutu_valittu && kentta.onkoRuutua(kursorix, kursoriy)) {
                        JFileChooser valintaruutu = new JFileChooser(new File("tekstuurit", "kentat"));
                        valintaruutu.setFileFilter(new FileNameExtensionFilter("PNG Image" , "png"));
                        int i = valintaruutu.showOpenDialog(null);
                        if (i == JFileChooser.APPROVE_OPTION) {
                            String tiedosto = valintaruutu.getSelectedFile().getName();
                            tiedosto = tiedosto.substring(0, Math.max(tiedosto.length() - 1 - 3, 0));
                            kentta.setRuutuKuva(kursorix, kursoriy, tiedosto);
                        }
                    }
                    else if (tilanne == Tilanne.viholliset && vihollinen_valittu) {
                        double d = valittu_vihollinen.getNopeus();
                        String uusi_nopeus = JOptionPane.showInputDialog(null, "Syötä uusi nopeus", d);
                        try {
                            if (uusi_nopeus != null)  {
                                valittu_vihollinen.setNopeus(Double.parseDouble(uusi_nopeus));
                            }
                        }
                        catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(null, "Numero ei kelpaa", "Virhe", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus && !huone_muokkaa_sivuja) {
                        if (!kentta.onkoSeuraavaaHuonetta()) {
                            huone_valittu = false;
                            kentta.poistaHuone(valittu_huone);
                            valittu_huone = null;
                        }
                        else {
                            kentta.poistaHuone(valittu_huone);
                            Huone h = kentta.getSeuraavaHuone(valittu_huone);
                            valittu_huone = h;
                        }
                    }
                    break;
                case KeyEvent.VK_4:
                case KeyEvent.VK_NUMPAD4:
                    if (tilanne == Tilanne.viholliset && vihollinen_valittu) {
                        viholliset.remove(valittu_vihollinen);
                        if (viholliset.size() <= 0) vihollinen_valittu = false;
                        else valittu_vihollinen = viholliset.get(viholliset.size() - 1);
                    }
                    break;
                case KeyEvent.VK_5:
                case KeyEvent.VK_NUMPAD5:
                    if (tilanne == Tilanne.viholliset && vihollinen_valittu) {
                        JComboBox valinta = new JComboBox();
                        valinta.addItem("Ei asetta / Poista ase");
                        AseHallinta.getAseTyypit().stream().forEach((t) -> {
                            valinta.addItem(t);
                        });
                        if (valittu_vihollinen.getAse() != null) valinta.setSelectedItem(valittu_vihollinen.getAse());
                        int i = JOptionPane.showConfirmDialog(null, valinta, "Valitse ase", JOptionPane.YES_NO_OPTION);
                        if (i == JOptionPane.YES_OPTION && valinta.getSelectedItem() instanceof AseTyyppi) {
                           valittu_vihollinen.setAse((AseTyyppi) valinta.getSelectedItem());
                        }
                        else if (i == JOptionPane.YES_OPTION) {
                            valittu_vihollinen.poistaAse();
                        }
                    }
                    break;
                case KeyEvent.VK_Q:
                    if (tilanne == Tilanne.ruutujen_muokkaus && ruutu_valittu) {
                        kentta.vaihdaLiikkumisTilaaPysty(kursorix, kursoriy, ruutu_kursorix, ruutu_kursoriy);
                    }
                    break;
                case KeyEvent.VK_E:
                    if (tilanne == Tilanne.ruutujen_muokkaus && ruutu_valittu) {
                        kentta.vaihdaLiikkumisTilaaVaaka(kursorix, kursoriy, ruutu_kursorix, ruutu_kursoriy);
                    }
                    break;
                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_ENTER:
                    if (tilanne == Tilanne.ruutujen_muokkaus && ruutu_valittu) {
                        kentta.vaihdaLiikkumisTilaa(kursorix, kursoriy, ruutu_kursorix, ruutu_kursoriy);
                    }
                    else if (tilanne == Tilanne.viholliset) {
                        viholliset.add(new OdottavaVihollinen());
                        valittu_vihollinen = viholliset.get(viholliset.size() - 1);
                        vihollinen_valittu = true;
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus) {
                        if (huone_muokkaa_yhteyksia) {
                            if (!yhteys_on_muodostettu) {
                                valittu_huone.lisaaYhteys(yhteys_valittu_huone);
                                yhteys_on_muodostettu = true;
                            }
                            else {
                                valittu_huone.poistaYhteys(yhteys_valittu_huone);
                                yhteys_on_muodostettu = false;
                            }
                            return;
                        }
                        kentta.lisaaHuone(new Huone());
                        valittu_huone = kentta.getViimeisinHuone();
                        huone_valittu = true;
                        huone_muokkaa_sivuja = false;
                    }
                    else if (tilanne == Tilanne.lopetusalue_muokkaus) {
                        lopetus_muokkaa_sivua = !lopetus_muokkaa_sivua;
                    }
                    else if (tilanne == Tilanne.tavarat) {
                        JComboBox<AseTyyppi> valinta = new JComboBox<>();
                        AseHallinta.getAseTyypit().stream().forEach((t) -> {
                            valinta.addItem(t);
                        });
                        int i = JOptionPane.showConfirmDialog(null, valinta, "Valitse ase", JOptionPane.YES_NO_OPTION);
                        if (i == JOptionPane.YES_OPTION) {
                            tavarat.add(Tavara.luoTavara((AseTyyppi) valinta.getSelectedItem()));
                            tavara_valittu = true;
                            valittu_tavara = tavarat.size() - 1;
                        }
                    }
                    break;
                case KeyEvent.VK_TAB:
                    if (tilanne == Tilanne.viholliset && vihollinen_valittu) {
                        int i = viholliset.indexOf(valittu_vihollinen) + 1;
                        if (i >= viholliset.size()) i = 0;
                        valittu_vihollinen = viholliset.get(i);
                    }
                    else if (tilanne == Tilanne.huoneiden_muokkaus) {
                        if (huone_muokkaa_yhteyksia) {
                            Huone h = kentta.getSeuraavaHuone(yhteys_valittu_huone);
                            if (h == valittu_huone) h = kentta.getSeuraavaHuone(h);
                            yhteys_valittu_huone = h;
                            yhteys_on_muodostettu = valittu_huone.onkoYhteytta(yhteys_valittu_huone);
                            return;
                        }
                        valittu_huone = kentta.getSeuraavaHuone(valittu_huone);
                        huone_muokkaa_sivuja = false;
                    }
                    else if (tilanne == Tilanne.tavarat && tavara_valittu) {
                        if (valittu_tavara == tavarat.size() - 1) valittu_tavara = 0;
                        else valittu_tavara++;
                    }
                break;
            }
        }
    }
    
    /** Käynnistää editorin.
     * @param args Ei käytössä. */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new VirheidenHallinta());
        SwingUtilities.invokeLater(() -> {
            uusiIkkuna();
        });
    }
}