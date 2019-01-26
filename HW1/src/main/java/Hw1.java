import javax.swing.*;

/**
 * Created by ashesh on 10/30/2015.
 */
public class Hw1 {
  public static void main(String[] args) {
//    TimeConvert time = new TimeConvert();
//    System.out.println(String.format("%d:%d:%d", time.getHour(),time.getMin(),time.getSec()));
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
