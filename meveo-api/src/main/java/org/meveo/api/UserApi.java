package org.meveo.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.User4_2Dto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UserApi extends BaseApi {

    @Inject
    private ProviderService providerService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    public void create4_2(User4_2Dto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUsername())) {
            missingParameters.add("username");
        }
        if (StringUtils.isBlank(postData.getEmail())) {
            missingParameters.add("email");
        }
        if (StringUtils.isBlank(postData.getProvider())) {
            missingParameters.add("provider");
        }
        if (StringUtils.isBlank(postData.getRole())) {
            missingParameters.add("role");
        }

        handleMissingParameters();

        // find provider
        Provider provider = providerService.findByCode(postData.getProvider());
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
        }

        // check if the user already exists
        if (userService.findByUsername(postData.getUsername()) != null) {
            throw new EntityAlreadyExistsException(User.class, postData.getUsername(), "username");
        }

        // find role
        Role role = roleService.findByName(postData.getRole(), currentUser.getProvider());
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, postData.getRole());
        }

        User user = new User();
        user.setUserName(postData.getUsername().toUpperCase());
        user.setEmail((postData.getEmail()));
        Name name = new Name();
        name.setLastName(postData.getLastName());
        name.setFirstName(postData.getFirstName());
        user.setName(name);
        user.setPassword(postData.getPassword());
        user.setLastPasswordModification(new Date());
        user.setProvider(provider);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        user.setRoles(roles);

        userService.create(user, currentUser);
    }
    
    public void create(UserDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUsername())) {
            missingParameters.add("username");
        }
        if (StringUtils.isBlank(postData.getEmail())) {
            missingParameters.add("email");
        }
        if (StringUtils.isBlank(postData.getProvider())) {
            missingParameters.add("provider");
        }
        if (postData.getRoles() == null || postData.getRoles().isEmpty()) {
            missingParameters.add("roles");
        }

        handleMissingParameters();

        // find provider
        Provider provider = providerService.findByCode(postData.getProvider());
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
        }

        // check if the user already exists
        if (userService.findByUsername(postData.getUsername()) != null) {
            throw new EntityAlreadyExistsException(User.class, postData.getUsername(), "username");
        }

        // find role
        Set<Role> roles = new HashSet<Role>();
        for(String rl : postData.getRoles()) {
        	Role role = roleService.findByName(rl, currentUser.getProvider());
        	if (role == null) {
        		throw new EntityDoesNotExistsException(Role.class, rl);
        	}
        	roles.add(role);
        }

        User user = new User();
        user.setUserName(postData.getUsername().toUpperCase());
        user.setEmail((postData.getEmail()));
        Name name = new Name();
        name.setLastName(postData.getLastName());
        name.setFirstName(postData.getFirstName());
        user.setName(name);
        user.setPassword(postData.getPassword());
        user.setLastPasswordModification(new Date());
        user.setProvider(provider);
        user.setRoles(roles);

        userService.create(user, currentUser);
    }

    public void update4_2(User4_2Dto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUsername())) {
            missingParameters.add("username");
        }
        if (StringUtils.isBlank(postData.getEmail())) {
            missingParameters.add("email");
        }
        if (StringUtils.isBlank(postData.getProvider())) {
            missingParameters.add("provider");
        }
        if (StringUtils.isBlank(postData.getRole())) {
            missingParameters.add("role");
        }

        handleMissingParameters();

        // find user
        User user = userService.findByUsername(postData.getUsername());

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, postData.getUsername(), "username");
        }

        // find provider
        Provider provider = providerService.findByCode(postData.getProvider());
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
        }

        // find role
        Role role = roleService.findByName(postData.getRole(), currentUser.getProvider());
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, postData.getRole());
        }

        user.setUserName(postData.getUsername());
        user.setEmail((postData.getEmail()));
        user.setNewPassword(postData.getPassword());
        Name name = new Name();
        name.setLastName(postData.getLastName());
        name.setFirstName(postData.getFirstName());
        user.setName(name);
        user.setProvider(provider);
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        user.setRoles(roles);

        userService.update(user, currentUser);
    }
    
    public void update(UserDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUsername())) {
            missingParameters.add("username");
        }
        if (StringUtils.isBlank(postData.getEmail())) {
            missingParameters.add("email");
        }
        if (StringUtils.isBlank(postData.getProvider())) {
            missingParameters.add("provider");
        }
        if (postData.getRoles() == null || postData.getRoles().isEmpty()) {
            missingParameters.add("roles");
        }

        handleMissingParameters();

        // find user
        User user = userService.findByUsername(postData.getUsername());

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, postData.getUsername(), "username");
        }

        // find provider
        Provider provider = providerService.findByCode(postData.getProvider());
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
        }

        // find role
        
        Set<Role> roles = new HashSet<Role>();
        for(String rl : postData.getRoles()) {
        	Role role = roleService.findByName(rl, currentUser.getProvider());
        	if (role == null) {
        		throw new EntityDoesNotExistsException(Role.class, rl);
        	}
        	roles.add(role);
        }

        user.setUserName(postData.getUsername());
        user.setEmail((postData.getEmail()));
        user.setNewPassword(postData.getPassword());
        Name name = new Name();
        name.setLastName(postData.getLastName());
        name.setFirstName(postData.getFirstName());
        user.setName(name);
        user.setProvider(provider);
        user.setRoles(roles);

        userService.update(user, currentUser);
    }

    public void remove(String username) throws MeveoApiException {
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, username, "username");
        }

        userService.remove(user);
    }

    public User4_2Dto find(String username) throws MeveoApiException {
        User user = userService.findByUsernameWithFetch(username, Arrays.asList("provider", "roles"));

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, username, "username");
        }

        User4_2Dto result = new User4_2Dto(user);

        return result;
    }

    public void createOrUpdate(User4_2Dto postData, User currentUser) throws MeveoApiException, BusinessException {
        User user = userService.findByUsername(postData.getUsername());
        if (user == null) {
            create4_2(postData, currentUser);
        } else {
            update4_2(postData, currentUser);
        }
    }
    
    public void createOrUpdate(UserDto postData, User currentUser) throws MeveoApiException, BusinessException {
        User user = userService.findByUsername(postData.getUsername());
        if (user == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}
