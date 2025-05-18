package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Position;
import java.util.ArrayList;
import java.util.List;

public class IndustryRepository {
    private final List<Industry> industries;

    public IndustryRepository() {
        this.industries = new ArrayList<>();
        initializeDefaultIndustries();
    }

    private void initializeDefaultIndustries() {
        // Add some default industries with positions
        industries.add(new Industry("Mine", "Mine", "Primary", 1900, new Position(0, 0)));
        industries.add(new Industry("Farm", "Farm", "Primary", 1900, new Position(1, 0)));
        industries.add(new Industry("Bakery", "Bakery", "Transforming", 1900, new Position(2, 0)));
        industries.add(new Industry("Port", "Port", "Port", 1900, new Position(3, 0)));
    }

    public List<Industry> getAvailableIndustries() {
        return new ArrayList<>(industries);
    }

    public boolean add(Industry industry) {
        if (industry != null) {
            return industries.add(industry);
        }
        return false;
    }
} 