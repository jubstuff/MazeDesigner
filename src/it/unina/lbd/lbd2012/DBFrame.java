/*
 * DBFrame.java
 *
 * Created on 5 novembre 2006, 8.48
 */
package it.unina.lbd.lbd2012;

import java.awt.Cursor;
import java.sql.*;
import javax.swing.*;

/**
 * Classe base per i frame di gestione delle tabelle principali. <br> Permette
 * le operazioni di inserimento, cancellazione, ricerca e navigazione.
 *
 * @author Massimo
 * @author ADeLuca
 * @version 2012
 */
abstract public class DBFrame extends javax.swing.JFrame implements NeedLookup {

    /**
     * Creates new form DBFrame
     */
    public DBFrame() {
        initComponents();
        bOk.setVisible(false);
    }
    /**
     * Indica che il frame si trova nello stato di inserimento di un nuovo
     * record o dei parametri di ricerca.
     *
     */
    final static public int APPEND_QUERY = 1;
    /**
     * Indica che il frame si trova nello stato di navigazione (ricerca
     * gi&agrave; effettuata).
     *
     */
    final static public int BROWSE = 2;
    /**
     * Indica che il frame si trova nello stato di modifica dei dati.
     *
     */
    final static public int UPDATE = 3;
    /**
     * indica che l'eccezione &egrave; stata sollevata eseguendo un comando
     * <i>SELECT</i>.
     */
    final static public int CONTESTO_ESEGUI_QUERY = 1;
    /**
     * Stato corrente del Frame.
     */
    private int modalita;
    /**
     * Posizione del record corrente nel resultSet.*
     */
    private int pos = 1;
    /**
     * modello della tabella di navigazione (quella in basso).
     */
    private DBTableModel modelloTabella;
    /**
     * ResultSet dell'ultima query eseguita
     */
    protected ResultSet rs;
    /**
     * Query da eseguire.
     */
    protected String query;
    /**
     * Nome della tabella (o vista).
     */
    private String nomeTabella;
    // protected String nomeVista;
    private javax.swing.JTable tabFrameTable;
    /**
     * Puntatore al Form che usa quello corrente come Lookup.
     */
    private NeedLookup padre = null;

    /**
     * Imposta il Puntatore al Form che usa quello corrente cone Lookup.
     */
    public void setPadre(NeedLookup p) {
        padre = p;
        if (padre == null) {
            bOk.setVisible(false);
        } else {
            bOk.setVisible(true);
        }
    }

    /**
     * Restituisce il puntatore al Form che usa quello corrente cone Lookup.
     */
    public NeedLookup getPadre() {
        return padre;
    }

    /**
     * Restituisce modello della tabella di navigazione.
     */
    DBTableModel getModelloTabella() {
        return modelloTabella;
    }

    /**
     * Imposta il puntatore al Form che usa quello corrente cone Lookup.
     */
    void setModelloTabella(DBTableModel gmt) {
        modelloTabella = gmt;
    }

    String getQuery() {
        return query;
    }

    /**
     * Imposta la query corrente.
     */
    void setQuery(String query) {
        this.query = query;
    }

    /**
     * Restituisce la query corrente.
     */
    String getNomeTabella() {
        return nomeTabella;
    }

    /**
     * Imposta il nome della tabella.
     */
    void setNomeTabella(String nomeTabella) {
        this.nomeTabella = nomeTabella;
    }

    /**
     * Metodo usato dalle form di lookup per passare dati al form chiamante.
     * <br> In DBFrame &egrave; disponibile un metodo vuoto per le form che non
     * necessitano di lookup.
     */
    public void setProprietaPadre(String proprieta, String valore) {
        //non pu? essere abstract, per alcuni frame non ? necessario
    }

    /**
     * Imposta la tabella di navigazione del form. <br> In genere dovrebbe
     * essere richiamato nei costruttori delle classi derivate.
     *
     */
    protected void setFrameTable(JTable t) {
        tabFrameTable = t;
        modelloTabella = new DBTableModel();
        tabFrameTable.setModel(modelloTabella);
        tabFrameTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        tabFrameTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                //       selezioneTabellaCambiata();
            }
        });

        tabFrameTable.getSelectionModel().addListSelectionListener(
                new javax.swing.event.ListSelectionListener() {
                    public void valueChanged(
                            javax.swing.event.ListSelectionEvent e) {
                        selezioneTabellaCambiata();
                    }
                });
    }

    /**
     * Chiave primaria del record corrente.
     */
    protected javax.swing.JTextField getTCodice() {
        return tCodice;
    }

    /**
     * Imposta lo stato corrente del form. <br> In base allo stato vengono
     * abilitati o disabilitati alcuni oggetti del form.
     *
     */
    public void setModalita(int modo) {
        modalita = modo;
        switch (modo) {
            case APPEND_QUERY:
                bPrimo.setEnabled(false);
                bPrecedente.setEnabled(false);
                bSuccessivo.setEnabled(false);
                bUltimo.setEnabled(false);
                bNuovo.setEnabled(true);
                bApri.setEnabled(false);
                bSalva.setEnabled(true);
                bCerca.setEnabled(true);
                bAnnulla.setEnabled(true);
                bElimina.setEnabled(false);
                if (tabFrameTable != null) {
                    tabFrameTable.setEnabled(false);
                }
                tCodice.setEnabled(true);
                bOk.setEnabled(false);
                break;
            case BROWSE:
                bPrimo.setEnabled(true);
                bPrecedente.setEnabled(true);
                bSuccessivo.setEnabled(true);
                bUltimo.setEnabled(true);
                bNuovo.setEnabled(true);
                bApri.setEnabled(true);
                bSalva.setEnabled(false);
                bCerca.setEnabled(false);
                bAnnulla.setEnabled(false);
                bElimina.setEnabled(false);
                if (tabFrameTable != null) {
                    tabFrameTable.setEnabled(true);
                }
                tCodice.setEnabled(false);
                bOk.setEnabled(true);
                break;
            case UPDATE:
                bPrimo.setEnabled(false);
                bPrecedente.setEnabled(false);
                bSuccessivo.setEnabled(false);
                bUltimo.setEnabled(false);
                bNuovo.setEnabled(true);
                bApri.setEnabled(false);
                bSalva.setEnabled(true);
                bCerca.setEnabled(false);
                bAnnulla.setEnabled(true);
                bElimina.setEnabled(true);
                if (tabFrameTable != null) {
                    tabFrameTable.setEnabled(false);
                }
                tCodice.setEnabled(false);
                bOk.setEnabled(false);
                break;
        }
    }

    /**
     * Mostra una descrizione di un errore SQL in un linguaggio comprensibile
     * per l'utente finale. <br> L'implementazione di DBFrame fa eccezione, essa
     * fornisce un messaggio standard per gli errori non previsti
     * esplicitamente.
     *
     */
    protected void mostraErrori(SQLException e, String query, int contesto) {
        String msg;
        if ((e.getErrorCode() == 17068 | e.getErrorCode() == 17011)
                & contesto == 0) {
            return; //questo errore non mi interessa
        }
        msg = "ErrorCode= " + e.getErrorCode() + "\n";
        msg += "Message= " + e.getMessage() + "\n";
        msg += "SQLState= " + e.getSQLState() + "\n";

        JOptionPane.showMessageDialog(this, msg, "Errore",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra una descrizione di un errore SQL in un linguaggio comprensibile
     * per l'utente finale. <br> L'implementazione di DBFrame fa eccezione, essa
     * fornisce un messaggio standard per gli errori non previsti
     * esplicitamente.
     *
     */
    protected void mostraErrori(SQLException e) {
        mostraErrori(e, "", 0);
    }

    /**
     * Mostra una descrizione di un errore in un linguaggio comprensibile per
     * l'utente finale. <br> L'implementazione di DBFrame fa eccezione, essa
     * fornisce un messaggio standard per gli errori non previsti
     * esplicitamente.
     *
     */
    protected void mostraErrori(Exception e, int contesto) {
        String msg;
        msg = e.getMessage() + "\n";

        JOptionPane.showMessageDialog(this, msg, "Errore",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra una descrizione di un errore in un linguaggio comprensibile per
     * l'utente finale. <br> L'implementazione di DBFrame fa eccezione, essa
     * fornisce un messaggio standard per gli errori non previsti
     * esplicitamente.
     *
     */
    protected void mostraErrori(Exception e) {
        mostraErrori(e, 0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dBTableModel1 = new it.unina.lbd.lbd2012.DBTableModel();
        bPrimo = new javax.swing.JButton();
        bPrecedente = new javax.swing.JButton();
        bSuccessivo = new javax.swing.JButton();
        bUltimo = new javax.swing.JButton();
        bNuovo = new javax.swing.JButton();
        bSalva = new javax.swing.JButton();
        bCerca = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tCodice = new javax.swing.JTextField();
        bApri = new javax.swing.JButton();
        bAnnulla = new javax.swing.JButton();
        bElimina = new javax.swing.JButton();
        bOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bPrimo.setText("|<");
        bPrimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPrimoActionPerformed(evt);
            }
        });

        bPrecedente.setText("<");
        bPrecedente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPrecedenteActionPerformed(evt);
            }
        });

        bSuccessivo.setText(">");
        bSuccessivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSuccessivoActionPerformed(evt);
            }
        });

        bUltimo.setText(">|");
        bUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUltimoActionPerformed(evt);
            }
        });

        bNuovo.setText("Nuovo");
        bNuovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNuovoActionPerformed(evt);
            }
        });

        bSalva.setText("Salva");
        bSalva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSalvaActionPerformed(evt);
            }
        });

        bCerca.setText("Cerca");
        bCerca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCercaActionPerformed(evt);
            }
        });

        jLabel1.setText("Codice");

        tCodice.setEditable(false);

        bApri.setText("Apri");
        bApri.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bApriActionPerformed(evt);
            }
        });

        bAnnulla.setText("Annulla");
        bAnnulla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAnnullaActionPerformed(evt);
            }
        });

        bElimina.setText("Elimina");
        bElimina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEliminaActionPerformed(evt);
            }
        });

        bOk.setText("OK");
        bOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOkActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(19, 19, 19)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(bPrimo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bPrecedente)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bSuccessivo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bUltimo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bNuovo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bApri)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bSalva))
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(55, 55, 55)
                        .add(tCodice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(bCerca)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bElimina))
                    .add(layout.createSequentialGroup()
                        .add(bOk, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bAnnulla)))
                .add(96, 96, 96))
        );

        layout.linkSize(new java.awt.Component[] {bCerca, bOk}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {bAnnulla, bElimina}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bPrimo)
                    .add(bPrecedente)
                    .add(bSuccessivo)
                    .add(bUltimo)
                    .add(bNuovo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bSalva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bApri, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bElimina, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bCerca, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(tCodice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bOk, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bAnnulla, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(372, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {bAnnulla, bApri, bCerca, bElimina, bNuovo, bOk, bSalva, bUltimo}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    protected void impostaCodice() {
        String codice;
        System.out.println("select max(codice)+1 from " + Database.schema + "."
                + this.nomeTabella);
        codice = Database.leggiValore("select nvl(max(codice)+1,1) from "
                + Database.schema + "." + this.nomeTabella).toString();
        tCodice.setText(codice);
    }

    /**
     * Metodo da usare nelle form di lookup per passare i dati alla form
     * chiamante.
     *
     */
    protected void premutoOK() {
        //non puo' essere abstract, per alcuni frame non e' necessario
    }

    private void bOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOkActionPerformed
        premutoOK();
    }//GEN-LAST:event_bOkActionPerformed

    private void bEliminaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEliminaActionPerformed
        String cmd;
        cmd = "delete from " + Database.schema + "." + nomeTabella
                + " where codice=" + tCodice.getText();
        try {
            if (rs.isLast()) {
                pos--;
            }
        } catch (SQLException e) {
            mostraErrori(e);
        }
        eseguiComando(cmd);
        pulisci();
        eseguiQuery();
    }//GEN-LAST:event_bEliminaActionPerformed

    private void bAnnullaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAnnullaActionPerformed
        eseguiQuery();
        if (getPadre() != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                mostraErrori(e);
            } finally {
                dispose();
            }
        }
    }//GEN-LAST:event_bAnnullaActionPerformed

    private void selezioneTabellaCambiata() {
        try {
            int row = tabFrameTable.getSelectionModel().getMinSelectionIndex() + 1;
            rs.absolute(row);
            mostraDati();

        } catch (SQLException e) {
            mostraErrori(e);
        } catch (Exception a) {
        }
    }

    private void bApriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bApriActionPerformed

        setModalita(UPDATE);
    }//GEN-LAST:event_bApriActionPerformed

    /**
     * Mostra i dati presenti nel record corrente. <br> Necessita di overriding
     * in tutte le classi derivate di DBFrame.
     *
     */
    protected void mostraDati() {
        try {
            tCodice.setText(rs.getString("codice"));
            pos = rs.getRow();
            tabFrameTable.setRowSelectionInterval(pos - 1, pos - 1);
        } catch (SQLException e) {
            mostraErrori(e);
        }
    }
    private void bUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUltimoActionPerformed
        try {
            rs.last();
        } catch (SQLException e) {
            mostraErrori(e);
        }
        mostraDati();
    }//GEN-LAST:event_bUltimoActionPerformed

    private void bSuccessivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSuccessivoActionPerformed
        try {
            if (!rs.isLast()) {
                rs.next();
            }
        } catch (SQLException e) {
            mostraErrori(e);
        }
        mostraDati();
    }//GEN-LAST:event_bSuccessivoActionPerformed

    private void bPrecedenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPrecedenteActionPerformed
        try {
            if (!rs.isFirst()) {
                rs.previous();
            }
        } catch (SQLException e) {
            mostraErrori(e);
        }
        mostraDati();
    }//GEN-LAST:event_bPrecedenteActionPerformed

    private void bPrimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPrimoActionPerformed
        try {
            rs.first();
        } catch (SQLException e) {
            mostraErrori(e);
        }
        mostraDati();
    }//GEN-LAST:event_bPrimoActionPerformed

    /**
     * Crea lo statement di ricerca. <br> Necessita di overriding in tutte le
     * classi derivate di DBFrame.
     *
     */
    protected PreparedStatement creaSelectStatement() {
        query = "select * from " + Database.schema + "." + nomeTabella + " ";
        return null;
    }
    private void bCercaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCercaActionPerformed
        eseguiQuery();
    }//GEN-LAST:event_bCercaActionPerformed

    /**
     * Esegue una ricerca in base ai valori impostati nel form. <br> Non
     * necessita di overriding nella classi derivate, occorre invece
     * specializzare il metodo <i>creaSelectStatement</i>.
     */
    public void eseguiQuery() {

        PreparedStatement st;
        try {
            st = creaSelectStatement();
            rs = st.executeQuery();
            modelloTabella.setRS(rs);
            rs.absolute(pos);
            mostraDati();
            setModalita(BROWSE);
        } catch (SQLException e) {
            System.out.println(query);
            mostraErrori(e, query, CONTESTO_ESEGUI_QUERY);
        } catch (java.lang.NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Cancella i dati presenti in tutti i controlli presenti sul form. <br>
     * Necessita di overriding in tutte le classi derivate di DBFrame.
     *
     */
    protected void pulisci() {
        tCodice.setText("");
    }
    private void bNuovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNuovoActionPerformed
        pulisci();
        pos = 1;
        try {
            if (rs != null) {
                rs.close();
            }
            rs = null;
            modelloTabella.setRS(rs);
            setModalita(APPEND_QUERY);
        } catch (SQLException e) {
            mostraErrori(e);
        }
    }//GEN-LAST:event_bNuovoActionPerformed

    /**
     * Crea lo statement di inserimento. <br> Necessita di overriding in tutte
     * le classi derivate di DBFrame.
     *
     */
    abstract protected PreparedStatement getComandoInserimento(Connection c)
            throws SQLException, Exception;

    /**
     * Crea lo statement di aggiornamento. <br> Necessita di overriding in tutte
     * le classi derivate di DBFrame.
     *
     */
    abstract protected PreparedStatement getComandoAggiornamento(Connection c)
            throws SQLException, Exception;

    private void bSalvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSalvaActionPerformed
        //String query;
        PreparedStatement st;
        boolean ret;
        Connection c = null;
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            try {
                c = Database.getDefaultConnection();
                if (modalita == APPEND_QUERY) {
                    st = getComandoInserimento(c);
                } else {
                    st = getComandoAggiornamento(c);
                }
                ret = false;
                c.setAutoCommit(false);
                ret = st.executeUpdate() >= 0;
                if (ret) {
                    ret = eseguiSalva(c);
                }
                if (ret) {
                    c.commit();
                } else {
                    c.rollback();
                }
                c.setAutoCommit(true);
            } catch (SQLException e) {
                mostraErrori(e);
                ret = false;
            } catch (Exception e) {
                mostraErrori(e);
                ret = false;
            }

            if (ret) {
                if (modalita == APPEND_QUERY) {
                    bCercaActionPerformed(evt);
                } else {
                    eseguiQuery();
                }
            } else {
                try {
                    c.rollback();
                    c.setAutoCommit(true);
                } catch (SQLException e) {
                    mostraErrori(e);
                    ret = false;
                }
            }
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_bSalvaActionPerformed

    /**
     * Esegue le operazioni collaterali al salvataggio del record principale, ad
     * esempio tutti i dati nelle relazioni.
     *
     */
    protected boolean eseguiSalva(Connection con) {
        return true;
    }

    private boolean eseguiComando(String cmd) {
        return eseguiComando(cmd, null);
    }

    private boolean eseguiComando(String cmd, Connection c) {
        Statement s;
        Connection mycon = null;
        try {
            if (c == null) {
                mycon = Database.getDefaultConnection();
            } else {
                mycon = c;
            }
            s = mycon.createStatement();
            s.execute(cmd);
        } catch (SQLException e) {
            mostraErrori(e);
            return false;
        } finally {
            if (c == null) {
                try {
                    mycon.close();
                } catch (SQLException e) {
                    mostraErrori(e);
                }
            }
        }
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAnnulla;
    private javax.swing.JButton bApri;
    private javax.swing.JButton bCerca;
    private javax.swing.JButton bElimina;
    private javax.swing.JButton bNuovo;
    private javax.swing.JButton bOk;
    private javax.swing.JButton bPrecedente;
    private javax.swing.JButton bPrimo;
    private javax.swing.JButton bSalva;
    private javax.swing.JButton bSuccessivo;
    private javax.swing.JButton bUltimo;
    private it.unina.lbd.lbd2012.DBTableModel dBTableModel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField tCodice;
    // End of variables declaration//GEN-END:variables
}
