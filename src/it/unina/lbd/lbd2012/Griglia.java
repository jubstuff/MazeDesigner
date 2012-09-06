package it.unina.lbd.lbd2012;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Giustino Borzacchiello - Raffaele Capasso
 */
public class Griglia extends JPanel {
    /*
     * Tipi di caselle
     */

    private final String CASELLA = "V";
    private JRadioButton casellaButton;
    private final String MURO = "M";
    private JRadioButton muroButton;
    private JRadioButton entrataButton;
    private final String ENTRATA = "E";
    private JRadioButton uscitaButton;
    private final String USCITA = "U";
    private JRadioButton specialeButton;
    private final String SPECIALE = "S";
    /**
     * Modalita' di inserimento caselle nella griglia
     */
    private String modalita = CASELLA;
    private CasellaButton[][] grid;
    private JPanel panel;
    /**
     * Codice del labirinto relativo alla griglia
     */
    private int codiceLabirinto;
    private CasellaButton entrata;
    private CasellaButton uscita;
    boolean isBozza;

    public Griglia(int width, int height, int codiceLabirinto) {
        this.codiceLabirinto = codiceLabirinto;
        isBozza = isBozza();
        createPanel(width, height);
    }

    /**
     * Crea il pannello contenente la griglia del labirinto e i controlli
     *
     * @param width
     * @param height
     */
    private void createPanel(int width, int height) {
        JPanel gridPanel = createGrid(width, height);
        JPanel controlPanel = createControlRadioButtons();

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(gridPanel);
        panel.add(controlPanel, BorderLayout.EAST);
        panel.setMinimumSize(new Dimension(400, 400));
        add(panel);
    }

    private void uscitaButtonAction(CasellaButton btn) {
        uscita = btn;
        btn.setBackground(Color.RED);
        btn.setTipo(CASELLA);
        btn.setIsUscita(true);
    }
    private void specialeButtonAction(CasellaButton btn) {
        btn.setBackground(Color.YELLOW);
        btn.setTipo(SPECIALE);
        String result;
        String messaggio = "Aggiungi una bonus/malus alla casella: (Es. -100, 45)";
        int punti = 0;
        try {
            result = JOptionPane.showInputDialog(messaggio);
            if(result != null) {
                punti = Integer.decode(result);
            } else {
                casellaButtonAction(btn);
            }
            
        } catch (NumberFormatException e) {
            String message = "Il bonus/malus deve essere un numero intero"
                    + "\n Bonus/malus impostato a 0.";
            JOptionPane.showMessageDialog(this, message, "Errore!",
                        JOptionPane.ERROR_MESSAGE);
            punti = 0;
        }
        btn.setPunteggio(punti);
    }

    private void casellaButtonAction(CasellaButton btn) {
        System.out.println("Creata casella vuota");
        btn.setBackground(Color.WHITE);
        btn.setTipo(CASELLA);
    }

    private void muroButtonAction(CasellaButton btn) {
        System.out.println("Creata casella muro");

        btn.setBackground(Color.BLACK);
        btn.setTipo(MURO);
    }

    private void entrataButtonAction(CasellaButton btn) {
        entrata = btn;
        btn.setBackground(Color.GREEN);
        btn.setTipo(CASELLA);
        btn.setIsEntrata(true);
    }

    /**
     * Crea il pannello contenente la griglia del labirinto
     *
     * @param width
     * @param height
     * @return
     */
    private JPanel createGrid(int width, int height) {
        JPanel gpanel = new JPanel();
        gpanel.setLayout(new GridLayout(height, width));

        class GridButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                gridButtonAction(event);
            }

            private void gridButtonAction(ActionEvent event) {
                CasellaButton source = (CasellaButton) event.getSource();
                int row = source.getPosX();
                int col = source.getPosY();
                String tipo = source.getTipo();
                System.out.println("Cliccato bottone griglia: [" + col + "," + row + "]");
                if (modalita.equals(CASELLA)) {
                    if (!(tipo.equals(CASELLA))) {
                        casellaButtonAction(grid[row][col]);
                    }
                } else if (modalita.equals(MURO)) {
                    if (!(tipo.equals(MURO))) {
                        muroButtonAction(grid[row][col]);
                    }
                } else if (modalita.equals(ENTRATA)) {
                    if (entrata != null) {
                        entrata.setIsEntrata(false);
                        casellaButtonAction(entrata);
                    }
                    entrataButtonAction(grid[row][col]);
                } else if (modalita.equals(USCITA)) {
                    if (uscita != null) {
                        uscita.setIsUscita(false);
                        casellaButtonAction(uscita);
                    }
                    uscitaButtonAction(grid[row][col]);
                } else if (modalita.equals(SPECIALE)) {
                    specialeButtonAction(grid[row][col]);
                }
            }
        }



        ActionListener listener = new GridButtonListener();
        buildGrid(width, height, listener, gpanel);
        return gpanel;
    }

    

    private JPanel createControlRadioButtons() {
        //creazione bottoni modalita'
        casellaButton = new JRadioButton("Casella");
        muroButton = new JRadioButton("Muro");
        entrataButton = new JRadioButton("Entrata");
        uscitaButton = new JRadioButton("Uscita");
        specialeButton = new JRadioButton("Speciale");
        //aggiunta metadati bottoni modalita'
        casellaButton.putClientProperty("tipo", CASELLA);
        muroButton.putClientProperty("tipo", MURO);
        entrataButton.putClientProperty("tipo", ENTRATA);
        uscitaButton.putClientProperty("tipo", USCITA);
        specialeButton.putClientProperty("tipo", SPECIALE);
        //listener associato ai controlli
        class ControlButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                JRadioButton source = (JRadioButton) event.getSource();
                System.out.println("Selezionato radio button " + source.getText());
                System.out.println("Tipo: " + source.getClientProperty("tipo"));

                modalita = (String) source.getClientProperty("tipo");
                System.out.println("Modalità: " + modalita);
            }
        }

        ActionListener listener = new ControlButtonListener();
        casellaButton.addActionListener(listener);
        muroButton.addActionListener(listener);
        entrataButton.addActionListener(listener);
        uscitaButton.addActionListener(listener);
        specialeButton.addActionListener(listener);
        //aggiunta dei bottoni al buttongroup
        ButtonGroup group = new ButtonGroup();
        group.add(casellaButton);
        group.add(muroButton);
        group.add(entrataButton);
        group.add(uscitaButton);
        group.add(specialeButton);
        //imposto il bottone predefinito
        casellaButton.setSelected(true);
        //aggiunta dei bottoni al pannello
        JPanel cpanel = new JPanel();
        cpanel.setLayout(new GridLayout(3, 2));
        cpanel.add(casellaButton);
        cpanel.add(muroButton);
        cpanel.add(entrataButton);
        cpanel.add(uscitaButton);
        cpanel.add(specialeButton);

        cpanel.setBorder(new TitledBorder(new EtchedBorder(), "Controlli"));

        return cpanel;
    }

    private void buildGrid(int width, int height, ActionListener listener, JPanel gpanel) {
        ResultSet rs;
        int posX;
        int posY;
        String tipo;
        int codice;
        int codiceEntrata = -1;
        int codiceUscita = -1;
        try {
            Connection con = Database.getDefaultConnection();
            if (!isBozza) {
                //Seleziona entrata e uscita
                String entrataUscitaQuery = "SELECT entrata, uscita FROM labirinto where codice = ?";
                PreparedStatement stmt = con.prepareStatement(entrataUscitaQuery,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                stmt.setInt(1, codiceLabirinto);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    codiceEntrata = rs.getInt("entrata");
                    codiceUscita = rs.getInt("uscita");
                    System.out.println("[" + codiceEntrata + ":" + codiceUscita + "]");
                }
            }

            String query = "SELECT codice, PosX, PosY, Tipo FROM Casella where labirinto = ? "
                    + "ORDER BY PosX ASC, PosY ASC";
            PreparedStatement st = con.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            st.setInt(1, codiceLabirinto);
            rs = st.executeQuery();
            grid = new CasellaButton[width][height];

            while (rs.next()) {

                posX = rs.getInt("PosX");
                posY = rs.getInt("PosY");
                tipo = rs.getString("Tipo");
                codice = rs.getInt("codice");

                grid[posX][posY] = new CasellaButton("");
                grid[posX][posY].setPreferredSize(new Dimension(20, 20));
                grid[posX][posY].setPosX(posX);
                grid[posX][posY].setPosY(posY);
                grid[posX][posY].setCodice(codice);
                setTipoCasella(grid[posX][posY], tipo);
                if (codice == codiceEntrata) {
                    entrataButtonAction(grid[posX][posY]);
                }
                if (codice == codiceUscita) {
                    uscitaButtonAction(grid[posX][posY]);
                }
                grid[posX][posY].addActionListener(listener);
                grid[posX][posY].addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        if( SwingUtilities.isRightMouseButton(evt)) {
                            rightClickAction(evt);
                        }
                    }

                    private void rightClickAction(MouseEvent evt) {
                        CasellaButton source = (CasellaButton) evt.getSource();
                        String descr = JOptionPane.showInputDialog("Aggiungi una descrizione alla casella:");
                        if (descr != null && descr.length() > 0) {
                            try {
                                Connection conn = Database.getDefaultConnection();
                                System.out.println("[" + source.getPosX() + ":" + source.getPosY() + "]: " + descr);
                                String updateQuery = "UPDATE Casella SET descrizione = ? WHERE codice = ?";
                                PreparedStatement stmt = conn.prepareStatement(updateQuery,
                                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY);
                                stmt.setString(1, descr);
                                stmt.setInt(2, source.getCodice());
                                stmt.executeUpdate();
                            } catch (SQLException e) {
                            }
                        }
                    }
                });

                gpanel.add(grid[posX][posY]);
                System.out.println(posX + ":" + posY + ":" + tipo);
            }

        } catch (SQLException e) {
            //TODO che fare qui?
        }
    }
    
    private void setTipoCasella(CasellaButton btn, String tipo) {
        btn.setTipo(tipo);
        if (tipo.equals(CASELLA)) {
            btn.setBackground(Color.white);

        } else if (tipo.equals(MURO)) {
            btn.setBackground(Color.black);
        } else if (tipo.equals(SPECIALE)) {
            btn.setBackground(Color.YELLOW);
        }
    }

    public void deleteGrid() throws SQLException {
        Connection conn = Database.getDefaultConnection();

        String deleteCollQuery = "DELETE FROM collegamento WHERE origine IN (SELECT origine FROM casella WHERE labirinto=?)";
        PreparedStatement delCollStmt = conn.prepareStatement(deleteCollQuery,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        delCollStmt.setInt(1, codiceLabirinto);
        delCollStmt.execute();
        
        String deleteSpecQuery = "DELETE FROM speciale WHERE id_speciale IN (SELECT codice FROM casella WHERE labirinto=?)";
        PreparedStatement delSpecStmt = conn.prepareStatement(deleteSpecQuery,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        delSpecStmt.setInt(1, codiceLabirinto);
        delSpecStmt.execute();
    }

    private boolean isBozza() {
        Object o;

        o = Database.leggiValore("select is_bozza from labirinto where "
                + "codice=?", codiceLabirinto);
        isBozza = o.toString().equals("F") ? false : true;
        System.out.println("Is_bozza: " + isBozza);
        return isBozza;
    }

    public void saveGrid() throws Exception {
        if (entrata != null && uscita != null) {
            if (!isBozza) {
                deleteGrid();
            }
            Connection conn = Database.getDefaultConnection();
            try {

                conn.setAutoCommit(false);
                //TODO utilizzare il codice in questa query
                String updateQuery = "UPDATE Casella SET Tipo = ? WHERE Labirinto = ? "
                        + "AND PosX = ? AND PosY = ?";
                PreparedStatement updStmt = conn.prepareStatement(updateQuery,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                
                String specialeQuery = "INSERT INTO SPECIALE(id_speciale, modificatorePunteggio) VALUES(?,?)";
                PreparedStatement specStmt = conn.prepareStatement(specialeQuery,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                
                updStmt.setInt(2, codiceLabirinto);
                String collQuery = "INSERT ALL ";
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[0].length; j++) {
                        String tipo = grid[i][j].getTipo();
                        updStmt.setString(1, tipo);
                        updStmt.setInt(3, i);
                        updStmt.setInt(4, j);
                        updStmt.executeUpdate();

                        /*
                         * Gestisci collegamenti
                         */
                        if (!(tipo.equals(MURO))) {
                            //Se non e' un muro, aggiungi le adiacenze

                            String intoQuery = "INTO Collegamento(origine,destinazione) ";

                            if (i > 0 && !((String) grid[i - 1][j].getTipo()).equals(MURO)) {
                                System.out.println("Collego " + grid[i][j].getCodice() + "->" + grid[i - 1][j].getCodice());
                                collQuery += intoQuery + "VALUES(" + grid[i][j].getCodice() + "," + grid[i - 1][j].getCodice() + ")";
                            }
                            if (i < grid.length - 1 && !((String) grid[i + 1][j].getTipo()).equals(MURO)) {
                                System.out.println("Collego " + grid[i][j].getCodice() + "->" + grid[i + 1][j].getCodice());
                                collQuery += intoQuery + "VALUES(" + grid[i][j].getCodice() + "," + grid[i + 1][j].getCodice() + ")";

                            }
                            if (j > 0 && !((String) grid[i][j - 1].getTipo()).equals(MURO)) {
                                System.out.println("Collego " + grid[i][j].getCodice() + "->" + grid[i][j - 1].getCodice());
                                collQuery += intoQuery + "VALUES(" + grid[i][j].getCodice() + "," + grid[i][j - 1].getCodice() + ")";
                            }
                            if (j < grid[0].length - 1 && !((String) grid[i][j + 1].getTipo()).equals(MURO)) {
                                System.out.println("Collego " + grid[i][j].getCodice() + "->" + grid[i][j + 1].getCodice());
                                collQuery += intoQuery + "VALUES(" + grid[i][j].getCodice() + "," + grid[i][j + 1].getCodice() + ")";
                            }
                        }
                        
                        if(tipo.equals(SPECIALE)) {
                            specStmt.setInt(1, grid[i][j].getCodice());
                            specStmt.setInt(2, grid[i][j].getPunteggio());
                            specStmt.execute();
                        }
                    }
                }
                collQuery += " SELECT * FROM DUAL";
                System.out.println(collQuery);

                Statement st = conn.createStatement();
                st.execute(collQuery);

                String updLabQuery = "UPDATE Labirinto SET entrata = ?, uscita = ?, ";
                updLabQuery += "is_bozza = 'F' WHERE codice = ? ";
                PreparedStatement stmt2 = conn.prepareStatement(updLabQuery,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                stmt2.setInt(1, entrata.getCodice());
                stmt2.setInt(2, uscita.getCodice());
                stmt2.setInt(3, codiceLabirinto);
                stmt2.executeUpdate();
                //Fare il commit delle modifiche se e' tutto OK
                conn.commit();
                JOptionPane.showMessageDialog(this, "Labirinto salvato!", "Successo!",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                String msg;

                msg = "ErrorCode= " + e.getErrorCode() + "\n";
                msg += "Message= " + e.getMessage() + "\n";
                msg += "SQLState= " + e.getSQLState() + "\n";

                System.out.println(msg);
                conn.rollback();
                throw new Exception(e.getMessage());
            }

        } else {
            throw new Exception("Bisogna scegliere entrata e uscita");
        }
    }
}