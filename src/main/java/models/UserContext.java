package models;

/**
 * Класс, позволяющий узнать, в каком состоянии находится пользователь (какая команда выполняетсся)
 * для последующей обработки его зароса. Необходим, когда взаимодействие с пользователем происходит
 * в форме диалога.
 */
public class UserContext {
    /** Состояние, в котором находится пользователь при взаимодействии с ботом. */
    private UserState state;

    /** Номер состояния, в котором находится пользователь. */
    private int stateNum;

    /**
     * Устанавливает {@link #state состояние пользователя} в {@link UserState NO_STATE},
     * {@link #stateNum номер состояния пользователя} в <b>0</b>
     */
    public UserContext() {
        state = UserState.NO_STATE;
        stateNum = 0;
    }

    /**
     * @param state состояние пользователя. не <b>null</b>.
     * @param stateNum номер состояния пользователя.
     */
    public UserContext(UserState state, int stateNum) {
        setState(state);
        setStateNum(stateNum);
    }

    /**
     * поле {@link #stateNum номер состояния пользователя} установит в 0.
     * @param state {@link #state состояние пользователя}. не <b>null</b>.
     */
    public UserContext(UserState state) {
        setState(state);
        stateNum = 0;
    }

    /** Возврашает {@link #state состояние пользователя}. */
    public UserState getState() {
        return state;
    }

    /** Возврашает {@link #stateNum номер состояния пользователя}. */
    public int getStateNum() {
        return stateNum;
    }

    /**
     * Устанавливает {@link #state состояние пользователя}.
     * @param state состояние пользователя. Не <b>null</b>.
     */
    public void setState(UserState state) throws IllegalArgumentException{
        if(state == null)
            throw new IllegalArgumentException("Try to make state, that not exist");
        this.state = state;
    }

    /**
     * Устанавливает {@link #stateNum номер состояния пользователя}.
     * @param stateNum номер состояния пользователя. Больше или равен <b>0</b>
     */
    public void setStateNum(int stateNum) {
        if(stateNum < 0)
            throw new IllegalArgumentException("Try to set negative state_num.");
        this.stateNum = stateNum;
    }

    /**Увеличивает  {@link #stateNum номер состояния пользователя} на <b>1</b>. */
    public void incrementStateNum() {
        stateNum++;
    }

    @Override
    public String toString() {
        return "UserContext{" +
                "state=" + state +
                ", stateNum=" + stateNum +
                '}';
    }
}

