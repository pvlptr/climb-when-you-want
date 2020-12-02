/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package climb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;


public class App {

    protected final static Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Starting");

        String urlBase = args[0];
        String login = args[1];
        String password = args[2];
        LocalDateTime dateTime = LocalDateTime.parse(args[3]);
        int period = Integer.valueOf(args[4]);

        log.info("Registering for: " + dateTime);

        Registrator registrator = new Registrator(urlBase, login, password);

        Timer timer = new Timer("Timer");
        TimerTask task = new RegistrationTimerTask(timer, registrator, dateTime);
        timer.scheduleAtFixedRate(task, 1000L, period);
        synchronized (timer) {
            try {
                timer.wait();
            } catch (InterruptedException ex) {
            }
        }
        log.info("Finished");
    }


    private static class RegistrationTimerTask extends TimerTask {

        protected final Logger log = LogManager.getLogger(this.getClass());

        private final Timer timer;
        private final Registrator registrator;
        private final LocalDateTime dateTime;

        public RegistrationTimerTask(Timer timer, Registrator registrator, LocalDateTime dateTime) {
            this.timer = timer;
            this.registrator = registrator;
            this.dateTime = dateTime;
        }

        @Override
        public void run() {
            try {
                var success = registrator.register(dateTime);
                log.info("Success: " + success);
                if (success) {
                    log.info("Stopping timer");
                    synchronized (timer) {
                        timer.cancel();
                        timer.notifyAll();
                    }
                }
            } catch (Exception ex) {
                log.info("Error: " + ex.getMessage());
                log.trace("Error", ex);
            }
        }

    }


}