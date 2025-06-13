package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Size;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapRepository {
    private final ConcurrentHashMap<String, Map> maps;
    private static final String MAPS_DIRECTORY = "saved_maps";
    private static final String SCENARIOS_PATH = "docs/mdisc/data";

    public MapRepository() {
        this.maps = new ConcurrentHashMap<>();
        createMapsDirectory();
        initializeMaps();
    }

    private void createMapsDirectory() {
        File directory = new File(MAPS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void initializeMaps() {
        // Initialize predefined maps with their sizes
        Map iberianPeninsula = Map.createMap("iberian_peninsula", Size.createSize(12, 9), 1);
        Map france = Map.createMap("france", Size.createSize(10, 10), 1);
        Map italy = Map.createMap("italy", Size.createSize(15, 15), 1);

        // Add maps to repository
        maps.put(iberianPeninsula.getNameID(), iberianPeninsula);
        maps.put(france.getNameID(), france);
        maps.put(italy.getNameID(), italy);

        // Load scenarios for each map
        loadScenarios();
    }

    private void loadScenarios() {
        File scenariosDir = new File(SCENARIOS_PATH);
        if (!scenariosDir.exists() || !scenariosDir.isDirectory()) {
            return;
        }

        File[] files = scenariosDir.listFiles((dir, name) -> name.startsWith("scenario") && name.endsWith("_lines.csv"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            String scenarioId = file.getName().replace("_lines.csv", "");
            for (Map map : maps.values()) {
                map.addScenario(scenarioId);
            }
        }
    }

    public List<Map> getAvailableMaps() {
        return new ArrayList<>(maps.values());
    }

    public Map getMap(String mapId) {
        return maps.get(mapId);
    }

    public boolean save(Map map) {
        if (map == null || map.getNameID() == null) {
            return false;
        }
        maps.put(map.getNameID(), map);
        return saveMapToFile(map);
    }

    public boolean delete(String mapId) {
        if (mapId == null || !maps.containsKey(mapId)) {
            return false;
        }
        maps.remove(mapId);
        File mapFile = new File(MAPS_DIRECTORY, mapId + ".map");
        return !mapFile.exists() || mapFile.delete();
    }

    public List<String> getScenarios(String mapId) {
        Map map = maps.get(mapId);
        if (map == null) return new ArrayList<>();
        return map.getAvailableScenarios();
    }

    public List<String> getAvailableMapFiles() {
        File directory = new File(MAPS_DIRECTORY);
        if (!directory.exists()) {
            return new ArrayList<>();
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".map"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<String> mapFiles = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            mapFiles.add(fileName.substring(0, fileName.length() - 4)); // Remove .map extension
        }
        return mapFiles;
    }

    private boolean saveMapToFile(Map map) {
        File file = new File(MAPS_DIRECTORY, map.getNameID() + ".map");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(map);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map loadMapFromFile(String mapId) {
        File file = new File(MAPS_DIRECTORY, mapId + ".map");
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map map = (Map) ois.readObject();
            maps.put(map.getNameID(), map);
            return map;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
} 