package pt.ama.enums.oauth2;

import lombok.Getter;

@Getter
public enum UserTypeEnum {

    CMD_NIC("CES:CT:000000001"),
    CMD_PASSPORT_AND_RESIDENCE("CES:CT:000000200"),
    NOTARY("CES:CT:000000016"),
    LAWYER("CES:CT:000000014"),
    SOLICITATOR("CES:CT:000000015"),
    SIMPLE("7"),
    SOCIAL_NETWORK("4"),
    EIDAS("CES:CT:000000100");

    private final String id;

    UserTypeEnum(String id) {
        this.id = id;
    }

}
