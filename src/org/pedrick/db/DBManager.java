/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pedrick.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sqlite.SQLiteException;

/*=======================================================================
*FILE:         DBManager.java
*
*DESCRIPTION:  A simple and lightweight Manager for DB connections
*REQUIREMENTS: 
*AUTHOR:       Patrick Facco
*VERSION:      1.0
*CREATED:      20-lug-2017
*LICENSE:      GNU/GPLv3
*COPYRIGTH:    Patrick Facco 2017
*This program is free software: you can redistribute it and/or modify
*it under the terms of the GNU General Public License as published by
*the Free Software Foundation, either version 3 of the License, or
*(at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program.  If not, see <http://www.gnu.org/licenses/>.
*========================================================================
* 
*/

public class DBManager
{
    private static final String DBFILE = "/tmp/logs.db"; //in qualche modo bisogna configurare questo path
                                                    //senza perdere il senso del singleton
    
    private Connection connection;
    private Statement statement;
     
    private static final DBManager DBMANAGER = new DBManager(); 
    
    private DBManager() 
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
            System.err.println("Opened database successfully");
            connection.setAutoCommit(true);
            statement = connection.createStatement();
            String sql = "CREATE TABLE LOGS " +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "TIMESTAMP DATETIME NOT NULL, " +
                    "SEVERITY VARCHAR(16), " +
                    "SOURCE VARCHAR(256), " +
                    " MESSAGE VARCHAR(256)  NOT NULL)";            
            statement.executeUpdate(sql);
            statement.close();
        } 
        catch (ClassNotFoundException | SQLException ex)
        {
            if( ex instanceof SQLiteException)
            {
                System.err.println("Database already exists, skipping creation");
            }
            else
            {
                System.err.println("ERROR: " + ex.getMessage());
            }
        }
    }
    
    public static DBManager getInstance()
    {
        return DBMANAGER;
    }
    
    public void connectDB() throws SQLException
    {
        if(connection == null || connection.isClosed())
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
        }
    }
    
    public void disconnectDB() throws SQLException
    {
        if(connection != null && !connection.isClosed())
        {
            if(statement != null)
            {
                statement.close();
            }
            connection.close();
        }
    }
    
    public synchronized ResultSet executeQueryDB(String sql) throws SQLException
    {
        if(connection == null || connection.isClosed())
        {
            connectDB();
        }
        statement = connection.createStatement();
        return statement.executeQuery(sql);
    }
            
    public synchronized int executeUpdateDB(String sql) throws SQLException
    {
        if(connection == null || connection.isClosed())
        {
            connectDB();
        }
        statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }
    
     /**
     * Translate a ResultSet object in a JSONArray
     * @param resultset The resultset to translate
     * @return a JSONArray with the resultset label as a keys and the resultset values as a values
     * @throws SQLException if an error on the resultset occurs
     */
    public JSONArray jsonize(ResultSet resultset) throws SQLException
    {
        
        
        ResultSetMetaData md = resultset.getMetaData();
        
        int nCol = md.getColumnCount();            
        JSONObject myString = new JSONObject();
        JSONArray queryRis = new JSONArray();
        
        while(resultset.next())
        {
            for(int i = 1; i < nCol+1; i++)
            {             
                myString.put(md.getColumnName(i), resultset.getString(md.getColumnName(i)));            
            }
            queryRis.put(myString);
            myString = new JSONObject();
        }       
        return queryRis;
    }   
}
