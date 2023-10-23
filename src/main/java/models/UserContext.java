package models;

/**
 * Класс, позволяющий узнать, в каком состоянии находится пользователь (какая команда выполняетсся)
 * для последующей обработки его зароса. Необходим, когда взаимодействие с пользователем происходит
 * в форме диалога.
 */
public class UserContext {
    /**
     * Состояния, в которых может находится при взаимодействии с ботом.
     * Доступные state смотри в {@link #setState(String)}
     */
    private String state;
    private int stateNum;

    public UserContext(String state, int stateNum) {
        setState(state);
        setStateNum(stateNum);
    }

    /**
     * поле {@link #getState_num() stateNum} установит в 0.
     * @param state
     */
    public UserContext(String state) {
        setState(state);
        stateNum = 0;
    }

    public String getState() {
        return state;
    }

    public int getState_num() {
        return stateNum;
    }

    /**
     * Доступные state:
     * <ol>
     *  <li><i>alcohol_intoxication</i></li>
     * </ol>
     * @param state
     * @throws IllegalArgumentException
     */
    public void setState(String state) throws IllegalArgumentException{
        //todo возможно тут нужен enum?
        switch (state) {
            case "alcohol_intoxication":
                this.state = state;
                return;
        }
        throw new IllegalArgumentException("Try to make state, that not exist");
    }

    public void setStateNum(int stateNum) {
        if(stateNum < 0) {
            throw new IllegalArgumentException("Try to set negative state_num.");
        }
        this.stateNum = stateNum;
    }

    public void incrementStateNum() {
        stateNum++;
    }

    @Override
    public String toString() {
        return "UserContext:(state: %s, state_num: %d)".formatted(state, stateNum);
    }
}

