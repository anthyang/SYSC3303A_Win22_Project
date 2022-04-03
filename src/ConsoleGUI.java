import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ConsoleGUI extends JFrame {

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
        JLabel elevatorOneFloorNum = new JLabel();
        elevatorOneFloorNum.setText(" Floor Number: ");
        elevatorOneFloorNum.setBorder(blackLine);
        Font plain = new Font("Verdana", Font.PLAIN, 12);
        elevatorOneFloorNum.setFont(plain);
        JLabel elevatorOneDirection = new JLabel();
        elevatorOneDirection.setText(" Direction: ");
        elevatorOneDirection.setBorder(blackLine);
        elevatorOneDirection.setFont(plain);
        JLabel elevatorOneStatus = new JLabel();
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
        JLabel elevatorTwoFloorNum = new JLabel();
        elevatorTwoFloorNum.setText(" Floor Number: ");
        elevatorTwoFloorNum.setBorder(blackLine);
        elevatorTwoFloorNum.setFont(plain);
        JLabel elevatorTwoDirection = new JLabel();
        elevatorTwoDirection.setText(" Direction: ");
        elevatorTwoDirection.setBorder(blackLine);
        elevatorTwoDirection.setFont(plain);
        JLabel elevatorTwoStatus = new JLabel();
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
        JLabel elevatorThreeFloorNum = new JLabel();
        elevatorThreeFloorNum.setText(" Floor Number: ");
        elevatorThreeFloorNum.setBorder(blackLine);
        elevatorThreeFloorNum.setFont(plain);
        JLabel elevatorThreeDirection = new JLabel();
        elevatorThreeDirection.setText(" Direction: ");
        elevatorThreeDirection.setBorder(blackLine);
        elevatorThreeDirection.setFont(plain);
        JLabel elevatorThreeStatus = new JLabel();
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
        JLabel elevatorFourFloorNum = new JLabel();
        elevatorFourFloorNum.setText(" Floor Number: ");
        elevatorFourFloorNum.setBorder(blackLine);
        elevatorFourFloorNum.setFont(plain);
        JLabel elevatorFourDirection = new JLabel();
        elevatorFourDirection.setText(" Direction: ");
        elevatorFourDirection.setBorder(blackLine);
        elevatorFourDirection.setFont(plain);
        JLabel elevatorFourStatus = new JLabel();
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

        JScrollPane faultScrollPane = new JScrollPane(faultListText);
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

    public static void main(String[] args) {
        ConsoleGUI gui = new ConsoleGUI();
    }
}
