package pt.ama.model;

import lombok.Getter;

@Getter
public enum DocumentStatus {
    RASCUNHO ("Rascunho"),
    SUBMETIDO("Submetido"),
    A_AGUARDAR_VALIDACAO("A Aguardar Validação"),
    EM_VALIDACAO("Em Validação"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado"),
    EM_ESPERA_INFORMACAO("Em Espera de Informação"),
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String description;

    DocumentStatus(String description) { this.description = description; }

}