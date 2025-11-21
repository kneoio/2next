package io.kneo.core.service;

import io.kneo.core.repository.cnst.UserRegStatus;
import io.kneo.core.dto.document.UserDTO;
import io.kneo.core.model.Module;
import io.kneo.core.model.SimpleReferenceEntity;
import io.kneo.core.model.user.IUser;
import io.kneo.core.model.user.Role;
import io.kneo.core.model.user.SuperUser;
import io.kneo.core.model.user.User;
import io.kneo.core.repository.ModuleRepository;
import io.kneo.core.repository.RoleRepository;
import io.kneo.core.repository.UserRepository;
import io.kneo.core.service.exception.ServiceException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger("UserService");
    @Inject
    private UserRepository repository;
    @Inject
    private RoleService roleService;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private ModuleRepository moduleRepository;

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
        user.setEmail(dto.getLogin() + "_place_holder@kneo.io");
        user.setDefaultLang(0);
       return addOrGet(user, dto.getRoles(), dto.getModules(), allowAutoRegistration);
    }

    public Uni<Long> addOrGet(User user, List<String> newRoles, List<String> newModules, boolean allowAutoRegistration) {
        user.setDefaultLang(0);
        if (allowAutoRegistration) {
            user.setRegStatus(UserRegStatus.REGISTERED_AUTOMATICALLY);
        } else {
            user.setRegStatus(UserRegStatus.REGISTERED);
        }

        Uni<List<Role>> rolesUni = roleRepository.getAll(0, 1000);
        Uni<List<Module>> moduleUni = moduleRepository.getAll(0, 1000);

        return rolesUni.onItem().transformToUni(roles -> {
            user.setRoles(getAllValidReferences(roles, newRoles));
            return moduleUni;
        }).onFailure().recoverWithUni(failure -> {
            throw new ServiceException(failure);
        }).onItem().transformToUni(modules -> {
            try {
                user.setModules(getAllValidReferences(modules, newModules));
                return repository.insert(user, SuperUser.build());
            } catch (Exception e) {
                return Uni.createFrom().failure(e);
            }
        }).onFailure().recoverWithUni(throwable -> {
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


    public Uni<Long> delete(String id) {
        assert repository != null;
        return repository.delete(Long.valueOf(id));
    }

    public Uni<UserDTO> getDTO(long id, IUser requester, io.kneo.core.localization.LanguageCode languageCode) {
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

    private <T extends SimpleReferenceEntity> List<T> getAllValidReferences(List<T> allAvailable, List<String> provided) {
        List<T> allValidRoles = new ArrayList<>();
        for (T e : allAvailable) {
            if (provided.contains(e.getIdentifier())) {
                allValidRoles.add(e);
            }
        }
        return allValidRoles;
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
