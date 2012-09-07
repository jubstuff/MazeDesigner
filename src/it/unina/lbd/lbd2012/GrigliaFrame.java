/*
 * GrigliaFrame.java
 * 
 */
package it.unina.lbd.lbd2012;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Giustino Borzacchiello - Raffaele Capasso
 */
public class GrigliaFrame extends javax.swing.JFrame {

    private ResultSet rs;
    Griglia gridPanel;
    private int codiceLabirinto = -1;
    private int righe = 0;
    private int colonne = 0;
    final static public int MAZE_NOT_FOUND = 0;

    /**
     * Creates new form GrigliaFrame
     */
    public GrigliaFrame() {
        initComponents();
        gridContainer.setLayout(new GridLayout(1, 1));
    }

    public GrigliaFrame(int codiceLabirinto) {
        initComponents();
        this.setTitle("Modifica labirinto");
        gridContainer.setLayout(new GridLayout(1, 1));
        this.codiceLabirinto = codiceLabirinto;
        visualizzaGriglia();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        tLabirintoNome = new javax.swing.JTextField();
        gridContainer = new javax.swing.JPanel();
        bSalvaLabirinto = new javax.swing.JButton();
        bAnnulla = new javax.swing.JButton();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel5.setText("Labirinto ");

        tLabirintoNome.setEditable(false);

        javax.swing.GroupLayout gridContainerLayout = new javax.swing.GroupLayout(gridContainer);
        gridContainer.setLayout(gridContainerLayout);
        gridContainerLayout.setHorizontalGroup(
            gridContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        gridContainerLayout.setVerticalGroup(
            gridContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        bSalvaLabirinto.setText("Salva");
        bSalvaLabirinto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSalvaLabirintoActionPerformed(evt);
            }
        });

        bAnnulla.setText("Annulla");
        bAnnulla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAnnullaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tLabirintoNome, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bSalvaLabirinto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bAnnulla)
                .addContainerGap(224, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(gridContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bSalvaLabirinto)
                        .addComponent(bAnnulla))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tLabirintoNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {bAnnulla, bSalvaLabirinto, jLabel5, tLabirintoNome});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bSalvaLabirintoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSalvaLabirintoActionPerformed

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            gridPanel.saveGrid();
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Errore",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_bSalvaLabirintoActionPerformed

    private void bAnnullaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAnnullaActionPerformed
        dispose();
    }//GEN-LAST:event_bAnnullaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAnnulla;
    private javax.swing.JButton bSalvaLabirinto;
    private javax.swing.JPanel gridContainer;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField tLabirintoNome;
    // End of variables declaration//GEN-END:variables

    private void visualizzaGriglia() {
        Object o;
        Connection con;

        o = Database.leggiValore("select nome from labirinto where "
                + "codice=?", codiceLabirinto);
        if (o != null) {
            tLabirintoNome.setText(o.toString());
            try {
                String query = "SELECT DimX, DimY FROM labirinto where codice = ?";
                con = Database.getDefaultConnection();
                PreparedStatement st = con.prepareStatement(query,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                st.setInt(1, codiceLabirinto);
                rs = st.executeQuery();

                while (rs.next()) {
                    colonne = rs.getInt("DimX");
                    righe = rs.getInt("DimY");
                    System.out.println("[" + colonne + ":" + righe + "]");
                }

            } catch (SQLException e) {
            }

            //Crea griglia labirinto
            gridPanel = new Griglia(colonne, righe, codiceLabirinto);
            //rimuovi il labirinto (eventualmente) caricato prima
            gridContainer.removeAll();
            gridContainer.add(gridPanel);
            pack();

        }
    }
}
