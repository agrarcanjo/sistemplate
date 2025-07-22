package pt.ama.config;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import pt.ama.util.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Dependent
public class SecurityConfig {

    private static final String TYPE_PKCS_12 = "PKCS12";

    @ConfigProperty(name = "application.security.keystore.path")
    String keystorePath;

    @ConfigProperty(name = "application.security.keystore.password")
    String keystorePassword;

    @Produces
    public KeyStore keyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        try (InputStream ks = ResourceLoader.load(keystorePath)) {
            if (ks == null) {
                throw new FileNotFoundException("Keystore not found in path " + keystorePath);
            }
            KeyStore keyStore = KeyStore.getInstance(TYPE_PKCS_12);
            keyStore.load(ks, keystorePassword != null ? keystorePassword.toCharArray() : null);

            return keyStore;
        }
    }

}
