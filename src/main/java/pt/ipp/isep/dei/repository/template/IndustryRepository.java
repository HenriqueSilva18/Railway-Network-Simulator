package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Position;
import java.util.ArrayList;
import java.util.List;

public class IndustryRepository {
    private final List<Industry> industries;

    public IndustryRepository() {
        this.industries = new ArrayList<>();
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