package uk.gov.dvla.osg.rpd.error;

public class BadResponseModelXml {
    private Message message;

    public BadResponseModelXml(String errorMessage, String errorAction) {
        message = new Message();
        message.setMessage(errorMessage);
        message.setAction(errorAction);
    }

    public BadResponseModelXml() {
        super(); // Default constructor required for Jackson Xml Parser
    }

    public Message getMessage ()
    {
        return message;
    }

    public void setMessage (Message message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+"]";
    }
}
            
    