/*
 * Database.java
 * 
 * Modified on 2012-06-14
 */
package it.unina.lbd.lbd2012;

import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;

/**
 * Classe principale di connessione al database.
 *
 * @author Massimo
 * @author ADeLuca
 */
public class Database {

   static public String host = "192.168.0.7";
   static public String servizio = "xe";
   static public int porta = 1521;
   static public String user = "labirinto";
   static public String password = "labirinto";
   static public String schema = "labirinto";
   static private OracleDataSource ods;
   static private Connection defaultConnection;

   /**
    * Creates a new instance of Database
    */
   public Database() {
   }

   /**
    * Restituisce la connessione di default al DB.
     *
    */
   static public Connection getDefaultConnection() throws SQLException {
      if (defaultConnection == null || defaultConnection.isClosed()) {
         defaultConnection = nuovaConnessione();
         System.out.println("nuova connessione");
      } else {
         System.out.println("ricicla connessione");
      }

      return defaultConnection;
   }

   /**
    * Imposta la connessione di default al DB.
     *
    */
   static public void setDefaultConnection(Connection c) throws SQLException {
      defaultConnection = c;
   }

   /**
    * Restituisce una nuova connessione al DB.
     *
    */
   static public Connection nuovaConnessione() throws SQLException {
      ods = new OracleDataSource();
      ods.setDriverType("thin");
      ods.setServerName(host);
      ods.setPortNumber(porta);
      ods.setUser(user);
      ods.setPassword(password);
      ods.setDatabaseName(servizio);
      return ods.getConnection();
   }

   /**
    * Effettua una query e restituisce il primo valore
    */
   static public Object leggiValore(String query) {
      Object ret;
      Connection con;
      Statement st;
      ResultSet rs;
      ret = null;
      try {
         con = getDefaultConnection();
         st = con.createStatement();
         rs = st.executeQuery(query);
         rs.next();
         ret = rs.getObject(1);
      } catch (SQLException e) {
      }
      return ret;
   }

   /**
    * Effettua una query e restituisce il primo valore.
    */
   static public Object leggiValore(String query, int codice) {
      Object ret;
      Connection con;
      PreparedStatement st;
      ResultSet rs;
      ret = null;
      System.out.println(query + codice);
      try {
         con = getDefaultConnection();
         st = con.prepareStatement(query);
         st.setInt(1, codice);
         rs = st.executeQuery();
         rs.next();
         ret = rs.getObject(1);
      } catch (SQLException e) {
      }
      return ret;
   }
   /*
    * Carica il driver JDBC: necessario solo con vecchi driver static { try {
    * Class.forName("oracle.jdbc.driver.OracleDriver"); } catch (Exception e) {
    * System.out.println("Qualcosa ? andato storto");
    * System.out.println(e.getMessage()); JOptionPane.showMessageDialog(null,
    * "Driver del database non trovato", "Errore", JOptionPane.ERROR_MESSAGE); }
    * }
    */
}
