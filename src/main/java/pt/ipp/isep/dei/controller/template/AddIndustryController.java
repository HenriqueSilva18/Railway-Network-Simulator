package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.IndustryRepository;
import pt.ipp.isep.dei.repository.template.MapRepository;

import java.util.List;

public class AddIndustryController {
    private final IndustryRepository industryRepository;
    private final MapRepository mapRepository;
    private final Map currentMap;

    public AddIndustryController() {
        this.industryRepository = Repositories.getInstance().getIndustryRepository();
        this.mapRepository = Repositories.getInstance().getMapRepository();
        this.currentMap = ApplicationSession.getInstance().getCurrentMap();
    }

    public List<Industry> getAvailableIndustries() {
        return industryRepository.getAvailableIndustries();
    }

    public boolean validateIndustry(String nameID, int x, int y) {
        if (nameID == null || nameID.isEmpty()) {
            return false;
        }

        if (currentMap == null) {
            return false;
        }

        return currentMap.isCellEmpty(x, y);
    }

    public Industry createIndustry(String nameID, int x, int y, Industry selectedIndustry) {
        if (!validateIndustry(nameID, x, y)) {
            return null;
        }

        Industry industry = new Industry(nameID, selectedIndustry.getType(), selectedIndustry.getSector(), 1900, new Position(x, y));
        if (currentMap.addIndustry(industry)) {
            mapRepository.save(currentMap);
            return industry;
        }

        return null;
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public boolean isNameIDTaken(String nameID) {
        if (currentMap == null) {
            return false;
        }
        return currentMap.getIndustries().stream()
                .anyMatch(industry -> industry.getNameID().equals(nameID));
    }
} 