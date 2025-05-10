package pt.ipp.isep.dei;

import pt.ipp.isep.dei.controller.template.AuthenticationController;
import pt.ipp.isep.dei.domain.Editor;
import pt.ipp.isep.dei.domain.template.Employee;
import pt.ipp.isep.dei.domain.template.Organization;
import pt.ipp.isep.dei.domain.template.TaskCategory;
import pt.ipp.isep.dei.repository.EditorRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.repository.template.OrganizationRepository;
import pt.ipp.isep.dei.repository.template.TaskCategoryRepository;
import pt.isep.lei.esoft.auth.domain.model.Email;

public class Bootstrap implements Runnable {

    public void run() {
        addTaskCategories();
        addOrganization();
        addUsers();
    }

    private void addOrganization() {
        OrganizationRepository organizationRepository = Repositories.getInstance().getOrganizationRepository();

        Organization organization = new Organization("Railway Simulator Inc.");
        organization.addEmployee(new Employee("admin@railway.app"));
        organization.addEmployee(new Employee("editor@railway.app"));
        organization.addEmployee(new Employee("player@railway.app"));
        organizationRepository.add(organization);
    }

    private void addTaskCategories() {
        TaskCategoryRepository taskCategoryRepository = Repositories.getInstance().getTaskCategoryRepository();

        taskCategoryRepository.add(new TaskCategory("Analysis"));
        taskCategoryRepository.add(new TaskCategory("Design"));
        taskCategoryRepository.add(new TaskCategory("Implementation"));
        taskCategoryRepository.add(new TaskCategory("Development"));
        taskCategoryRepository.add(new TaskCategory("Testing"));
        taskCategoryRepository.add(new TaskCategory("Deployment"));
        taskCategoryRepository.add(new TaskCategory("Maintenance"));
    }

    private void addUsers() {
        AuthenticationRepository authRepo = Repositories.getInstance().getAuthenticationRepository();
        EditorRepository editorRepo = Repositories.getInstance().getEditorRepository();

        // Add roles
        authRepo.addUserRole(AuthenticationController.ROLE_ADMIN, AuthenticationController.ROLE_ADMIN);
        authRepo.addUserRole(AuthenticationController.ROLE_EDITOR, AuthenticationController.ROLE_EDITOR);
        authRepo.addUserRole(AuthenticationController.ROLE_PLAYER, AuthenticationController.ROLE_PLAYER);

        // Add admin user
        authRepo.addUserWithRole("System Admin", "admin@railway.app", "admin123",
                AuthenticationController.ROLE_ADMIN);

        // Add editor user and entity
        Email editorEmail = new Email("editor@railway.app");
        authRepo.addUserWithRole("Map Editor", editorEmail.toString(), "editor123",
                AuthenticationController.ROLE_EDITOR);
        editorRepo.addEditor(new Editor(editorEmail, "Map Editor", "editor123"));

        // Add player user
        authRepo.addUserWithRole("Game Player", "player@railway.app", "player123",
                AuthenticationController.ROLE_PLAYER);
    }
}