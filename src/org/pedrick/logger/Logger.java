package org.pedrick.logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.pedrick.db.DBManager;
import org.pedrick.db.Severity;

public class Logger
{   
    public static final long DAY_MILLISECONDS = 86400000;
    private static final Logger LOGGER = new Logger();
    private final DBManager dbmanager;
    private long retention;
    
    private Logger()
    {
        dbmanager = DBManager.getInstance();
        retention = 0;
    }
    
    public static Logger getInstance()
    {
        return LOGGER;
    }

    public long getRetention()
    {
        return retention / DAY_MILLISECONDS;
    }

    public void setRetention(int retention_days)
    {
        this.retention = retention_days * DAY_MILLISECONDS;
    }
    
    
    
    /**
     * Method to write new log on database
     * @param timestamp timestamp of the event
     * @param source the generator source
     * @param severity the severity of the event
     * @param message the message to show
     * @return true if log is successfully written, false otherwise
     */
    public synchronized boolean writeLog(Long timestamp, String source, Severity severity, String message)
    {
        try
        {
            dbmanager.connectDB();
            if(retention > 0)
            {
                String remove_sql = "delete from logs where timestamp < " + (timestamp - retention);               
                int rows_deleted = dbmanager.executeUpdateDB(remove_sql);
                System.err.println("Deleted " + rows_deleted + " due to retention policy.");
            }
            
            String sql = "insert into logs(timestamp,source, severity,message) "
                    + "values('" + timestamp + "', "
                    + "'" + source + "', "
                    + "'" + severity + "','"
                    + message + "')";
           
            dbmanager.executeUpdateDB(sql);
            dbmanager.disconnectDB();
            return true;
        } 
        catch (SQLException ex)
        {          
            return false;
        }
    }
    
    //metrodo per restituire i log con condizione
    public synchronized JSONArray readLog(String condition)    
    {
        JSONArray result = new JSONArray();
        try
        {
            dbmanager.connectDB();
            String sql = "select * from logs";
            if(condition != null)
            {
                sql += " where " + condition;
            }
            ResultSet rs = dbmanager.executeQueryDB(sql);
            result = dbmanager.jsonize(rs);
            dbmanager.disconnectDB();
            return result;
        } 
        catch (SQLException ex)
        {   System.err.println(ex);
            return result;
        }        
    }
    
}
