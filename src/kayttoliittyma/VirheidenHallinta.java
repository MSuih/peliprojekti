package kayttoliittyma;

import java.lang.Thread.UncaughtExceptionHandler;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/** Virheiden kiinniottaminen ja näyttäminen käyttäjälle. Tämä siksi, että virheen tapahtuessa vain virheen kohdannut Thread suljetaan ja ilmoitus piilotetaan konsoliin, jolloin käyttäjälle jää rikkinäinen ikkuna eikä tietoa siitä mitä tapahtui. */
public class VirheidenHallinta implements UncaughtExceptionHandler{
    
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        JTextArea tekstialue = new JTextArea(10, 30);
        tekstialue.setEditable(false);
        tekstialue.setLineWrap(true);
        JScrollPane tekstiruutu = new JScrollPane(tekstialue);
        tekstialue.append("Ohjelman suorittamisessa tapahtui virhe!\n\nThread: ");
        tekstialue.append(t.toString());
        if (e.getCause() != null) {
            tekstialue.append("\nAlkuperäinen virhe: ");
            tekstialue.append(e.getCause().toString());
            tekstialue.append("\n\nAlk. stack:\n");
            for (StackTraceElement stack : e.getStackTrace()) {
                tekstialue.append(stack.toString());
                tekstialue.append("\n");
            }
        }
        tekstialue.append("\nVirhe: ");
        tekstialue.append(e.toString());
        tekstialue.append("\n\nStacktrace:\n");
        for (StackTraceElement stack : e.getStackTrace()) {
            tekstialue.append(stack.toString());
            tekstialue.append("\n");
        }
        tekstialue.setCaretPosition(0);
        
        JOptionPane.showMessageDialog(null, tekstiruutu, "Virhe!", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}    
