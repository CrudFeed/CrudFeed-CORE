package org.crudfeed.core.service;

import org.crudfeed.core.dto.ModActionRoleResultResponseDto;
import org.crudfeed.core.dto.ModRoleInfoResponseDto;
import org.crudfeed.core.dto.ModShowResponseDto;
import org.crudfeed.core.entity.Post;
import org.crudfeed.core.entity.Role;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.repository.ModRepository;
import org.crudfeed.core.repository.PostRepository;
import org.crudfeed.core.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ModService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ModRepository modRepository;

    public ModService(UserRepository userRepository, PostRepository postRepository, ModRepository modRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.modRepository = modRepository;
    }

    public String modBanOrUnbanAccount(long modId, long targetId, String action) {
        if (modId <= 0 || targetId <= 0 || action == null) {
            return "Something went wrong!";
        }

        String actionLowerCase = action.toLowerCase();
        if (!actionLowerCase.equals("ban") && !actionLowerCase.equals("unban")) {
            return "Something went wrong!";
        }

        if (modId == targetId) {
            return "We truly understand your wish to " + actionLowerCase + " yourself, " +
                    "but our strict policies force us to decline. " +
                    "We are sorry!";
        }

        User mod = userRepository.findById(modId);
        User target = userRepository.findById(targetId);

        if (mod == null || target == null) {
            return "User not found!";
        }

        if (mod.getRole() == null || target.getRole() == null) {
            return "Something went wrong!";
        }

        if (!mod.getRole().hasAtLeast(Role.MODERATOR) || !mod.getRole().hasHigherThan(target.getRole())) {
            return "Access denied!";
        }

        boolean success = actionLowerCase.equals("ban")
                ? modRepository.modBanAccount(targetId)
                : modRepository.modUnbanAccount(targetId);

        if (!success) {
            return "Something went wrong!";
        }

        return actionLowerCase.equals("ban")
                ? "Successfully banned!"
                : "Successfully unbanned!";
    }

    public String modDeleteOrRestoreAccount(long modId, long targetId, String action) {
        if (modId <= 0 || targetId <= 0) {
            return "Something went wrong!";
        }

        String actionLowerCase = action.toLowerCase();
        if (!actionLowerCase.equals("delete") && !actionLowerCase.equals("restore")) {
            return "Something went wrong!";
        }

        if (modId == targetId) {
            return "You can not " + actionLowerCase + " yourself!";
        }

        User mod = userRepository.findById(modId);
        User target = userRepository.findById(targetId);

        if (mod == null || target == null) {
            return "User not found!";
        }

        if (mod.getRole() == null || target.getRole() == null) {
            return "Something went wrong!";
        }

        if (!mod.getRole().hasAtLeast(Role.MODERATOR) || !mod.getRole().hasHigherThan(target.getRole())) {
            return "Access denied!";
        }


        boolean success = actionLowerCase.equals("delete")
                ? modRepository.modDeleteOrRestore(targetId, "account-delete")
                : modRepository.modDeleteOrRestore(targetId, "account-restore");

        if (!success) {
            return "Something went wrong!";
        }

        return actionLowerCase.equals("delete")
                ? "Successfully delete!"
                : "Successfully restored!";
    }

    public ModActionRoleResultResponseDto modPromoteOrDemoteAccount(long modId, long targetId, String action) {
        if (modId <= 0 || targetId <= 0 || action == null) {
            return new ModActionRoleResultResponseDto(false, "Something went wrong!", null);
        }

        String actionLowerCase = action.toLowerCase();
        if (!actionLowerCase.equals("promote") && !actionLowerCase.equals("demote")) {
            return new ModActionRoleResultResponseDto(false, "Something went wrong!", null);
        }

        if (modId == targetId) {
            return new ModActionRoleResultResponseDto(
                    false,
                    "You can not " +
                            actionLowerCase +
                            " yourself!",
                    null
            );
        }

        User mod = userRepository.findById(modId);
        User target = userRepository.findById(targetId);

        if (mod == null || target == null) {
            return new ModActionRoleResultResponseDto(false, "User not found!", null);
        }

        if (mod.getRole() == null || target.getRole() == null) {
            return new ModActionRoleResultResponseDto(false, "Something went wrong!", null);
        }

        Role newRole = actionLowerCase.equals("promote")
                ? resolvePromotedRole(mod.getRole(), target.getRole())
                : resolveDemotedRole(mod.getRole(), target.getRole());

        if (newRole == null) {
            return new ModActionRoleResultResponseDto(false, "Access denied!", null);
        }

        ModRoleInfoResponseDto dto = modRepository.modChangeRoleForAccount(targetId, newRole);
        if (dto == null) {
            return new ModActionRoleResultResponseDto(
                    false,
                    "Failed to " +
                            actionLowerCase +
                            " user!",
                    null
            );
        }

        return new ModActionRoleResultResponseDto(true, "Successfully " + actionLowerCase + "ed!", dto);
    }

    public String modDeleteOrRestorePost(long modId, long targetId, String action) {
        if (modId <= 0 || targetId <= 0 || action == null) {
            return "Something went wrong!";
        }

        String actionLowerCase = action.toLowerCase();
        if (!actionLowerCase.equals("delete") && !actionLowerCase.equals("restore")) {
            return "Something went wrong!";
        }

        User mod = userRepository.findById(modId);
        Post target = postRepository.findById(targetId);

        if (mod == null || target == null) {
            return "User or post not found!";
        }

        if (mod.getRole() == null || !mod.getRole().hasAtLeast(Role.MODERATOR)) {
            return "Access denied!";
        }

        User author = userRepository.findById(target.getUserId());
        if (
                author != null
                && author.getRole() != null
                && mod.getId() != author.getId()
                && !mod.getRole().hasHigherThan(author.getRole())
        ) {
            return "Access denied!";
        }

        String repositoryAction;
        if (actionLowerCase.equals("delete")) {
            repositoryAction = "post-delete";
        } else {
            repositoryAction = "post-restore";
        }

        boolean success = modRepository.modDeleteOrRestore(targetId, repositoryAction);

        if (!success) {
            return "Something went wrong!";
        }

        return actionLowerCase.equals("delete")
                ? "Successfully deleted!"
                : "Successfully restored!";
    }

    public ModShowResponseDto modShow(long modId, long targetId) {
        if (modId <= 0 || targetId <= 0) {
            return null;
        }

        User mod = userRepository.findById(modId);
        User target = userRepository.findById(targetId);

        if (mod == null || target == null) {
            return null;
        }

        if (mod.getRole() == null || !mod.getRole().hasAtLeast(Role.MODERATOR)) {
            return null;
        }

        return modRepository.modShow(targetId);
    }

    private Role resolvePromotedRole(Role modRole, Role targetRole) {
        if (modRole == null || targetRole == null || !modRole.hasHigherThan(targetRole)) {
            return null;
        }

        if (modRole == Role.OWNER) {
            return targetRole == Role.USER ? Role.MODERATOR : Role.ADMIN;
        }

        if (modRole == Role.ADMIN && targetRole == Role.USER) {
            return Role.MODERATOR;
        }

        return null;
    }

    private Role resolveDemotedRole(Role modRole, Role targetRole) {
        if (modRole == null || targetRole == null || !modRole.hasHigherThan(targetRole)) {
            return null;
        }

        if (modRole == Role.OWNER) {
            if (targetRole == Role.ADMIN) {
                return Role.MODERATOR;
            }
            if (targetRole == Role.MODERATOR) {
                return Role.USER;
            }
        }

        if (modRole == Role.ADMIN && targetRole == Role.MODERATOR) {
            return Role.USER;
        }

        return null;
    }
}