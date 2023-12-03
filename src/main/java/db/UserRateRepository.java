package db;

import org.apache.log4j.Logger;

public class UserRateRepository extends Repository{
    /** логер */
    private static final Logger log = Logger.getLogger(UserContextRepository.class.getName());

    /** Репозиторий пользователей */
    private final UserRepository userRepository;
    public UserRateRepository(DB db, UserRepository userRepository) {
        super(db);
        this.userRepository = userRepository;
    }

    
}
