package com.semantyca.core.service;

import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.repository.cnst.UserRegStatus;
import com.semantyca.core.dto.document.UserDTO;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.model.user.SuperUser;
import com.semantyca.core.model.user.User;
import com.semantyca.core.repository.UserRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    private static final Logger LOGGER = Logger.getLogger("UserService");
    @Inject
    private UserRepository repository;

    public Uni<List<User>> getAll() {
        return repository.getAll();
    }

    public Uni<List<UserDTO>> getAll(final int limit, final int offset) {
        assert repository != null;
        return repository.getAll(limit, offset)
                .chain(list -> {
                    List<Uni<UserDTO>> unis = list.stream()
                            .map(this::mapToDTO)
                            .collect(Collectors.toList());
                    return Uni.join().all(unis).andFailFast();
                });
    }

    public Uni<Integer> getAllCount() {
        assert repository != null;
        return repository.getAllCount("_users");
    }

    public Uni<List<User>> search(String keyword) {
        return repository.search(keyword);
    }

    public Multi<IUser> getAllStream() {
        return repository.getAllStream();
    }

    public Uni<Optional<IUser>> get(String id) {
        return repository.get(Long.parseLong(id));
    }

    public Uni<Optional<IUser>> get(long id) {
        return repository.get(id);
    }

    public Uni<Long> resolveIdentifier(String identifier) {
        return repository.findByIdentifier(identifier);
    }

    public Uni<IUser> findByLogin(String login) {
        return repository.findByLogin(login);
    }

    public Uni<IUser> findByTelegramId(String id) {
        return repository.findByTelegramId(id);
    }

    public Uni<IUser> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Uni<Optional<IUser>> findById(long id) {
        return repository.findById(id);
    }

    public Uni<String> getName(long id) {
        return repository.getUserName(id);
    }

    public Uni<String> getUserName(long id) {
        return repository.getUserName(id);
    }

    public Uni<Long> add(UserDTO dto, boolean allowAutoRegistration) {
        User user = new User();
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setDefaultLang(0);
        return addOrGet(user, allowAutoRegistration);
    }

    public Uni<Long> addOrGet(User user, boolean allowAutoRegistration) {
        user.setDefaultLang(0);
        if (allowAutoRegistration) {
            user.setRegStatus(UserRegStatus.REGISTERED_AUTOMATICALLY);
        } else {
            user.setRegStatus(UserRegStatus.REGISTERED);
        }

        return repository.insert(user, SuperUser.build())
                .onFailure().recoverWithUni(throwable -> {
                    if (allowAutoRegistration && throwable instanceof RuntimeException
                            && throwable.getCause() instanceof PgException pgException) {
                        if ("23505".equals(pgException.getSqlState()) && pgException.getMessage().contains("_users_login_key")) {
                            return repository.findByLogin(user.getLogin())
                                    .onItem().transform(IUser::getId);
                        }
                    }
                    return Uni.createFrom().failure(throwable);
                });
    }

    public Uni<Long> upsert(String id, UserDTO userDTO, IUser actor) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setEmail(userDTO.getEmail() != null ? userDTO.getEmail() : userDTO.getLogin() + "_place_holder@kneo.io");

        if (id == null || "new".equalsIgnoreCase(id)) {
            return repository.insert(user, actor);
        } else {
            try {
                user.setId(Long.parseLong(id));
            } catch (NumberFormatException e) {
                return Uni.createFrom().failure(e);
            }
            return repository.update(user, actor);
        }
    }

    public Uni<Void> updateEmail(Long userId, String email, IUser actor) {
        return repository.updateEmail(userId, email, actor)
                .onItem().transform(count -> null);
    }

    public Uni<Long> delete(String id) {
        assert repository != null;
        return repository.delete(Long.valueOf(id));
    }

    public Uni<UserDTO> getDTO(long id, IUser requester, LanguageCode languageCode) {
        return repository.get(id).chain(opt -> {
            if (opt.isEmpty()) {
                return Uni.createFrom().failure(new IllegalArgumentException("User not found"));
            }
            IUser iu = opt.get();
            if (iu instanceof User) {
                return mapToDTO((User) iu);
            } else {
                UserDTO dto = new UserDTO();
                try {
                    dto.setName(iu.getUserName());
                } catch (Exception ignored) {}
                try {
                    dto.setLogin(iu.getLogin());
                } catch (Exception ignored) {}
                try {
                    dto.setEmail(iu.getEmail());
                } catch (Exception ignored) {}
                return Uni.createFrom().item(dto);
            }
        });
    }

    private Uni<UserDTO> mapToDTO(User doc) {
        return Uni.combine().all().unis(
                getName(doc.getAuthor()),
                getName(doc.getLastModifier())
        ).asTuple().onItem().transform(tuple -> {
                    UserDTO dto = new UserDTO();
                    dto.setName(doc.getUserName());
                    dto.setEmail(doc.getEmail());
                    dto.setLogin(doc.getLogin());
                    return dto;
                }
        );
    }

}
