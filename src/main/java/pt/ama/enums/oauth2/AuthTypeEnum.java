package pt.ama.enums.oauth2;

import jakarta.ws.rs.BadRequestException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public enum AuthTypeEnum {

    CC("NIC", "citizen" , "NCC", 3, UserTypeEnum.CMD_NIC.getId()),
    NOTARY("NON", "notary", "NON", 4, UserTypeEnum.NOTARY.getId()),
    LAWYER("NOA","lawyer", "NOA", 4, UserTypeEnum.LAWYER.getId()),
    SOLICITATOR("NCS", "requester", "NCS", 4, UserTypeEnum.SOLICITATOR.getId()),
    SIMPLE("EMAIL", "citizen" , "EMAIL", 1, UserTypeEnum.SIMPLE.getId()),
    CMD_NIC("NICCMD","citizen",  "NCC", 3, UserTypeEnum.CMD_NIC.getId()),
    CMD_NIC_EMAIL("NICCMDEMAIL","citizen",  "NCC", 2, UserTypeEnum.CMD_NIC.getId()),
    SOCIAL_NETWORK("SOCIAL","citizen",  "NCC", 1, UserTypeEnum.SOCIAL_NETWORK.getId()),
    EIDAS("EIDAS", "eidas", "EIDAS", 2, UserTypeEnum.EIDAS.getId()),
    CMD_TELEMOVEL("NIC", "citizen", "NCC", 3, UserTypeEnum.CMD_NIC.getId()),
    CMD_DOCUMENT("DOC", "foreigner", "CMDESTRANGEIRO", 3, UserTypeEnum.CMD_PASSPORT_AND_RESIDENCE.getId());

    private final String type;
    private final String typeFa;
    private final String typePmc;
    private final int authLevel;
    private final String userType;

    AuthTypeEnum(String type, String typeFa, String typePmc, int authLevel, String userType) {
        this.type = type;
        this.typeFa = typeFa;
        this.typePmc = typePmc;
        this.authLevel = authLevel;
        this.userType = userType;
    }

    public static Optional<AuthTypeEnum> getByType(String authType) {
        return Arrays
                .stream(AuthTypeEnum.values())
                .filter(authTypeEnum -> authTypeEnum.getType().equals(authType))
                .findFirst();
    }

    public String getTypeName() {
        if (List.of(AuthTypeEnum.CC, AuthTypeEnum.CMD_NIC, AuthTypeEnum.CMD_NIC_EMAIL).contains(this)) {
            return AuthTypeEnum.CC.getType();
        }

        return this.getType();
    }

    public static AuthTypeEnum getByTypeFa(String value) {
        return Arrays.stream(AuthTypeEnum.values())
                .filter(authTypeEnum -> authTypeEnum.getTypeFa().equals(value))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid authentication type."));
    }

    public static AuthTypeEnum getByValue(String authType) {
        return AuthTypeEnum.getByType(authType).orElseThrow(() -> new BadRequestException("Invalid authentication type."));
    }

}
