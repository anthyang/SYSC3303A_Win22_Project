import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ConsoleGUI extends JFrame {

    private JLabel elevatorOneFloorNum;
    private JLabel elevatorTwoFloorNum;
    private JLabel elevatorThreeFloorNum;
    private JLabel elevatorFourFloorNum;

    private JLabel elevatorOneDirection;
    private JLabel elevatorTwoDirection;
    private JLabel elevatorThreeDirection;
    private JLabel elevatorFourDirection;

    private JLabel elevatorOneStatus;
    private JLabel elevatorTwoStatus;
    private JLabel elevatorThreeStatus;
    private JLabel elevatorFourStatus;

    private JTextArea faultList;

    Color nardo = new Color (192, 192, 192);
    Color space = new Color (128, 128, 128);
    Color white = new Color(255, 255, 255);

    public ConsoleGUI() {
        super("Elevator Monitoring");
        this.setLayout(new GridLayout(3, 2, 2, 2));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(700, 600));
        this.setBackground(white);
        this.setResizable(false);

        JPanel elevatorOnePanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorOnePanel.setBackground(nardo);
        Border raisedBevel = BorderFactory.createRaisedBevelBorder();
        elevatorOnePanel.setBorder(raisedBevel);
        JLabel elevatorOneName = new JLabel();
        elevatorOneName.setText("           Elevator #1");
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        elevatorOneName.setBorder(blackLine);
        Font bold = new Font("Verdana", Font.BOLD, 12);
        elevatorOneName.setFont(bold);
        elevatorOneFloorNum = new JLabel();
        elevatorOneFloorNum.setText(" Floor Number: ");
        elevatorOneFloorNum.setBorder(blackLine);
        Font plain = new Font("Verdana", Font.PLAIN, 12);
        elevatorOneFloorNum.setFont(plain);
        elevatorOneDirection = new JLabel();
        elevatorOneDirection.setText(" Direction: ");
        elevatorOneDirection.setBorder(blackLine);
        elevatorOneDirection.setFont(plain);
        elevatorOneStatus = new JLabel();
        elevatorOneStatus.setText(" Status: ");
        elevatorOneStatus.setBorder(blackLine);
        elevatorOneStatus.setFont(plain);
        elevatorOnePanel.add(elevatorOneName);
        elevatorOnePanel.add(elevatorOneFloorNum);
        elevatorOnePanel.add(elevatorOneDirection);
        elevatorOnePanel.add(elevatorOneStatus);

        JPanel elevatorTwoPanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorTwoPanel.setBackground(space);
        Border loweredBevel = BorderFactory.createLoweredBevelBorder();
        elevatorTwoPanel.setBorder(loweredBevel);
        JLabel elevatorTwoName = new JLabel();
        elevatorTwoName.setText("           Elevator #2");
        elevatorTwoName.setBorder(blackLine);
        elevatorTwoName.setFont(bold);
        elevatorTwoFloorNum = new JLabel();
        elevatorTwoFloorNum.setText(" Floor Number: ");
        elevatorTwoFloorNum.setBorder(blackLine);
        elevatorTwoFloorNum.setFont(plain);
        elevatorTwoDirection = new JLabel();
        elevatorTwoDirection.setText(" Direction: ");
        elevatorTwoDirection.setBorder(blackLine);
        elevatorTwoDirection.setFont(plain);
        elevatorTwoStatus = new JLabel();
        elevatorTwoStatus.setText(" Status: ");
        elevatorTwoStatus.setBorder(blackLine);
        elevatorTwoStatus.setFont(plain);
        elevatorTwoPanel.add(elevatorTwoName);
        elevatorTwoPanel.add(elevatorTwoFloorNum);
        elevatorTwoPanel.add(elevatorTwoDirection);
        elevatorTwoPanel.add(elevatorTwoStatus);

        JPanel elevatorThreePanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorThreePanel.setBackground(space);
        elevatorThreePanel.setBorder(loweredBevel);
        JLabel elevatorThreeName = new JLabel();
        elevatorThreeName.setText("           Elevator #3");
        elevatorThreeName.setBorder(blackLine);
        elevatorThreeName.setFont(bold);
        elevatorThreeFloorNum = new JLabel();
        elevatorThreeFloorNum.setText(" Floor Number: ");
        elevatorThreeFloorNum.setBorder(blackLine);
        elevatorThreeFloorNum.setFont(plain);
        elevatorThreeDirection = new JLabel();
        elevatorThreeDirection.setText(" Direction: ");
        elevatorThreeDirection.setBorder(blackLine);
        elevatorThreeDirection.setFont(plain);
        elevatorThreeStatus = new JLabel();
        elevatorThreeStatus.setText(" Status: ");
        elevatorThreeStatus.setBorder(blackLine);
        elevatorThreeStatus.setFont(plain);
        elevatorThreePanel.add(elevatorThreeName);
        elevatorThreePanel.add(elevatorThreeFloorNum);
        elevatorThreePanel.add(elevatorThreeDirection);
        elevatorThreePanel.add(elevatorThreeStatus);

        JPanel elevatorFourPanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorFourPanel.setBackground(nardo);
        elevatorFourPanel.setBorder(raisedBevel);
        JLabel elevatorFourName = new JLabel();
        elevatorFourName.setText("           Elevator #4");
        elevatorFourName.setBorder(blackLine);
        elevatorFourName.setFont(bold);
        elevatorFourFloorNum = new JLabel();
        elevatorFourFloorNum.setText(" Floor Number: ");
        elevatorFourFloorNum.setBorder(blackLine);
        elevatorFourFloorNum.setFont(plain);
        elevatorFourDirection = new JLabel();
        elevatorFourDirection.setText(" Direction: ");
        elevatorFourDirection.setBorder(blackLine);
        elevatorFourDirection.setFont(plain);
        elevatorFourStatus = new JLabel();
        elevatorFourStatus.setText(" Status: ");
        elevatorFourStatus.setBorder(blackLine);
        elevatorFourStatus.setFont(plain);
        elevatorFourPanel.add(elevatorFourName);
        elevatorFourPanel.add(elevatorFourFloorNum);
        elevatorFourPanel.add(elevatorFourDirection);
        elevatorFourPanel.add(elevatorFourStatus);

        JLabel faultListText = new JLabel();
        faultListText.setText("Fault List: ");
        faultListText.setHorizontalAlignment(JLabel.CENTER);
        faultListText.setBackground(nardo);
        faultListText.setBorder(raisedBevel);
        faultListText.setFont(bold);
        faultListText.setMinimumSize(new Dimension(350, 200));

        faultList = new JTextArea("No faults");
        JScrollPane faultScrollPane = new JScrollPane(faultList);
        faultScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        faultScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        faultScrollPane.setMinimumSize(new Dimension(350, 200));

        this.add(elevatorOnePanel);
        this.add(elevatorTwoPanel);
        this.add(elevatorThreePanel);
        this.add(elevatorFourPanel);
        this.add(faultListText);
        this.add(faultScrollPane);

        this.setVisible(true);
    }

    public void displayFaults(String faults) {
        faultList.setText(faults);
    }

    public void displayElevatorPos(int elevatorID, int positionNum) {
        switch(elevatorID) {
            case(1):
                elevatorOneFloorNum.setText(" Floor Number: " + positionNum);
            case(2):
                elevatorTwoFloorNum.setText(" Floor Number: " + positionNum);
            case(3):
                elevatorThreeFloorNum.setText(" Floor Number: " + positionNum);
            case(4):
                elevatorFourFloorNum.setText(" Floor Number: " + positionNum);
        }
    }

    public void displayElevatorDirection(int elevatorID, Direction dir) {
        switch(elevatorID) {
            case(1):
                elevatorOneDirection.setText(" Direction: " + dir.toString());
            case(2):
                elevatorTwoDirection.setText(" Direction: " + dir.toString());
            case(3):
                elevatorThreeDirection.setText(" Direction: " + dir.toString());
            case(4):
                elevatorFourDirection.setText(" Direction: " + dir.toString());
        }
    }

    public void displayElevatorStatus(int elevatorID, String status) {
        switch(elevatorID) {
            case(1):
                elevatorOneStatus.setText(" Status: " + status);
            case(2):
                elevatorTwoStatus.setText(" Status: " + status);
            case(3):
                elevatorThreeStatus.setText(" Status: " + status);
            case(4):
                elevatorFourStatus.setText(" Status " + status);
        }
    }

    public static void main(String[] args) {
        ConsoleGUI gui = new ConsoleGUI();

        BlockingDeque<Request> master = new LinkedBlockingDeque<>();
        BlockingDeque<Integer> reqsToServe = new LinkedBlockingDeque<>();
        Map<Integer, ElevatorStatus> elevators = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));

        Scheduler elevScheduler = new Scheduler(master, reqsToServe, elevators, true, false, gui);
        Scheduler reqScheduler = new Scheduler(master, reqsToServe, elevators, true, true, gui);
        Scheduler floorScheduler = new Scheduler(master, reqsToServe, elevators, false, false, gui);

        Thread elevSch = new Thread(elevScheduler);
        Thread reqSch = new Thread(reqScheduler);
        Thread floorSch = new Thread(floorScheduler);

        elevSch.start();
        reqSch.start();
        floorSch.start();
    }
}
