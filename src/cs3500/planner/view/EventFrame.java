package cs3500.planner.view;

import java.awt.*;
import java.time.format.DateTimeFormatter;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;

import cs3500.planner.model.CentralSystem;
import cs3500.planner.model.Event;

public class EventFrame extends JFrame implements EventView {
  private CentralSystem centralSystem;
  private CentralSystemFrame frame;

  private JTextField eventNameField;
  private JTextField eventLocationField;
  private JCheckBox isOnlineCheckbox;
  private JComboBox<String> startingDayComboBox;
  private JComboBox<String> endingDayComboBox;
  private JTextField startingTimeField;
  private JTextField endingTimeField;
  private JList<String> userList;
  private JButton removeButton;
  private JButton modifyButton;

  public EventFrame(CentralSystem centralSystem) {
    super("Event Planner");
    this.centralSystem = centralSystem;
    removeButton = new JButton("Remove Event");
    modifyButton = new JButton("Modify Event");
    modifyButton.addActionListener(e -> modifyEvent());
    removeButton.addActionListener(e -> removeEvent());
    initializeComponents();
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.pack();
    this.setVisible(true);
  }

  private void initializeComponents() {
    this.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(5, 5, 5, 5);
    //Event Name
    eventNameField = new JTextField();
    constraints.gridx = 0;
    constraints.gridy = 0;
    this.add(new JLabel("Event Name:"), constraints);
    constraints.gridx = 1;
    constraints.gridwidth = 1;
    this.add(eventNameField, constraints);
    //Location Label
    eventLocationField = new JTextField();
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    this.add(new JLabel("Location:"), constraints);
    constraints.gridx = 1;
    constraints.gridwidth = 1;
    this.add(eventLocationField, constraints);
    //Online Checkbox
    isOnlineCheckbox = new JCheckBox("Is Online");
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 4; // Span two columns
    this.add(isOnlineCheckbox, constraints);
    //Starting Day ComboBox
    String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    startingDayComboBox = new JComboBox<>(daysOfWeek);
    constraints.gridx = 0;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    this.add(new JLabel("Starting Day:"), constraints);
    constraints.gridx = 0;
    constraints.gridy = 4;
    this.add(startingDayComboBox, constraints);
    //Starting Time Field
    startingTimeField = new JTextField();
    constraints.gridx = 0;
    constraints.gridy = 5;
    this.add(new JLabel("Starting Time:"), constraints);
    constraints.gridy = 6;
    this.add(startingTimeField, constraints);
    //Ending Day ComboBox
    endingDayComboBox = new JComboBox<>(daysOfWeek);
    constraints.gridx = 1;
    constraints.gridy = 3;
    this.add(new JLabel("Ending Day:"), constraints);
    constraints.gridx = 4;
    this.add(endingDayComboBox, constraints);
    constraints.gridx = 1;
    constraints.gridy = 4;
    this.add(endingDayComboBox, constraints);
    //Ending Time Field
    endingTimeField = new JTextField();
    constraints.gridx = 1;
    constraints.gridy = 5;
    this.add(new JLabel("Ending Time:"), constraints);
    constraints.gridy = 6;
    this.add(endingTimeField, constraints);
    //Users List
    userList = new JList<>(new DefaultListModel<>());
    userList.setVisibleRowCount(4);
    JScrollPane userListScrollPane = new JScrollPane(userList);
    constraints.gridx = 0;
    constraints.gridy = 8;
    constraints.gridwidth = 3; // Span four columns for list
    constraints.fill = GridBagConstraints.BOTH;
    constraints.weightx = 1.0;
    constraints.weighty = 1.0;
    this.add(userListScrollPane, constraints);
    constraints.gridx = 0;
    constraints.gridy = 7;
    constraints.gridwidth = 2;
    this.add(new JLabel("Available Users:"), constraints);
    //Reset
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.weighty = 0;
    //Modify Button
    constraints.gridx = 1;
    constraints.gridy = 9;
    constraints.gridwidth = 1;
    this.add(modifyButton, constraints);
    //Remove Button
    constraints.gridx = 0;
    constraints.gridy = 9;
    constraints.gridwidth = 1;
    this.add(removeButton, constraints);
  }

  //need to implement this
  private void modifyEvent() {
    if (validateInput()) {
      String eventDetails = getEventDetails();
      System.out.println("Create event: " + eventDetails);
    } else {
      displayError("Error: Missing event information for modification.");
    }
  }

  //need to implement this
  private void removeEvent() {
    if (validateInput()) {
      String eventDetails = getEventDetails();
      System.out.println("Remove event: " + eventDetails);
    } else {
      displayError("Error: Missing event information for removal.");
    }
  }

  private boolean validateInput() {
    return !eventNameField.getText().trim().isEmpty() &&
            !startingTimeField.getText().trim().isEmpty() &&
            !endingTimeField.getText().trim().isEmpty();
  }

  private String getEventDetails() {
    String result = "";
    result += eventNameField.getText().trim() + " ";
    result += eventLocationField.getText().trim() + " ";
    result += isOnlineCheckbox.isSelected() + " ";
    result += startingDayComboBox.getSelectedItem() + " ";
    result += startingTimeField.getText().trim() + " ";
    result += endingDayComboBox.getSelectedItem() + " ";
    result += endingTimeField.getText().trim();
    return result;
  }
//
//  @Override
//  public void clearForm() {
//    // Clear all input fields and selections
//  }

  @Override
  public void setEventDetails(Event event) {
    eventNameField.setText(event.getName());
    eventLocationField.setText(event.getLocation());
    isOnlineCheckbox.setSelected(event.isOnline());
    startingDayComboBox.setSelectedItem(event.getStartTime().getDayOfWeek().toString());
    startingTimeField.setText(event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    endingDayComboBox.setSelectedItem(event.getEndTime().getDayOfWeek().toString());
    endingTimeField.setText(event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));

    // Populate the users list
    DefaultListModel<String> listModel = new DefaultListModel<>();
    for (String user : event.getInvitees()) {
      listModel.addElement(user);
    }
    userList.setModel(listModel);
    userList.setSelectedValue(event.getHostId(), true);
  }

  @Override
  public void displayError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

}
