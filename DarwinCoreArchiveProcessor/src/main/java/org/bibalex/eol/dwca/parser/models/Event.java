package org.bibalex.eol.dwca.parser.models;

public class Event {
    String eventId;
    String locality;
    String remarks;

    public Event(String eventId, String locality, String remarks) {
        this.eventId = eventId;
        this.locality = locality;
        this.remarks = remarks;
    }

    public String getEventId() {

        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
