import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GUI class for Project.
 */
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

    private ArrayList<JCheckBox> elevLamps;

    private JTextArea faultList;

    private static final ImageIcon OFF = new ImageIcon("resources/off.png");
    private static final ImageIcon ON = new ImageIcon("resources/on.png");

    Color nardo = new Color (192, 192, 192);
    Color white = new Color(255, 255, 255);

    /**
     * Constructor to initialize Swing components.
     */
    public ConsoleGUI() {
        super("Elevator Monitoring");
        this.setLayout(new GridLayout(4, 2, 2, 2));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(700, 600));
        this.setBackground(white);
        this.setResizable(false);

        /*
        Elevator One Control Panel
         */
        JPanel elevatorOnePanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorOnePanel.setBackground(white);
        Border raisedBevel = BorderFactory.createRaisedBevelBorder();
        elevatorOnePanel.setBorder(raisedBevel);
        JLabel elevatorOneName = new JLabel();
        elevatorOneName.setText("        Elevator #1");
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        elevatorOneName.setBorder(blackLine);
        Font bold = new Font("Verdana", Font.BOLD, 14);
        elevatorOneName.setFont(bold);
        elevatorOneFloorNum = new JLabel();
        elevatorOneFloorNum.setText(" Floor Number: ");
        elevatorOneFloorNum.setBorder(blackLine);
        Font plain = new Font("Verdana", Font.PLAIN, 14);
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

        /*
        Elevator Two Control Panel
         */
        JPanel elevatorTwoPanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorTwoPanel.setBackground(nardo);
        Border loweredBevel = BorderFactory.createLoweredBevelBorder();
        elevatorTwoPanel.setBorder(loweredBevel);
        JLabel elevatorTwoName = new JLabel();
        elevatorTwoName.setText("        Elevator #2");
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

        /*
        Elevator Three Control Panel
         */
        JPanel elevatorThreePanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorThreePanel.setBackground(nardo);
        elevatorThreePanel.setBorder(loweredBevel);
        JLabel elevatorThreeName = new JLabel();
        elevatorThreeName.setText("        Elevator #3");
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

        /*
        Elevator Four Control Panel
         */
        JPanel elevatorFourPanel = new JPanel(new GridLayout(2, 2, 1, 1));
        elevatorFourPanel.setBackground(white);
        elevatorFourPanel.setBorder(raisedBevel);
        JLabel elevatorFourName = new JLabel();
        elevatorFourName.setText("        Elevator #4");
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

        /*
        Fault List Text Label
         */
        JLabel faultListText = new JLabel();
        faultListText.setText("Fault List: ");
        faultListText.setHorizontalAlignment(JLabel.CENTER);
        faultListText.setBackground(white);
        faultListText.setBorder(raisedBevel);
        faultListText.setFont(bold);
        faultListText.setMinimumSize(new Dimension(350, 200));

        /*
        Fault List Scrollable Pane
         */
        faultList = new JTextArea("No faults");
        JScrollPane faultScrollPane = new JScrollPane(faultList);
        faultScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        faultScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        faultScrollPane.setMinimumSize(new Dimension(350, 200));
        
        /*
         * Elevator lamps.
         */
        JPanel lampSectionOne = new JPanel(new GridLayout(1, 2, 2, 2));
        lampSectionOne.setBorder(loweredBevel);
        JPanel lampSectionTwo = new JPanel(new GridLayout(1, 2, 2, 2));
        lampSectionTwo.setBorder(raisedBevel);
        JPanel lampElevOne = new JPanel(new GridLayout(8, 3, 1, 1));
        lampElevOne.setBackground(nardo);
        lampElevOne.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Elevator One Lamps", TitledBorder.CENTER, TitledBorder.TOP));
        JPanel lampElevTwo = new JPanel(new GridLayout(8, 3, 1, 1));
        lampElevTwo.setBackground(nardo);
        lampElevTwo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Elevator Two Lamps", TitledBorder.CENTER, TitledBorder.TOP));
        JPanel lampElevThree = new JPanel(new GridLayout(8, 3, 1, 1));
        lampElevThree.setBackground(white);
        lampElevThree.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Elevator Three Lamps", TitledBorder.CENTER, TitledBorder.TOP));
        JPanel lampElevFour = new JPanel(new GridLayout(8, 3, 1, 1));
        lampElevFour.setBackground(white);
        lampElevFour.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Elevator Four Lamps", TitledBorder.CENTER, TitledBorder.TOP));
        elevLamps = new ArrayList<>();
        for(int i = 0; i < (Config.NUMBER_OF_ELEVATORS * Config.NUMBER_OF_FLOORS); i++) {
        	elevLamps.add(new JCheckBox("" + ((i % Config.NUMBER_OF_FLOORS) + 1)));
        	elevLamps.get(i).setSelected(false);
        	elevLamps.get(i).setOpaque(false);
        	elevLamps.get(i).setIcon(OFF);
        	switch(i / Config.NUMBER_OF_FLOORS) {
        		case(0):
        			lampElevOne.add(elevLamps.get(i));
        			break;
        		case(1):
        			lampElevTwo.add(elevLamps.get(i));
        			break;
        		case(2):
        			lampElevThree.add(elevLamps.get(i));
        			break;
        		case(3):
        			lampElevFour.add(elevLamps.get(i));
        			break;
        	}
        }
                
        lampSectionOne.add(lampElevOne);
        lampSectionOne.add(lampElevTwo);
        lampSectionTwo.add(lampElevThree);
        lampSectionTwo.add(lampElevFour);
    
        
        this.add(elevatorOnePanel);
        this.add(elevatorTwoPanel);
        this.add(elevatorThreePanel);
        this.add(elevatorFourPanel);
        this.add(faultListText);
        this.add(faultScrollPane);
        this.add(lampSectionOne);
        this.add(lampSectionTwo);
    }

    /*
    Display updated faults.
     */
    public void displayFaults(String faults) {
        faultList.setText(faults);
    }

    /*
    Display updated elevator floor position.
     */
    public void displayElevatorPos(int elevatorID, int positionNum) {
        switch(elevatorID) {
            case(1):
                elevatorOneFloorNum.setText(" Floor Number: " + positionNum);
                break;
            case(2):
                elevatorTwoFloorNum.setText(" Floor Number: " + positionNum);
                break;
            case(3):
                elevatorThreeFloorNum.setText(" Floor Number: " + positionNum);
                break;
            case(4):
                elevatorFourFloorNum.setText(" Floor Number: " + positionNum);
                break;
        }
    }

    /*
    Display updated elevator direction.
     */
    public void displayElevatorDirection(int elevatorID, Direction dir) {
        switch(elevatorID) {
            case(1):
                elevatorOneDirection.setText("<html> Direction: <br/>" + dir.toString() + "</html>");
                break;
            case(2):
                elevatorTwoDirection.setText("<html> Direction: <br/>" + dir.toString() + "</html>");
                break;
            case(3):
                elevatorThreeDirection.setText("<html> Direction: <br/>" + dir.toString() + "</html>");
                break;
            case(4):
                elevatorFourDirection.setText("<html> Direction: <br/>" + dir.toString() + "</html>");
                break;
        }
    }

    /*
    Display updated elevator status.
     */
    public void displayElevatorStatus(int elevatorID, boolean status) {
        String statusStr;
        if(status) {
            statusStr = "Active";
        } else {
            statusStr = "Inactive";
        }
        switch(elevatorID) {
            case(1):
                elevatorOneStatus.setText(" Status: " + statusStr);
                break;
            case(2):
                elevatorTwoStatus.setText(" Status: " + statusStr);
                break;
            case(3):
                elevatorThreeStatus.setText(" Status: " + statusStr);
                break;
            case(4):
                elevatorFourStatus.setText(" Status: " + statusStr);
                break;
        }
    }

    public void updateLamps(int elevId, List<Request> requests) {
        Set<Integer> pressedButtons = requests.stream().filter(Request::isPickedUp).map(Request::getDestFloor).collect(Collectors.toSet());
        for (int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
            ImageIcon icon = pressedButtons.contains(i + 1) ? ON : OFF;
            elevLamps.get(i + (elevId - 1) * Config.NUMBER_OF_FLOORS).setIcon(icon);
        }
    }

    /*
    Set GUI frame to visible.
     */
    private void display() {
        this.setVisible(true);
    }

    public static void main(String[] args) {
        ConsoleGUI gui = new ConsoleGUI();
        gui.display();

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
