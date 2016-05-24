package errors;

public enum PaErrors {
    UNSUPPORTED_ACTION("L'action demandée n'est pas supportée"),
    EMPTY_PARAMETERS("Les informations transmises sont vides"),
    PASSWORD_CONFIRM_MISMATCH("Le mot de passe est différent de la confirmation de mot passe"),
    INCORRECT_PARAMETERS("Les informations transmises sont invalides"),
    LOGIN_MISMATCH("Le nom d'utilisateur et/ou le mot de passe sont incorrects"),
    SQL_REQUEST_FAILED("La requête à échouée");

    private final String error;

    private PaErrors(String view) {
        this.error = view ;
    }

    @Override
    public String toString() {
        return this.error;
    }
}
