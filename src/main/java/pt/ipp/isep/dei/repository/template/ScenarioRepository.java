package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Scenario;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ScenarioRepository {
    private static final String SCENARIOS_DIR = "saved_scenarios";
    private final List<Scenario> scenarios;
    private Scenario currentScenario;
    private Date currentDate;

    public ScenarioRepository() {
        this.scenarios = new ArrayList<>();
        // Initialize current date to today
        this.currentDate = new Date();
        createScenariosDirectory();
    }

    private void createScenariosDirectory() {
        File directory = new File(SCENARIOS_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public List<Scenario> getAvailableScenarios() {
        return new ArrayList<>(scenarios);
    }

    public List<Scenario> getAllScenarios() {
        return getAvailableScenarios();
    }

    public boolean save(Scenario scenario) {
        if (scenario == null) return false;

        // Save to in-memory repository
        if (!scenarios.contains(scenario)) {
            scenarios.add(scenario);
        }

        // Save to file
        try {
            String filePath = SCENARIOS_DIR + File.separator + scenario.getNameID() + ".scenario";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(scenario);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Scenario loadScenarioFromFile(String scenarioId) {
        if (scenarioId == null) return null;

        String filePath = SCENARIOS_DIR + File.separator + scenarioId + ".scenario";
        File file = new File(filePath);
        
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Scenario) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAvailableScenarioFiles() {
        File directory = new File(SCENARIOS_DIR);
        List<String> fileNames = new ArrayList<>();
        
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".scenario"));
            if (files != null) {
                for (File file : files) {
                    fileNames.add(file.getName().replace(".scenario", ""));
                }
            }
        }
        
        return fileNames;
    }

    public Scenario getScenario(String scenarioId) {
        if (scenarioId == null) return null;
        
        return scenarios.stream()
                .filter(s -> s.getNameID().equals(scenarioId))
                .findFirst()
                .orElse(null);
    }

    public Scenario getCurrentScenario() {
        return currentScenario;
    }

    public void setCurrentScenario(Scenario scenario) {
        this.currentScenario = scenario;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date date) {
        if (date != null) {
            this.currentDate = date;
        }
    }
} 