package models;

/**
 * Сущность, хранящаяся в LogginUsersRepository
 */
public class Domain {
    /** Целое число отвечающее за номер этапа авторизации пользователя */
    private int loginContext;

    /** Логин, под которым хочет зайти пользователь */
    private String requiredLogin;

    /** Платформа, через которую хочет зайти пользователь */
    private Platform fromPlatform;

    /** id пользователя на платформе, с которой хочет зайти пользователь */
    private String idOnPlatform;

    /** Платформа, на которую должен отправиться код подтверждения для входа */
    private Platform verificationPlatform;

    /** Код подтверждения, отправляемый на указанную пользователе платформу */
    private int verificationCode;

    /**
     * Пустой конструктор
     */
    public Domain() {
        setLoginContext(0);
        setRequiredLogin("required login");
        setFromPlatform(Platform.NO_PLATFORM);
        setIdOnPlatform("id_on_platform");
        setVerificationPlatform(Platform.NO_PLATFORM);
        setVerificationCode(1111);
    }

    /**
     * Полный конструктор
     */
    public Domain(int loginContext, String requiredLogin, Platform fromPlatform, String idOnPlatform, Platform verificationPlatform, int verificationCode) {
        setLoginContext(loginContext);
        setRequiredLogin(requiredLogin);
        setFromPlatform(fromPlatform);
        setIdOnPlatform(idOnPlatform);
        setVerificationPlatform(verificationPlatform);
        setVerificationCode(verificationCode);
    }

    public int getLoginContext() {
        return loginContext;
    }

    public void setLoginContext(int loginContext) {
        assert loginContext >= 0;
        this.loginContext = loginContext;
    }

    public String getRequiredLogin() {
        return requiredLogin;
    }

    public void setRequiredLogin(String requiredLogin) {
        assert requiredLogin != null;
        this.requiredLogin = requiredLogin;
    }

    public Platform getFromPlatform() {
        return fromPlatform;
    }

    public void setFromPlatform(Platform fromPlatform) {
        assert fromPlatform != null;
        this.fromPlatform = fromPlatform;
    }

    public String getIdOnPlatform() {
        return idOnPlatform;
    }

    public void setIdOnPlatform(String idOnPlatform) {
        assert idOnPlatform != null;
        this.idOnPlatform = idOnPlatform;
    }

    public Platform getVerificationPlatform() {
        return verificationPlatform;
    }

    public void setVerificationPlatform(Platform verificationPlatform) {
        assert verificationPlatform != null;
        this.verificationPlatform = verificationPlatform;
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(int verificationCode) {
        assert verificationCode >= 0;
        this.verificationCode = verificationCode;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "loginContext=" + loginContext +
                ", requiredLogin='" + requiredLogin + '\'' +
                ", fromPlatform=" + fromPlatform +
                ", idOnPlatform='" + idOnPlatform + '\'' +
                ", verificationPlatform=" + verificationPlatform +
                ", verificationCode=" + verificationCode +
                '}';
    }
}
