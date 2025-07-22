package pt.ama.service.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import pt.ama.enums.oauth2.AuthTypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pt.ama.enums.oauth2.UserAttributeEnum.*;

@RequestScoped
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class);

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    JsonWebToken jwt;

    String FA_CREDENTIAL_TYPE = "credential_type";
    String FA_ATTRIBUTES = "faAttributes";
    String FA_ATTRIBUTE_VALUE = "string";

    List<String> USER_IDENTIFIER_LIST = List.of(
            MDC_CIDADAO_NIC.getUrl(),
            HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_PERSONIDENTIFIER.getUrl(),
            MDC_ADVOGADO_NOA.getUrl(),
            MDC_SOLICITADOR_NCS.getUrl(),
            MDC_NOTARIO_NON.getUrl(),
            MDC_CIDADAO_NIF.getUrl()
    );



    /**
     * Retrieves the value of a specific claim from the JWT.
     *
     * @param claimName the name of the claim to retrieve
     * @return the value of the specified claim as a String
     */
    public String getClaim(String claimName) {
        return jwt.getClaim(claimName);
    }

    /**
     * Retrieves the FA_ATTRIBUTES claim from the JWT as a map of key-value pairs.
     * <p>
     * The method processes the FA_ATTRIBUTES claim, attempting to transform it into a
     * Map where each key represents an attribute name and each value represents the
     * corresponding attribute value. If the claim is not present or an error occurs
     * while parsing it, a runtime exception will be thrown.
     *
     * @return A Map containing the keys and associated objects from the FA_ATTRIBUTES claim
     * of the JWT.
     * @throws RuntimeException if an error occurs during the parsing or mapping process.
     */
    public Map<String, Object> getFaAttributesAsMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        Object faAttributes = jwt.getClaim(FA_ATTRIBUTES);

        try {
            String faAttributesJson = objectMapper.writeValueAsString(faAttributes);
            return objectMapper.readValue(faAttributesJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            LOG.errorv(
                    "Failed to parse FA_ATTRIBUTES for principal name '{0}'",
                    securityIdentity.getPrincipal().getName()
            );
            throw new RuntimeException("Failed to parse FA_ATTRIBUTES", e);
        }
    }

    /**
     * Retrieves the FA_ATTRIBUTES claim from the JWT as a Map of string key-value pairs.
     * <p>
     * The method processes the FA_ATTRIBUTES claim, converting nested attributes with a
     * FA_ATTRIBUTE_VALUE field into a flat map where each key corresponds to the keys in the claim and
     * each value is the associated string. If the claim contains non-convertible data or an
     * error occurs during processing, a runtime exception is thrown.
     *
     * @return A Map where the keys are the attribute names from FA_ATTRIBUTES and the values
     * are the corresponding string representatives.
     * @throws RuntimeException if parsing or mapping FA_ATTRIBUTES fails.
     */
    public Map<String, String> getFaAttributesAsStringMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        Object faAttributes = jwt.getClaim(FA_ATTRIBUTES);
        try {

            String faAttributesJson = objectMapper.writeValueAsString(faAttributes);
            Map<String, Object> values = objectMapper.readValue(faAttributesJson, new TypeReference<Map<String, Object>>() {
            });

            return getStringStringMap(values);

        } catch (Exception e) {
            LOG.errorv(
                    "Failed to parse FA_ATTRIBUTES for principal name '{0}'",
                    securityIdentity.getPrincipal().getName()
            );
            throw new RuntimeException("Failed to parse faAttributes", e);
        }
    }

    private Map<String, String> getStringStringMap(Map<String, Object> values) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map<?, ?> nestedMap) {
                Object stringValue = nestedMap.get(FA_ATTRIBUTE_VALUE);
                if (stringValue instanceof String) {
                    result.put(key, (String) stringValue);
                }
            }
        }
        return result;
    }


    /**
     * Retrieves the value associated with the given URI from the FA attributes.
     *
     * The method fetches a flat map of FA (functional area) attributes as key-value pairs
     * using the {@code getFaAttributesAsStringMap()} method. It then searches for the specified
     * URI in the map and returns the corresponding value, if present. If the map is null or the URI
     * is not found, the method returns null.
     *
     * @param uri the URI to be searched in the FA attributes map
     * @return the value associated with the specified URI, or null if not found
     */
    public String getFaAttribute(String uri) {
        Map<String, String> faAttributes = getFaAttributesAsStringMap();
        if (faAttributes != null) {
            return faAttributes.get(uri);
        }
        return null;
    }


    /**
     * Retrieves an identifier from the FA (functional area) attributes or default to the principal's name
     * if no matching identifier is found.
     * <p>
     * The method first fetches FA attributes as a {@link Map} of string key-value pairs using the
     * {@code getFaAttributesAsStringMap()} method. It then iterates through the list of FA identifiers
     * to check if any identifier exists in the FA attributes. If a match is found, the corresponding
     * value is returned. Otherwise, it falls back to the name of the principal associated with the
     * security identity.
     *
     * @return The matched identifier from FA attributes, or the principal's name if no match is found.
     */
    public String getIdentifier() {
        Map<String, String> faAttributes = getFaAttributesAsStringMap();
        for (String identifier : USER_IDENTIFIER_LIST) {
            if (faAttributes.containsKey(identifier)) {
                return faAttributes.get(identifier);
            }
        }
        LOG.warnv(
                "No identifier found in FA attributes. Using principal name '{0}' as identifier.",
                securityIdentity.getPrincipal().getName()
        );
        return securityIdentity.getPrincipal().getName();
    }

    /**
     * Constructs a PMC identifier for the user based on their credential type and attributes.
     *
     * The method retrieves the user's FA (functional area) attributes and credential type,
     * determines the authentication type based on the credential type using {@link AuthTypeEnum},
     * and constructs a unique identifier depending on the resolved authentication type.
     * For the CMD_DOCUMENT type, the identifier is formed using the document's nationality, type,
     * and number. For other types, the default identifier value is returned.
     *
     * @return The PMC identifier, which is a unique string representing the user based on their
     *         credential type and attributes.
     */
    public String getPmcIdentifier() {
        Map<String, String> faAttributes = getFaAttributesAsStringMap();
        String credentialType = getCredentialType();
        AuthTypeEnum authTypeEnum = AuthTypeEnum.getByTypeFa(credentialType);
        switch (authTypeEnum) {
            case CMD_DOCUMENT:
                String nationality = Optional.ofNullable(faAttributes.get(MDC_CIDADAO_DOCNATIONALITY.getUrl()))
                        .orElse(faAttributes.get(MDC_CIDADAO_NACIONALIDADE.getUrl()));
                return nationality +
                        "_" +
                        faAttributes.get(MDC_CIDADAO_DOCTYPE.getUrl()) +
                        "_" +
                        faAttributes.get(MDC_CIDADAO_DOCNUMBER.getUrl());

            case NOTARY:
            case LAWYER:
            case SOLICITATOR:
            case SIMPLE:
            case CMD_NIC:
            case CMD_NIC_EMAIL:
            case SOCIAL_NETWORK:
            case EIDAS:
            default:
                return getIdentifier();
        }
    }


    /**
     * Retrieves the credential type from the JWT claim with FA_CREDENTIAL_TYPE named.
     * <p>
     * If the claim FA_CREDENTIAL_TYPE is not present in the JWT, an IllegalArgumentException is thrown.
     *
     * @return The value of the FA_CREDENTIAL_TYPE claim in the JWT.
     * @throws IllegalArgumentException if the FA_CREDENTIAL_TYPE claim does not exist.
     */
    public String getCredentialType() {
        if (jwt.getClaim(FA_CREDENTIAL_TYPE) == null) {
            LOG.errorv(
                    "Claim FA_CREDENTIAL_TYPE does not exist for principal name '{0}'",
                    securityIdentity.getPrincipal().getName()
            );
            throw new IllegalArgumentException("Claim FA_CREDENTIAL_TYPE does not exist");
        }
        return jwt.getClaim(FA_CREDENTIAL_TYPE);
    }

    /**
     * Retrieves the authentication type based on the credential type extracted from the JWT claim.
     * <p>
     * The method uses the `getCredentialType` method to fetch the credential type from the JWT claim.
     * It then matches this credential type against the `AuthTypeEnum` enumeration using the
     * `AuthTypeEnum.getByTypeFa` method and returns the corresponding authentication type.
     *
     * @return The authentication type string corresponding to the credential type.
     * @throws BadRequestException If the credential type does not match any in `AuthTypeEnum`.
     */
    public String getAuthType() {
        AuthTypeEnum authTypeEnum = AuthTypeEnum.getByTypeFa(getCredentialType());
        if (authTypeEnum == null) {
            LOG.errorv(
                    "Invalid authentication type for principal name '{0}'",
                    securityIdentity.getPrincipal().getName()
            );
            throw new BadRequestException("Invalid authentication type.");
        }
        return authTypeEnum.getType();
    }

    public String getFirstName() {
        return jwt.getClaim("given_name");
    }

    public String getLastName() {
        return jwt.getClaim("family_name");
    }
}