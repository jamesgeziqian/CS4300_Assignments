import javax.swing.*;

/**
 * Created by ashesh on 10/30/2015.
 */

/**
 * This is the main class that everything start.
 * This class will create a JOGLFrame and set it visible
 * and all other works will be done starting from JOGLFrame.
 */
public class HelloJOGLAnimator {

  /**
   * The main class.
   * @param args
   */
  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  /**
   * Create a JOGLFrame and show it.
   */
  private static void createAndShowGUI() {
    JFrame frame = new JOGLFrame("Hello JOGL Animator");
    frame.setVisible(true);
  }
}
