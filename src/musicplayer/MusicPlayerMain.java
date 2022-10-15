package musicplayer;

import javafx.application.Application;
import javafx.stage.Stage;

public class MusicPlayerMain extends Application {
  public static void main(String[] args) {

    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
          .getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(Controller.class.getName()).log(
          java.util.logging.Level.SEVERE, null,
          ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(Controller.class.getName()).log(
          java.util.logging.Level.SEVERE, null,
          ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(Controller.class.getName()).log(
          java.util.logging.Level.SEVERE, null,
          ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(Controller.class.getName()).log(
          java.util.logging.Level.SEVERE, null,
          ex);
    }

    Application.launch(args);
  }

  @Override
  public void start(Stage arg0) throws Exception {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new Controller().setVisible(true);
      }
    });
  }
}
