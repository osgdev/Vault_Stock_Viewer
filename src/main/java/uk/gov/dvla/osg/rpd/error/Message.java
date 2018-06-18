package uk.gov.dvla.osg.rpd.error;

public class Message {
    private String message;

    private String time;

    private String name;

    private String action;

    private String severity;

    private String code;

    public String getMessage ()
    {
        return message;
    }

    public Message() {
        super(); // Default constructor required for Jackson Xml Parser
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getAction ()
    {
        return action;
    }

    public void setAction (String action)
    {
        this.action = action;
    }

    public String getSeverity ()
    {
        return severity;
    }

    public void setSeverity (String severity)
    {
        this.severity = severity;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", time = "+time+", name = "+name+", action = "+action+", severity = "+severity+", code = "+code+"]";
    }
}
            
            