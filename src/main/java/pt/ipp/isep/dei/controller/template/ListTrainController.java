package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Locomotive;
import pt.ipp.isep.dei.domain.template.Train;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.TrainRepository;

import java.util.List;

public class ListTrainController {

    private final TrainRepository trainRepository;

    public ListTrainController() {
        Repositories repositories = Repositories.getInstance();
        this.trainRepository = repositories.getTrainRepository();
    }

    public List<Train> getAllTrains() {
        return trainRepository.getAllTrains();
    }

    public Train getTrainById(String trainId) {
        return trainRepository.getTrain(trainId);
    }

    public void addTrain(Train train) {
        if (train != null && !trainRepository.contains(train)) {
            trainRepository.save(train);
        }
    }

    public void removeTrain(Train train) {
        if (train != null) {
            trainRepository.remove(train);
        }
    }

    public Train getTrainForLocomotive(Locomotive loco) {
        if (loco == null) {
            return null;
        }
        List<Train> trains = trainRepository.getAllTrains();
        for (Train train : trains) {
            if (train.getLocomotive().equals(loco)) {
                return train;
            }
        }
        return null; // No train found with the given locomotive
    }

    public String getRouteForTrain(Train train) {
        if (train == null) {
            return null;
        }
        // Assuming Train has a method to get its route
        return train.getAssignedRoute() != null ? train.getAssignedRoute().getNameID() : null;
    }
}
