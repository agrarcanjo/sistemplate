package pt.ama.enums.oauth2;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
public enum UserAttributeEnum {

    MDC_CIDADAO_NIC("cidadao_nic", "NIC", "http://interop.gov.pt/MDC/Cidadao/NIC"),
    MDC_CIDADAO_NOMEPROPRIO("cidadao_nomeproprio", UserAttributeKey.FIRST_NAME, "http://interop.gov.pt/MDC/Cidadao/NomeProprio"),
    MDC_CIDADAO_NOMEAPELIDO("cidadao_nomeapelido", UserAttributeKey.LAST_NAME, "http://interop.gov.pt/MDC/Cidadao/NomeApelido"),
    MDC_CIDADAO_DATANASCIMENTO("cidadao_datanascimento", UserAttributeKey.BIRTH_DATE, "http://interop.gov.pt/MDC/Cidadao/DataNascimento"),
    MDC_CIDADAO_NOMECOMPLETO("cidadao_nomecompleto", UserAttributeKey.CITIZEN_FULL_NAME, "http://interop.gov.pt/MDC/Cidadao/NomeCompleto"),
    MDC_CIDADAO_NIF("cidadao_nif", "NIF", "http://interop.gov.pt/MDC/Cidadao/NIF"),
    MDC_CIDADAO_NISS("cidadao_niss", "NISS", "http://interop.gov.pt/MDC/Cidadao/NISS"),
    MDC_CIDADAO_NSNS("cidadao_nsns", "NSNS", "http://interop.gov.pt/MDC/Cidadao/NSNS"),
    MDC_CIDADAO_NIFCIFRADO("cidadao_nifcifrado", "NIFCifrado", "http://interop.gov.pt/MDC/Cidadao/NIFCifrado"),
    MDC_CIDADAO_NISSCIFRADO("cidadao_nisscifrado", "NISSCifrado", "http://interop.gov.pt/MDC/Cidadao/NISSCifrado"),
    MDC_CIDADAO_NICCIFRADO("cidadao_niccifrado", "NICCifrado", "http://interop.gov.pt/MDC/Cidadao/NICCifrado"),
    MDC_CIDADAO_NSNSCIFRADO("cidadao_nsnscifrado", "NSNSCifrado", "http://interop.gov.pt/MDC/Cidadao/NSNSCifrado"),
    MDC_CIDADAO_DATAVALIDADE("cidadao_datavalidade", UserAttributeKey.EXPIRATION_DATE, "http://interop.gov.pt/MDC/Cidadao/DataValidade"),
    MDC_CIDADAO_DATAVALIDADEDOC("cidadao_datavalidadedoc", "DataValidadeDoc", "http://interop.gov.pt/MDC/Cidadao/DataValidadeDoc"),
    MDC_CIDADAO_SEXO("cidadao_sexo", "Sexo", "http://interop.gov.pt/MDC/Cidadao/Sexo"),
    MDC_CIDADAO_NACIONALIDADE("cidadao_nacionalidade", UserAttributeKey.NATIONALITY, "http://interop.gov.pt/MDC/Cidadao/Nacionalidade"),
    MDC_CIDADAO_CORREIOELECTRONICO("cidadao_correioelectronico", UserAttributeKey.EMAIL, "http://interop.gov.pt/MDC/Cidadao/CorreioElectronico"),
    MDC_CIDADAO_IDADE("cidadao_idade", "Idade", "http://interop.gov.pt/MDC/Cidadao/Idade"),
    MDC_CIDADAO_IDADESUPERIORA("cidadao_idadesuperiora", "IdadeSuperiorA", "http://interop.gov.pt/MDC/Cidadao/IdadeSuperiorA"),
    MDC_CIDADAO_NUMEROSERIE("cidadao_numeroserie", UserAttributeKey.SERIAL_NUMBER, "http://interop.gov.pt/MDC/Cidadao/NumeroSerie"),
    MDC_CIDADAO_DOCTYPE("cidadao_doctype", "DocType", "http://interop.gov.pt/MDC/Cidadao/DocType"),
    MDC_CIDADAO_DOCNATIONALITY("cidadao_docnationality", "DocNationality", "http://interop.gov.pt/MDC/Cidadao/DocNationality"),
    MDC_CIDADAO_DOCNUMBER("cidadao_docnumber", "DocNumber", "http://interop.gov.pt/MDC/Cidadao/DocNumber"),
    MDC_ADVOGADO_SOCIEDADE("advogado_sociedade", "Sociedade", "http://interop.gov.pt/MDC/Advogado/Sociedade"),
    MDC_ADVOGADO_NSOCIEDADE("advogado_nsociedade", "NSociedade", "http://interop.gov.pt/MDC/Advogado/NSociedade"),
    MDC_ADVOGADO_NOMEPROFISSIONAL("advogado_nomeprofissional", "NomeProfissional", "http://interop.gov.pt/MDC/Advogado/NomeProfissional"),
    MDC_ADVOGADO_NOA("advogado_noa", "NOA", "http://interop.gov.pt/MDC/Advogado/NOA"),
    MDC_ADVOGADO_CORREIOELECTRONICO("advogado_correioelectronico", UserAttributeKey.EMAIL, "http://interop.gov.pt/MDC/Advogado/CorreioElectronico"),
    MDC_ADVOGADO_NOMECOMPLETO("advogado_nomecompleto", UserAttributeKey.CITIZEN_FULL_NAME, "http://interop.gov.pt/MDC/Advogado/NomeCompleto"),
    MDC_ADVOGADO_NUMEROSERIE("advogado_numeroserie", UserAttributeKey.SERIAL_NUMBER, "http://interop.gov.pt/MDC/Advogado/NumeroSerie"),
    MDC_SOLICITADOR_NCS("solicitador_ncs", "NCS", "http://interop.gov.pt/MDC/Solicitador/NCS"),
    MDC_SOLICITADOR_NOMECOMPLETO("solicitador_nomecompleto", UserAttributeKey.CITIZEN_FULL_NAME, "http://interop.gov.pt/MDC/Solicitador/NomeCompleto"),
    MDC_SOLICITADOR_CORREIOELECTRONICO("solicitador_correioelectronico", UserAttributeKey.EMAIL, "http://interop.gov.pt/MDC/Solicitador/CorreioElectronico"),
    MDC_SOLICITADOR_NUMEROSERIE("solicitador_numeroserie", UserAttributeKey.SERIAL_NUMBER, "http://interop.gov.pt/MDC/Solicitador/NumeroSerie"),
    MDC_NOTARIO_NON("notario_non", "NON", "http://interop.gov.pt/MDC/Notario/NON"),
    MDC_NOTARIO_NOMEPROPRIO("notario_nomeproprio", UserAttributeKey.FIRST_NAME, "http://interop.gov.pt/MDC/Notario/NomeProprio"),
    MDC_NOTARIO_NOMECOMPLETO("notario_nomecompleto", UserAttributeKey.CITIZEN_FULL_NAME, "http://interop.gov.pt/MDC/Notario/NomeCompleto"),
    MDC_NOTARIO_NOMEAPELIDO("notario_nomeapelido", UserAttributeKey.LAST_NAME, "http://interop.gov.pt/MDC/Notario/NomeApelido"),
    MDC_NOTARIO_NUMEROSERIE("notario_numeroserie", UserAttributeKey.SERIAL_NUMBER, "http://interop.gov.pt/MDC/Notario/NumeroSerie"),
    MDC_NOTARIO_NOMECARTORIONOTARIAL("notario_nomecartorionotarial", "NomeCartorioNotarial", "http://interop.gov.pt/MDC/Notario/NomeCartorioNotarial"),
    MDC_NOTARIO_LOCALIDADECARTORIONOTARIAL("notario_localidadecartorionotarial", "LocalidadeCartorioNotarial", "http://interop.gov.pt/MDC/Notario/LocalidadeCartorioNotarial"),
    MDC_NOTARIO_DISTRITOCARTORIONOTARIAL("notario_distritocartorionotarial", "DistritoCartorioNotarial", "http://interop.gov.pt/MDC/Notario/DistritoCartorioNotarial"),
    MDC_NOTARIO_NACIONALIDADE("notario_nacionalidade", UserAttributeKey.NATIONALITY, "http://interop.gov.pt/MDC/Notario/Nacionalidade"),
    MDC_CIDADAO_FOTO("cidadao_foto", "Foto", "http://interop.gov.pt/MDC/Cidadao/Foto"),
    MDC_CIDADAO_ALTURA("cidadao_altura", "Altura", "http://interop.gov.pt/MDC/Cidadao/Altura"),
    MDC_CIDADAO_NOMEPROPRIOPAI("cidadao_nomepropriopai", "NomeProprioPai", "http://interop.gov.pt/MDC/Cidadao/NomeProprioPai"),
    MDC_CIDADAO_NOMEAPELIDOPAI("cidadao_nomeapelidopai", "NomeApelidoPai", "http://interop.gov.pt/MDC/Cidadao/NomeApelidoPai"),
    MDC_CIDADAO_NOMEPROPRIOMAE("cidadao_nomepropriomae", "NomeProprioMae", "http://interop.gov.pt/MDC/Cidadao/NomeProprioMae"),
    MDC_CIDADAO_NOMEAPELIDOMAE("cidadao_nomeapelidomae", "NomeApelidoMae", "http://interop.gov.pt/MDC/Cidadao/NomeApelidoMae"),
    MDC_CIDADAO_INDICACOESEVENTUAIS("cidadao_indicacoeseventuais", "IndicacoesEventuais", "http://interop.gov.pt/MDC/Cidadao/IndicacoesEventuais"),
    MDC_CIDADAO_NODOCUMENTO("cidadao_nodocumento", "NoDocumento", "http://interop.gov.pt/MDC/Cidadao/NoDocumento"),
    MDC_CIDADAO_MRZ1("cidadao_mrz1", "mrz1", "http://interop.gov.pt/MDC/Cidadao/mrz1"),
    MDC_CIDADAO_MRZ2("cidadao_mrz2", "mrz2", "http://interop.gov.pt/MDC/Cidadao/mrz2"),
    MDC_CIDADAO_MRZ3("cidadao_mrz3", "mrz3", "http://interop.gov.pt/MDC/Cidadao/mrz3"),
    MDC_CIDADAO_VERSAOCARTAO("cidadao_versaocartao", "VersaoCartao", "http://interop.gov.pt/MDC/Cidadao/VersaoCartao"),
    MDC_CIDADAO_CARTAOPAN("cidadao_cartaopan", "CartaoPAN", "http://interop.gov.pt/MDC/Cidadao/CartaoPAN"),
    MDC_CIDADAO_DATAEMISSAO("cidadao_dataemissao", "DataEmissao", "http://interop.gov.pt/MDC/Cidadao/DataEmissao"),
    MDC_CIDADAO_ENTIDADEEMISSORA("cidadao_entidadeemissora", "EntidadeEmissora", "http://interop.gov.pt/MDC/Cidadao/EntidadeEmissora"),
    MDC_CIDADAO_TIPODOCUMENTO("cidadao_tipodocumento", "TipoDocumento", "http://interop.gov.pt/MDC/Cidadao/TipoDocumento"),
    MDC_CIDADAO_LOCALDEPEDIDO("cidadao_localdepedido", "LocalDePedido", "http://interop.gov.pt/MDC/Cidadao/LocalDePedido"),
    MDC_CIDADAO_VERSAO("cidadao_versao", "Versao", "http://interop.gov.pt/MDC/Cidadao/Versao"),
    MDC_CIDADAO_BLOCONOTAS("cidadao_bloconotas", "BlocoNotas", "http://interop.gov.pt/MDC/Cidadao/BlocoNotas"),
    MDC_CIDADAO_NUMERODECONTROLO("cidadao_numerodecontrolo", "NumeroDeControlo", "http://interop.gov.pt/MDC/Cidadao/NumeroDeControlo"),
    MDC_ECCE_CARGODOTITULAR("ecce_cargodotitular", "CargoDoTitular", "http://interop.gov.pt/MDC/ECCE/CargoDoTitular"),
    MDC_ECCE_MICROSOFTUPN("ecce_microsoftupn", "MicrosoftUpn", "http://interop.gov.pt/MDC/ECCE/MicrosoftUpn"),
    MDC_ECCE_MINISTERIO("ecce_ministerio", "Ministerio", "http://interop.gov.pt/MDC/ECCE/Ministerio"),
    MDC_ECCE_NOME("ecce_nome", "Nome", "http://interop.gov.pt/MDC/ECCE/Nome"),
    MDC_ECCE_ORGANISMO("ecce_organismo", "Organismo", "http://interop.gov.pt/MDC/ECCE/Organismo"),
    MDC_ECCE_PAIS("ecce_pais", "Pais", "http://interop.gov.pt/MDC/ECCE/Pais"),
    DADOSCC_CIDADAO_ALTURA("dadoscc_cidadao_altura", "Altura", "http://interop.gov.pt/DadosCC/Cidadao/Altura"),
    DADOSCC_CIDADAO_ASSINATURA("dadoscc_cidadao_assinatura", "Assinatura", "http://interop.gov.pt/DadosCC/Cidadao/Assinatura"),
    DADOSCC_CIDADAO_CONTACTOSXML("dadoscc_cidadao_contactosxml", "ContactosXML", "http://interop.gov.pt/DadosCC/Cidadao/ContactosXML"),
    DADOSCC_CIDADAO_CORREIOELECTRONICO("dadoscc_cidadao_correioelectronico", UserAttributeKey.EMAIL, "http://interop.gov.pt/DadosCC/Cidadao/CorreioElectronico"),
    DADOSCC_CIDADAO_DATANASCIMENTO("dadoscc_cidadao_datanascimento", UserAttributeKey.BIRTH_DATE, "http://interop.gov.pt/DadosCC/Cidadao/DataNascimento"),
    DADOSCC_CIDADAO_DATAVALIDADE("dadoscc_cidadao_datavalidade", UserAttributeKey.EXPIRATION_DATE, "http://interop.gov.pt/DadosCC/Cidadao/DataValidade"),
    DADOSCC_CIDADAO_FOTO("dadoscc_cidadao_foto", "Foto", "http://interop.gov.pt/DadosCC/Cidadao/Foto"),
    DADOSCC_CIDADAO_INDICATIVOTELEFONEMOVEL("dadoscc_cidadao_indicativotelefonemovel", "IndicativoTelefoneMovel", "http://interop.gov.pt/DadosCC/Cidadao/IndicativoTelefoneMovel"),
    DADOSCC_CIDADAO_NACIONALIDADE("dadoscc_cidadao_nacionalidade", UserAttributeKey.NATIONALITY, "http://interop.gov.pt/DadosCC/Cidadao/Nacionalidade"),
    DADOSCC_CIDADAO_NODOCUMENTO("dadoscc_cidadao_nodocumento", "NoDocumento", "http://interop.gov.pt/DadosCC/Cidadao/NoDocumento"),
    DADOSCC_CIDADAO_NOMEAPELIDO("dadoscc_cidadao_nomeapelido", UserAttributeKey.LAST_NAME, "http://interop.gov.pt/DadosCC/Cidadao/NomeApelido"),
    DADOSCC_CIDADAO_NOMEAPELIDOMAE("dadoscc_cidadao_nomeapelidomae", "NomeApelidoMae", "http://interop.gov.pt/DadosCC/Cidadao/NomeApelidoMae"),
    DADOSCC_CIDADAO_NOMEAPELIDOPAI("dadoscc_cidadao_nomeapelidopai", "NomeApelidoPai", "http://interop.gov.pt/DadosCC/Cidadao/NomeApelidoPai"),
    DADOSCC_CIDADAO_NOMEPROPRIO("dadoscc_cidadao_nomeproprio", UserAttributeKey.FIRST_NAME, "http://interop.gov.pt/DadosCC/Cidadao/NomeProprio"),
    DADOSCC_CIDADAO_NOMEPROPRIOMAE("dadoscc_cidadao_nomepropriomae", "NomeProprioMae", "http://interop.gov.pt/DadosCC/Cidadao/NomeProprioMae"),
    DADOSCC_CIDADAO_NOMEPROPRIOPAI("dadoscc_cidadao_nomepropriopai", "NomeProprioPai", "http://interop.gov.pt/DadosCC/Cidadao/NomeProprioPai"),
    DADOSCC_CIDADAO_NUMEROTELEFONEMOVEL("dadoscc_cidadao_numerotelefonemovel", "NumeroTelefoneMovel", "http://interop.gov.pt/DadosCC/Cidadao/NumeroTelefoneMovel"),
    DADOSCC_CIDADAO_SEXO("dadoscc_cidadao_sexo", "Sexo", "http://interop.gov.pt/DadosCC/Cidadao/Sexo"),
    DADOSCC_CIDADAO_MORADAXML("dadoscc_cidadao_moradaxml", "MoradaXML", "http://interop.gov.pt/DadosCC/Cidadao/MoradaXML"),
    ADSE_CIDADAO_NOME("adse_cidadao_nome", "Nome", "http://interop.gov.pt/ADSE/Cidadao/Nome"),
    ADSE_CIDADAO_NUMEROBENEFICIARIO("adse_cidadao_numerobeneficiario", "NumeroBeneficiario", "http://interop.gov.pt/ADSE/Cidadao/NumeroBeneficiario"),
    ADSE_CIDADAO_QUALIDADE("adse_cidadao_qualidade", "Qualidade", "http://interop.gov.pt/ADSE/Cidadao/Qualidade"),
    ADSE_CIDADAO_SITUACAO("adse_cidadao_situacao", "Situacao", "http://interop.gov.pt/ADSE/Cidadao/Situacao"),
    ADSE_CIDADAO_DATAVALIDADE("adse_cidadao_datavalidade", UserAttributeKey.EXPIRATION_DATE, "http://interop.gov.pt/ADSE/Cidadao/DataValidade"),
    IMTT_CIDADAO_NOMEPROPRIO("imtt_cidadao_nomeproprio", UserAttributeKey.FIRST_NAME, "http://interop.gov.pt/IMTT/Cidadao/NomeProprio"),
    IMTT_CIDADAO_NOMEAPELIDO("imtt_cidadao_nomeapelido", UserAttributeKey.LAST_NAME, "http://interop.gov.pt/IMTT/Cidadao/NomeApelido"),
    IMTT_CIDADAO_LOCALNASCIMENTO("imtt_cidadao_localnascimento", "LocalNascimento", "http://interop.gov.pt/IMTT/Cidadao/LocalNascimento"),
    IMTT_CIDADAO_DATANASCIMENTO("imtt_cidadao_datanascimento", UserAttributeKey.BIRTH_DATE, "http://interop.gov.pt/IMTT/Cidadao/DataNascimento"),
    IMTT_CIDADAO_NOCARTA("imtt_cidadao_nocarta", "NoCarta", "http://interop.gov.pt/IMTT/Cidadao/NoCarta"),
    IMTT_CIDADAO_DATAEMISSAO("imtt_cidadao_dataemissao", "DataEmissao", "http://interop.gov.pt/IMTT/Cidadao/DataEmissao"),
    IMTT_CIDADAO_ENTIDADEEMISSORA("imtt_cidadao_entidadeemissora", "EntidadeEmissora", "http://interop.gov.pt/IMTT/Cidadao/EntidadeEmissora"),
    IMTT_CIDADAO_ESTADO("imtt_cidadao_estado", "Estado", "http://interop.gov.pt/IMTT/Cidadao/Estado"),
    IMTT_CIDADAO_CATEGORIAS("imtt_cidadao_categorias", "Categorias", "http://interop.gov.pt/IMTT/Cidadao/Categorias"),
    IMTT_CIDADAO_DIGITOCARTA("imtt_cidadao_digitocarta", "DigitoCarta", "http://interop.gov.pt/IMTT/Cidadao/DigitoCarta"),
    IMTT_CIDADAO_DIGITOCONTROLO("imtt_cidadao_digitocontrolo", "DigitoControlo", "http://interop.gov.pt/IMTT/Cidadao/DigitoControlo"),
    IMTT_CIDADAO_NOCONTROLO("imtt_cidadao_nocontrolo", "NoControlo", "http://interop.gov.pt/IMTT/Cidadao/NoControlo"),
    MDC_CIDADAO_NUMEROTELEMOVEL("cidadao_numerotelemovel", "NumeroTelemovel", "http://interop.gov.pt/MDC/Cidadao/NumeroTelemovel"),
    MDC_CIDADAO_CODIGONACIONALIDADE("cidadao_codigonacionalidade", "CodigoNacionalidade", "http://interop.gov.pt/MDC/Cidadao/CodigoNacionalidade"),
    HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_PERSONIDENTIFIER("http_eidas_europa_eu_attributes_naturalperson_personidentifier", "PersonIdentifier", "http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier"),
    HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_CURRENTFAMILYNAME("http_eidas_europa_eu_attributes_naturalperson_currentfamilyname", "CurrentFamilyName", "http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName"),
    HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_CURRENTGIVENNAME("http_eidas_europa_eu_attributes_naturalperson_currentgivenname", "CurrentGivenName", "http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName"),
    HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_DATEOFBIRTH("http_eidas_europa_eu_attributes_naturalperson_dateofbirth", "DateOfBirth", "http://eidas.europa.eu/attributes/naturalperson/DateOfBirth"),
    HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_CURRENTADDRESS("http_eidas_europa_eu_attributes_naturalperson_currentaddress", "CurrentAddress", "http://eidas.europa.eu/attributes/naturalperson/CurrentAddress"),
    HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_GENDER("http_eidas_europa_eu_attributes_naturalperson_gender", "Gender", "http://eidas.europa.eu/attributes/naturalperson/Gender"),
    HTTP_EIDAS_EUROPA_EU_ATTRIBUTES_NATURALPERSON_PLACEOFBIRTH("http_eidas_europa_eu_attributes_naturalperson_placeofbirth", "PlaceOfBirth", "http://eidas.europa.eu/attributes/naturalperson/PlaceOfBirth"),
    EIDAS_NISS("http_aima_europa_eu_attributes_niss", "NISS", "http://interop.gov.pt/AIMA/NISS"),
    EIDAS_NSNS("http_aima_europa_eu_attributes_nsns", "NSNS", "http://interop.gov.pt/AIMA/NSNS"),
    EIDAS_NIE("http_aima_europa_eu_attributes_nie", "NIE", "http://interop.gov.pt/MDC/Cidadao/NIE"),
    SEGURANCASOCIAL_SITUACAOPROFISSIONAL("segurancasocial_situacaoprofissional", "SituacaoProfissional", "http://interop.gov.pt/SegurancaSocial/SituacaoProfissional"),
    SCAP_ENTERPRISE("scap_enterprise", "ENTERPRISE", "http://interop.gov.pt/SCAP/ENTERPRISE"),
    SCAP_FAF("scap_faf", "FAF", "http://interop.gov.pt/SCAP/FAF"),
    SCAP("scap", "SCAP", "http://interop.gov.pt/SCAP"),
    MDC_CIDADAO_TEMASSINATURADIGITALCMD("cidadao_temassinaturadigitalcmd", "TemAssinaturaDigitalCMD", "http://interop.gov.pt/MDC/Cidadao/TemAssinaturaDigitalCMD");

    private final String id;
    private final String key;
    private final String url;

    UserAttributeEnum(String id, String key, String url) {
        this.id = id;
        this.key = key;
        this.url = url;
    }

    public static Optional<UserAttributeEnum> getById(String id) {
        return getBy(userAttributeEnum -> userAttributeEnum.id.equals(id));
    }

    public static Optional<UserAttributeEnum> getByUrl(String url) {
        return getBy(userAttributeEnum -> userAttributeEnum.url.equals(url));
    }

    private static Optional<UserAttributeEnum> getBy(Predicate<? super UserAttributeEnum> predicate) {
        return Arrays
                .stream(UserAttributeEnum.values())
                .filter(predicate)
                .findFirst();
    }

    private static class UserAttributeKey {
        public static final String FIRST_NAME = "NomeProprio";
        public static final String LAST_NAME = "NomeApelido";
        public static final String BIRTH_DATE = "DataNascimento";
        public static final String CITIZEN_FULL_NAME = "NomeCompleto";
        public static final String EXPIRATION_DATE = "DataValidade";
        public static final String NATIONALITY = "Nacionalidade";
        public static final String EMAIL = "CorreioElectronico";
        public static final String SERIAL_NUMBER = "NumeroSerie";
    }

    public boolean isIdentity() {
        return this.equals(UserAttributeEnum.MDC_CIDADAO_NOMEPROPRIO) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_NOMEAPELIDO) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_DATANASCIMENTO) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_SEXO) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_NACIONALIDADE) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_ALTURA) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_NOMEPROPRIOPAI) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_NOMEAPELIDOPAI) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_NOMEAPELIDOMAE) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_NOMEPROPRIOMAE);
    }

    public boolean isSS() {
        return this.equals(UserAttributeEnum.MDC_CIDADAO_NISS);
    }

    public boolean isSns() {
        return this.equals(UserAttributeEnum.MDC_CIDADAO_NSNS);
    }

    public boolean isFiscal() {
        return this.equals(UserAttributeEnum.MDC_CIDADAO_NIF);
    }

    public boolean isCivil() {
        return this.equals(UserAttributeEnum.MDC_CIDADAO_NIC) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_NUMERODECONTROLO) ||
                this.equals(UserAttributeEnum.MDC_CIDADAO_DATAVALIDADEDOC) ||
                this.equals(UserAttributeEnum.DADOSCC_CIDADAO_DATAVALIDADE);
    }

    public boolean isAddress() {
        return this.equals(UserAttributeEnum.DADOSCC_CIDADAO_MORADAXML);
    }

}