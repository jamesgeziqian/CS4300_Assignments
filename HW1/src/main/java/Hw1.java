import javax.swing.*;

/**
 * This is the main class that opens a JFrame and run the clock
 **/
public class Hw1 {

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  private static void createAndShowGUI() {
    JFrame frame = new JOGLFrame("Clock!");
    frame.setVisible(true);
  }
}
