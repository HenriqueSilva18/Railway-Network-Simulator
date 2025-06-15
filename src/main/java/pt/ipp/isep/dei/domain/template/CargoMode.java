package pt.ipp.isep.dei.domain.template;

public enum CargoMode {
    FULL,    // Train only departs when fully loaded
    HALF,    // Train departs when half of carriages are loaded
    AVAILABLE // Train departs with available cargoes in the station
} 