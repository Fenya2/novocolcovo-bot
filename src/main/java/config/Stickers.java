package config;

public enum Stickers {
    GREETINGS("CAACAgIAAxkBAAICcWVAvz6fc7rHLppAb8KyIgp1IlDxAAK-FwACULcJS3QwQiBxVE10MwQ");
    private String token;
    Stickers(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
