
package it.unina.lbd.lbd2012;

import javax.swing.JButton;

/**
 *
 * @author justb
 */
public class CasellaButton extends JButton {
    private int posX;
    private int posY;
    private int codice;
    private boolean isEntrata;
    private boolean isUscita;
    private String tipo;
    private int punteggio = 0;
    
    public CasellaButton(String label) {
        super(label);
    }

    /**
     * @return the posX
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @param posX the posX to set
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * @return the posY
     */
    public int getPosY() {
        return posY;
    }

    /**
     * @param posY the posY to set
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * @return the codice
     */
    public int getCodice() {
        return codice;
    }

    /**
     * @param codice the codice to set
     */
    public void setCodice(int codice) {
        this.codice = codice;
    }

    /**
     * @return the isEntrata
     */
    public boolean isEntrata() {
        return isEntrata;
    }

    /**
     * @param isEntrata the isEntrata to set
     */
    public void setIsEntrata(boolean isEntrata) {
        this.isEntrata = isEntrata;
    }

    /**
     * @return the isUscita
     */
    public boolean isUscita() {
        return isUscita;
    }

    /**
     * @param isUscita the isUscita to set
     */
    public void setIsUscita(boolean isUscita) {
        this.isUscita = isUscita;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the punteggio
     */
    public int getPunteggio() {
        return punteggio;
    }

    /**
     * @param punteggio the punteggio to set
     */
    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
}
