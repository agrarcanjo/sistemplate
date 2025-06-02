package pt.ama.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    tags = {
            @Tag(name="Templates", description="Operações de gerenciamento de templates"),
            @Tag(name="Documents", description="Operações de geração de documentos")
    },
    info = @Info(
        title="Sistema de Templates API",
        version = "1.0.0",
        contact = @Contact(
            name = "API Support",
            url = "http://accenture.com/contact",
            email = "support@accenture.com"),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class SwaggerConfig extends Application {
}
