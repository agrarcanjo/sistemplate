package pt.ama.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import pt.ama.dto.ErrorResponse;

import java.util.Set;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Exception occurred", exception);

        if (exception instanceof TemplateNotFoundException) {
            return handleTemplateNotFoundException((TemplateNotFoundException) exception);
        }
        
        if (exception instanceof TemplateAlreadyExistsException) {
            return handleTemplateAlreadyExistsException((TemplateAlreadyExistsException) exception);
        }
        
        if (exception instanceof InactiveTemplateException) {
            return handleInactiveTemplateException((InactiveTemplateException) exception);
        }
        
        if (exception instanceof PdfGenerationException) {
            return handlePdfGenerationException((PdfGenerationException) exception);
        }
        
        if (exception instanceof EmailGenerationException) {
            return handleEmailGenerationException((EmailGenerationException) exception);
        }
        
        if (exception instanceof SmsGenerationException) {
            return handleSmsGenerationException((SmsGenerationException) exception);
        }
        
        if (exception instanceof DocumentGenerationException) {
            return handleDocumentGenerationException((DocumentGenerationException) exception);
        }
        
        if (exception instanceof UnsupportedDocumentTypeException) {
            return handleUnsupportedDocumentTypeException((UnsupportedDocumentTypeException) exception);
        }
        
        if (exception instanceof ConstraintViolationException) {
            return handleValidationException((ConstraintViolationException) exception);
        }
        
        if (exception instanceof BusinessException) {
            return handleBusinessException((BusinessException) exception);
        }

        return handleGenericException(exception);
    }

    private Response handleTemplateNotFoundException(TemplateNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "TEMPLATE_NOT_FOUND",
            ex.getMessage(),
            Response.Status.NOT_FOUND.getStatusCode()
        );
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }

    private Response handleTemplateAlreadyExistsException(TemplateAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
            "TEMPLATE_ALREADY_EXISTS",
            ex.getMessage(),
            Response.Status.CONFLICT.getStatusCode()
        );
        return Response.status(Response.Status.CONFLICT).entity(error).build();
    }

    private Response handleInactiveTemplateException(InactiveTemplateException ex) {
        ErrorResponse error = new ErrorResponse(
            "INACTIVE_TEMPLATE",
            ex.getMessage(),
            Response.Status.BAD_REQUEST.getStatusCode()
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handlePdfGenerationException(PdfGenerationException ex) {
        ErrorResponse error = new ErrorResponse(
            "PDF_GENERATION_ERROR",
            ex.getMessage(),
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }

    private Response handleEmailGenerationException(EmailGenerationException ex) {
        ErrorResponse error = new ErrorResponse(
            "EMAIL_GENERATION_ERROR",
            ex.getMessage(),
            Response.Status.BAD_REQUEST.getStatusCode()
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handleSmsGenerationException(SmsGenerationException ex) {
        ErrorResponse error = new ErrorResponse(
            "SMS_GENERATION_ERROR",
            ex.getMessage(),
            Response.Status.BAD_REQUEST.getStatusCode()
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handleDocumentGenerationException(DocumentGenerationException ex) {
        ErrorResponse error = new ErrorResponse(
            "DOCUMENT_GENERATION_ERROR",
            ex.getMessage(),
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }

    private Response handleUnsupportedDocumentTypeException(UnsupportedDocumentTypeException ex) {
        ErrorResponse error = new ErrorResponse(
            "UNSUPPORTED_DOCUMENT_TYPE",
            ex.getMessage(),
            Response.Status.BAD_REQUEST.getStatusCode()
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handleValidationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
            
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            message,
            Response.Status.BAD_REQUEST.getStatusCode()
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            Response.Status.BAD_REQUEST.getStatusCode()
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Erro interno do servidor",
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}