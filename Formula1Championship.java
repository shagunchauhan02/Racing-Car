import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

abstract class Driver {
    protected String name;
    protected String location;
    protected String team;

    public Driver(String name, String location, String team) {
        this.name = name;
        this.location = location;
        this.team = team;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getTeam() { return team; }
}
class Formula1Driver extends Driver {
    private int firstPositions, secondPositions, thirdPositions, totalPoints, racesParticipated;

    public Formula1Driver(String name, String location, String team) {
        super(name, location, team);
        this.firstPositions = 0;
        this.secondPositions = 0;
        this.thirdPositions = 0;
        this.totalPoints = 0;
        this.racesParticipated = 0;
    }

    public void updateStatistics(int position) {
        racesParticipated++;
        switch (position) {
            case 1: firstPositions++; totalPoints += 25; break;
            case 2: secondPositions++; totalPoints += 18; break;
            case 3: thirdPositions++; totalPoints += 15; break;
            case 4: totalPoints += 12; break;
            case 5: totalPoints += 10; break;
            case 6: totalPoints += 8; break;
            case 7: totalPoints += 6; break;
            case 8: totalPoints += 4; break;
            case 9: totalPoints += 2; break;
            case 10: totalPoints += 1; break;
        }
    }

    public int getTotalPoints() { return totalPoints; }
    public int getFirstPositions() { return firstPositions; }
    public int getRacesParticipated() { return racesParticipated; }

    @Override
    public String toString() {
        return name + " | " + team + " | Points: " + totalPoints + " | Wins: " + firstPositions;
    }
}
interface ChampionshipManager {
    void addDriver(Formula1Driver driver);
    void removeDriver(String team);
    void displayDriverStats(String name);
    void displayChampionshipTable();
    void addRaceResults(Map<String, Integer> results);
}
class Formula1ChampionshipManager implements ChampionshipManager {
    private List<Formula1Driver> drivers = new ArrayList<>();

    @Override
    public void addDriver(Formula1Driver driver) {
        drivers.add(driver);
        System.out.println(driver.getName() + " added to " + driver.getTeam());
    }

    @Override
    public void removeDriver(String team) {
        drivers.removeIf(driver -> driver.getTeam().equalsIgnoreCase(team));
        System.out.println("Driver from " + team + " removed.");
    }

    @Override
    public void displayDriverStats(String name) {
        for (Formula1Driver driver : drivers) {
            if (driver.getName().equalsIgnoreCase(name)) {
                System.out.println(driver);
                return;
            }
        }
        System.out.println("Driver not found.");
    }

    @Override
    public void displayChampionshipTable() {
        drivers.sort(Comparator.comparingInt(Formula1Driver::getTotalPoints).reversed()
                .thenComparing(Formula1Driver::getFirstPositions, Comparator.reverseOrder()));
        for (Formula1Driver driver : drivers) {
            System.out.println(driver);
        }
    }

    @Override
    public void addRaceResults(Map<String, Integer> results) {
        for (Formula1Driver driver : drivers) {
            if (results.containsKey(driver.getName())) {
                driver.updateStatistics(results.get(driver.getName()));
            }
        }
        System.out.println("Race results updated.");
    }

    public void startConsoleMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Add Driver\n2. Remove Driver\n3. View Driver Stats\n4. View Championship Table\n5. Add Race Results\n6. Open GUI\n7. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter location: ");
                    String location = scanner.nextLine();
                    System.out.print("Enter team: ");
                    String team = scanner.nextLine();
                    addDriver(new Formula1Driver(name, location, team));
                    break;
                case 2:
                    System.out.print("Enter team to remove: ");
                    removeDriver(scanner.nextLine());
                    break;
                case 3:
                    System.out.print("Enter driver name: ");
                    displayDriverStats(scanner.nextLine());
                    break;
                case 4:
                    displayChampionshipTable();
                    break;
                case 5:
                    Map<String, Integer> results = new HashMap<>();
                    for (Formula1Driver driver : drivers) {
                        System.out.print("Enter position for " + driver.getName() + ": ");
                        results.put(driver.getName(), scanner.nextInt());
                    }
                    addRaceResults(results);
                    break;
                case 6:
                    SwingUtilities.invokeLater(() -> new ChampionshipGUI(drivers));
                    break;
                case 7:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
class ChampionshipGUI extends JFrame {
    public ChampionshipGUI(List<Formula1Driver> drivers) {
        setTitle("Formula 1 Championship");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        for (Formula1Driver driver : drivers) {
            textArea.append(driver + "\n");
        }
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        setVisible(true);
    }
}
public class Formula1Championship {
    public static void main(String[] args) {
        Formula1ChampionshipManager manager = new Formula1ChampionshipManager();
        manager.startConsoleMenu();
    }
}
