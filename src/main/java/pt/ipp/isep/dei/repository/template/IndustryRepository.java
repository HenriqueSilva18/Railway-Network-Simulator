package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Industry;
import java.util.ArrayList;
import java.util.List;

public class IndustryRepository {
    private final List<Industry> industries;

    public IndustryRepository() {
        this.industries = new ArrayList<>();
        initializeDefaultIndustries();
    }

    private void initializeDefaultIndustries() {
        // Add some default industries
        industries.add(new Industry("Mine", "Primary", 1900, 100.0));
        industries.add(new Industry("Farm", "Primary", 1900, 80.0));
        industries.add(new Industry("Bakery", "Transforming", 1900, 60.0));
        industries.add(new Industry("Factory", "Mixed", 1900, 90.0));
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