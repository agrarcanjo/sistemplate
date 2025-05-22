package pt.ama.service;

import pt.ama.model.Template;
import pt.ama.repository.TemplateRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class TemplateService {

    @Inject
    TemplateRepository templateRepository;

    public List<Template> listAll() {
        return templateRepository.listAll();
    }

    public void add(Template template) {
        templateRepository.persist(template);
    }

    public Template findByName(String name) {
        return templateRepository.find("name", name).firstResult();
    }
}
