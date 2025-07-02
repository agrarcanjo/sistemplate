package pt.ama.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.ama.model.ImageAsset;
import pt.ama.repository.ImageAssetRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ImageAssetService {
    
    @Inject
    ImageAssetRepository imageRepository;
    
    public ImageAsset saveImage(String name, String originalFilename, String contentType, 
                               byte[] data, String description, String category, String owner) {
        
        // Verificar se já existe uma imagem com o mesmo hash (evitar duplicatas)
        String hash = calculateHash(data);
        Optional<ImageAsset> existing = imageRepository.findByHash(hash);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Verificar se já existe uma imagem com o mesmo nome
        if (imageRepository.existsByName(name)) {
            throw new RuntimeException("Image with name '" + name + "' already exists");
        }
        
        try {
            // Obter dimensões da imagem
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            
            // Criar thumbnail
            String thumbnailBase64 = createThumbnail(bufferedImage);
            
            // Criar base64 da imagem original
            String base64Data = Base64.getEncoder().encodeToString(data);
            
            ImageAsset imageAsset = new ImageAsset();
            imageAsset.setName(name);
            imageAsset.setOriginalFilename(originalFilename);
            imageAsset.setContentType(contentType);
            imageAsset.setSize(data.length);
            imageAsset.setHash(hash);
            imageAsset.setData(data);
            imageAsset.setWidth(width);
            imageAsset.setHeight(height);
            imageAsset.setDescription(description);
            imageAsset.setCategory(category);
            imageAsset.setOwner(owner);
            imageAsset.setCreatedAt(LocalDateTime.now());
            imageAsset.setUpdatedAt(LocalDateTime.now());
            imageAsset.setBase64Data(base64Data);
            imageAsset.setThumbnailBase64(thumbnailBase64);
            
            imageRepository.persist(imageAsset);
            return imageAsset;
            
        } catch (IOException e) {
            throw new RuntimeException("Error processing image: " + e.getMessage(), e);
        }
    }
    
    public Optional<ImageAsset> getImageByName(String name) {
        return imageRepository.findByName(name);
    }
    
    public List<ImageAsset> getImagesByCategory(String category) {
        return imageRepository.findByCategory(category);
    }
    
    public List<ImageAsset> getImagesByOwner(String owner) {
        return imageRepository.findByOwner(owner);
    }
    
    public void deleteImage(String name) {
        imageRepository.softDelete(name);
    }
    
    public String getImageAsBase64(String name) {
        Optional<ImageAsset> image = imageRepository.findByName(name);
        if (image.isPresent()) {
            return "data:" + image.get().getContentType() + ";base64," + image.get().getBase64Data();
        }
        return null;
    }
    
    private String calculateHash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }
    
    private String createThumbnail(BufferedImage originalImage) throws IOException {
        int thumbnailWidth = 150;
        int thumbnailHeight = 150;
        
        // Calcular proporções
        double widthRatio = (double) thumbnailWidth / originalImage.getWidth();
        double heightRatio = (double) thumbnailHeight / originalImage.getHeight();
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (originalImage.getWidth() * ratio);
        int newHeight = (int) (originalImage.getHeight() * ratio);
        
        BufferedImage thumbnail = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpg", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}