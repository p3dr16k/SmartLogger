package org.pedrick.db;


public enum Severity
{
    INFO, WARNING, ERROR, GRAVE;
    
    @Override    
    public String toString()
    {
        switch(this)
        {
            case INFO:
                return "info";
            case WARNING:
                return "warning";
            case ERROR:
                return "error";
            case GRAVE:
                return "grave";
            default:
                return "N/A";
        }
    }
}
