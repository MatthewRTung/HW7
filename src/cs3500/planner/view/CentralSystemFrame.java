package cs3500.planner.view;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Objects;

import cs3500.planner.model.CentralSystem;
import cs3500.planner.model.Event;
import cs3500.planner.xml.XMLConfigurator;

public class CentralSystemFrame extends JFrame implements CentralSystemView {
  private JPanel schedulePanel;
  private JComboBox<String> userDropDown;
  private JButton loadButton;
  private JButton saveButton;
  private final List<Event> events;
  private final CentralSystem model;

  public CentralSystemFrame(CentralSystem model) {
    super("Planner Central System");
    this.model = model;
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());
    events = new ArrayList<>();

    initializeMenu();
    initializeSchedulePanel();
    initializeControlPanel();

    this.pack();
    this.setVisible(true);
  }

  private void initializeMenu() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");

    JMenuItem loadMenuItem = new JMenuItem("Add calendar");
    loadMenuItem.addActionListener(this::loadXMLAction);

    JMenuItem saveMenuItem = new JMenuItem("Save calendars");
    saveMenuItem.addActionListener(this::saveSchedulesAction);

    fileMenu.add(loadMenuItem);
    fileMenu.add(saveMenuItem);

    menuBar.add(fileMenu);
    this.setJMenuBar(menuBar);
  }

  private void initializeSchedulePanel() {
    schedulePanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawEvents(g);
      }
    };
    schedulePanel.setPreferredSize(new Dimension(800, 600));
    this.add(schedulePanel, BorderLayout.CENTER);
  }

  private void drawGrid(Graphics g) {
    g.setColor(Color.LIGHT_GRAY);
    int rows = 24;
    int cols = 7;
    int cellWidth = schedulePanel.getWidth() / cols;
    int cellHeight = schedulePanel.getHeight() / rows;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        g.drawRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
      }
      if (i % 4 == 0) {
        g.setColor(Color.BLACK);
        g.fillRect(0, i * cellHeight - 1, schedulePanel.getWidth(), 2);
      }
    }
  }

  private void drawEvents(Graphics g) {
    int cellWidth = schedulePanel.getWidth() / 7;
    int cellHeight = schedulePanel.getHeight() / 24;
    g.setColor(Color.RED);
    for (Event event : events) {
      LocalDateTime startTime = event.getStartTime();
      LocalDateTime endTime = event.getEndTime();
      int dayCol = startTime.getDayOfWeek().getValue() % 7;
      int startHour = startTime.getHour();
      int endHour = endTime.getHour();
      int daySpan = endTime .getDayOfWeek().getValue() - startTime.getDayOfWeek().getValue();
      if (daySpan < 0) {
        daySpan += 7;
      }
      for (int day = 0; day <= daySpan; day++) {
        int column = (dayCol + day) % 7;
        int startY = startHour * cellHeight;
        int endY;
        if (day < daySpan) {
          endY = 24 * cellHeight;
        } else {
          endY = endHour * cellHeight;
        }
        int rectHeight = endY - startY;
        g.fillRect(column * cellWidth, startY, cellWidth, rectHeight);
      }
    }
  }

  private void initializeControlPanel() {
    JPanel controlPanel = new JPanel(new GridLayout(1, 0, 5, 0));
    loadButton = new JButton("Create event");
    saveButton = new JButton("Schedule event");
    //takes list of user-names to pick from
    userDropDown = new JComboBox<>();
    userDropDown.addItem("<none>");
    for (String userName : model.getUserName()) {
      userDropDown.addItem(userName);
    }
    userDropDown.addActionListener(e -> {
      String selectedUser = Objects.requireNonNull(userDropDown.getSelectedItem()).toString();
      if (!"<none>".equals(selectedUser)) {
        loadUserSchedule(selectedUser);
      }
    });
    saveButton.addActionListener(this::saveSchedulesAction);

    loadButton.addActionListener(e -> {
      EventFrame eventFrame = new EventFrame(model);
      eventFrame.setVisible(true);
    });

    controlPanel.add(userDropDown);
    controlPanel.add(loadButton);
    controlPanel.add(saveButton);

    loadButton.setHorizontalAlignment(SwingConstants.CENTER);
    saveButton.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(controlPanel, BorderLayout.SOUTH);
  }

  private void loadXMLAction(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser();
    int option = fileChooser.showOpenDialog(this);
    if (option == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      XMLConfigurator configurator = new XMLConfigurator();
      String userId = configurator.readScheduleUserId(selectedFile.getAbsolutePath());
      if (userId != null && !userId.isEmpty()) {
        if (!model.getUserName().contains(userId)) {
          userDropDown.addItem(userId);
          userDropDown.setSelectedItem(userId);
          model.addUser(userId);
        }
        List<Event> events = configurator.readXMLFile(selectedFile.getAbsolutePath());
        for (Event event : events) {
          model.createEvent(userId, event);
        }
        loadUserSchedule(userId);
        updateView();
      } else {
        displayError("The user ID could not be read from the XML file.");
      }
    }
  }

  private void loadUserSchedule(String userId) {
    events.clear();
    events.addAll(model.getEventsForUser(userId));
    schedulePanel.repaint();
  }

  private void saveSchedulesAction(ActionEvent e) {
    // Placeholder action for saving schedules
    System.out.println("Save schedules action triggered");
  }

  protected void updateEvent(Event eventDetails) {
    events.add(eventDetails);
    schedulePanel.repaint();
  }

  @Override
  public void repaint() {
    super.repaint();
    if (schedulePanel != null) {
      schedulePanel.repaint();
    }
  }

  @Override
  public void updateView() {
    String selectedUser = Objects.requireNonNull(userDropDown.getSelectedItem()).toString();
    userDropDown.removeAllItems();
    userDropDown.addItem("<none>");
    for (String userName : model.getUserName()) {
      userDropDown.addItem(userName);
    }
    userDropDown.setSelectedItem(selectedUser);
    if (selectedUser != null && !"<none>".equals(selectedUser)) {
      loadUserSchedule(selectedUser);
    } else {
      events.clear();
      schedulePanel.repaint();
    }
  }

  @Override
  public void displayError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}