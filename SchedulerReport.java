
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SchedulerReport implements Serializable {
	
	private Elevator e;
	
    public SchedulerReport(int id) {
    	this.e = new Elevator(id);
    }

    public byte[] serialize() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(byteOut);
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    public static Request deserialize(byte[] serializedData) {
        Request report;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(serializedData);

        try {
            ObjectInputStream ois = new ObjectInputStream(byteIn);
            report = (Request) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return report;
    }
    
    public static ElevatorReport deserializeData(byte[] serializedData) {
        ElevatorReport report;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(serializedData);

        try {
            ObjectInputStream ois = new ObjectInputStream(byteIn);
            report =  (ElevatorReport) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return report;
    }
    
    public Elevator getElevator() {
    	return e;
    }
}