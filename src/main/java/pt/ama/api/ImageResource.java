package pt.ama.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jboss.resteasy.reactive.RestForm;
import pt.ama.model.ImageAsset;
import pt.ama.service.ImageAssetService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Path("/api/images")
@Produces(MediaType.APPLICATION_JSON)
public class ImageResource {
    
    @Inject
    ImageAssetService imageService;
    
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(@RestForm("file") FileUpload file,
                               @RestForm("name") String name,
                               @RestForm("description") String description,
                               @RestForm("category") String category,
                               @RestForm("owner") String owner) {
        try {
            if (file == null || file.uploadedFile() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity("File is required").build();
            }
            
            // Validar tipo de arquivo
            String contentType = file.contentType();
            if (!isValidImageType(contentType)) {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity("Invalid image type. Only PNG, JPG, JPEG, GIF are allowed").build();
            }
            
            byte[] data = Files.readAllBytes(file.uploadedFile());
            
            ImageAsset savedImage = imageService.saveImage(
                name != null ? name : file.fileName(),
                file.fileName(),
                contentType,
                data,
                description,
                category,
                owner
            );
            
            return Response.status(Response.Status.CREATED).entity(savedImage).build();
            
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Error reading file: " + e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/{name}")
    public Response getImage(@PathParam("name") String name) {
        return imageService.getImageByName(name)
                .map(image -> Response.ok(image).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @GET
    @Path("/{name}/base64")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getImageAsBase64(@PathParam("name") String name) {
        String base64 = imageService.getImageAsBase64(name);
        if (base64 != null) {
            return Response.ok(base64).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @GET
    @Path("/category/{category}")
    public Response getImagesByCategory(@PathParam("category") String category) {
        List<ImageAsset> images = imageService.getImagesByCategory(category);
        return Response.ok(images).build();
    }
    
    @GET
    @Path("/owner/{owner}")
    public Response getImagesByOwner(@PathParam("owner") String owner) {
        List<ImageAsset> images = imageService.getImagesByOwner(owner);
        return Response.ok(images).build();
    }
    
    @DELETE
    @Path("/{name}")
    public Response deleteImage(@PathParam("name") String name) {
        try {
            imageService.deleteImage(name);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Error deleting image: " + e.getMessage()).build();
        }
    }
    
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
            contentType.equals("image/png") ||
            contentType.equals("image/jpeg") ||
            contentType.equals("image/jpg") ||
            contentType.equals("image/gif")
        );
    }
}