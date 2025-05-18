package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Train;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TrainRepository {
    private final java.util.Map<String, Train> trains;

    public TrainRepository() {
        this.trains = new ConcurrentHashMap<>();
    }

    public boolean save(Train train) {
        if (train == null || train.getNameID() == null) {
            return false;
        }
        trains.put(train.getNameID(), train);
        return true;
    }

    public Train getById(String id) {
        return trains.get(id);
    }

    public List<Train> getAll() {
        return new ArrayList<>(trains.values());
    }

    public boolean exists(String trainId) {
        return trains.containsKey(trainId);
    }

    public boolean delete(String trainId) {
        return trains.remove(trainId) != null;
    }

    public void initialize() {
        // Add some default trains if needed
        // This can be implemented later when we have train creation functionality
    }
} 