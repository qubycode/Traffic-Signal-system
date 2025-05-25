import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        TrafficController controller = new TrafficController();
        controller.startTrafficCycle();
    }
}

class TrafficSignal {

    private final String direction;
    private boolean isGreen;
    private boolean isYellow;

   
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    public TrafficSignal(String direction) {
        this.direction = direction;
        this.isGreen = false;
        this.isYellow = false;
    }

    public synchronized void turnGreen() {
        isGreen = true;
        isYellow = false;
        System.out.println("\n" + timeStamp() + direction + " signal is " + GREEN + "GREEN" + RESET + ".\n");
    }

    public synchronized void turnYellow() {
        isGreen = false;
        isYellow = true;
        System.out.println("\n" + timeStamp() + direction + " signal is " + YELLOW + "YELLOW" + RESET + ".\n");
    }

    public synchronized void turnRed() {
        isGreen = false;
        isYellow = false;
        System.out.println("\n" + timeStamp() + direction + " signal is " + RED + "RED" + RESET + ".\n");
    }

    public boolean isGreen() {
        return isGreen;
    }

    public String getDirection() {
        return direction;
    }

    private String timeStamp() {
        return "[" + LocalTime.now().withNano(0) + "] ";
    }
}

class TrafficController {

    private final TrafficSignal north = new TrafficSignal("North");
    private final TrafficSignal south = new TrafficSignal("South");
    private final TrafficSignal east = new TrafficSignal("East");
    private final TrafficSignal west = new TrafficSignal("West");

    private final TrafficSignal[] signals = { north, south, east, west };

    public void startTrafficCycle() {
        Thread controllerThread = new Thread(() -> {
            while (true) {
                for (TrafficSignal signal : signals) {
                    turnOnlyOneGreen(signal);
                    startCountdown("GREEN", 12);
                    signal.turnYellow();
                    startCountdown("YELLOW", 3);
                    signal.turnRed();
                    System.out.println("\n==========================================\n");
                }
            }
        });
        controllerThread.start();
    }

    private void turnOnlyOneGreen(TrafficSignal greenSignal) {
        for (TrafficSignal signal : signals) {
            if (signal == greenSignal) {
                signal.turnGreen();
            } else {
                signal.turnRed();
            }
        }
    }

    private void startCountdown(String phase, int seconds) {
        for (int i = seconds; i > 0; i--) {
            System.out.println(phase + " TIMER: " + i + "s remaining");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
