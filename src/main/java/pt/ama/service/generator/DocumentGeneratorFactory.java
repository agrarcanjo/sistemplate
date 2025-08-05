package pt.ama.service.generator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.exception.UnsupportedDocumentTypeException;
import pt.ama.model.DocumentType;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory para criar geradores de documento baseado no tipo
 */
@ApplicationScoped
public class DocumentGeneratorFactory {
    
    private static final Logger LOG = Logger.getLogger(DocumentGeneratorFactory.class);
    
    private final Map<DocumentType, DocumentGenerator> generators = new ConcurrentHashMap<>();
    
    @Inject
    PdfGenerator pdfGenerator;
    
    @Inject
    EmailGenerator emailGenerator;
    
    @Inject
    SmsGenerator smsGenerator;
    
    /**
     * Inicializa os geradores disponíveis
     */
    public void init() {
        if (generators.isEmpty()) {
            LOG.info("Inicializando geradores de documento");
            
            generators.put(DocumentType.PDF, pdfGenerator);
            generators.put(DocumentType.EMAIL, emailGenerator);
            generators.put(DocumentType.SMS, smsGenerator);
            
            LOG.infof("Geradores inicializados: %s", generators.keySet());
        }
    }
    
    /**
     * Obtém o gerador apropriado para o tipo de documento
     */
    public DocumentGenerator getGenerator(DocumentType type) {
        init();

        DocumentGenerator generator = generators.get(type);
        
        if (generator == null) {
            LOG.errorf("Gerador não encontrado para tipo: %s", type);
            throw new UnsupportedDocumentTypeException(type);
        }
        
        LOG.debugf("Gerador encontrado para tipo %s: %s", type, generator.getClass().getSimpleName());
        return generator;
    }
}