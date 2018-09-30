package tk.androidtechnical.rohanmachinetest.model;

public class Event {

    public int id;
    public String agenda;
    public String participants;
    public String date;
    public String time;

    public Event() {
    }

    public Event(String agenda, String participants, String date, String time) {
        this.agenda = agenda;
        this.participants = participants;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
