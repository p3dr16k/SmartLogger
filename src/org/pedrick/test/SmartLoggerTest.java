package org.pedrick.test;

import java.sql.SQLException;
import java.util.Date;
import org.json.JSONArray;
import org.pedrick.db.Severity;
import org.pedrick.logger.Logger;

public class SmartLoggerTest
{
    public static void main(String[] args) throws SQLException
    {   
        System.out.println(new Date());

        Logger logger = Logger.getInstance();        
        boolean ris = logger.writeLog(new Date().getTime(), "patrick", Severity.ERROR, "test log");
        System.out.println("RIS: " + ris);
        
        logger.setRetention(1);
        System.out.println("RETENTION IS " + logger.getRetention() + " DAYS.");
        ris = logger.writeLog(new Date().getTime(), "patrick", Severity.ERROR, "test log");
        System.out.println("RETENTION RIS: " + ris);
        
        JSONArray query_ris = logger.readLog("severity is null");
        System.out.println("QUERY RIS: " + query_ris);

        System.out.println(new Date());
    }
}
