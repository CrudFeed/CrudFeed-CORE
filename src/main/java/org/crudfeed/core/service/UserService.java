package org.crudfeed.core.service;

import org.crudfeed.core.dto.account.GetBannedRequestDto;
import org.crudfeed.core.dto.account.ShowFriendsListRequestDto;
import org.crudfeed.core.dto.account.ViewAccountPageRequestDto;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GetBannedRequestDto getBanned(long userId) {
        return userRepository.getBanned(userId);
    }

    public String getNicknameById(long userId) { return userRepository.getNicknameById(userId); }

    public String subscribe(long subscriberId, String name) {
        return userRepository.subscribe(subscriberId, name);
    }

    public String unsubscribe(long subscriberId, String name) {
        return userRepository.unsubscribe(subscriberId, name);
    }

    public String addOrRemoveFriend(long userId, String friendName, String action) {
        if (userId <= 0 || friendName == null || friendName.isBlank()) {
            return "Error: Invalid user id or friend name.";
        }

        if (action == null) {
            return "Error: Invalid action.";
        }

        String actionLowerCase = action.toLowerCase();
        if (!actionLowerCase.equals("add") && !actionLowerCase.equals("remove")) {
            return "Error: Invalid action.";
        }

        User friend = userRepository.findByNickname(friendName);
        if (friend == null) {
            return "User not found!";
        }

        boolean isAdd = actionLowerCase.equals("add");

        if (userId == friend.getId()) {
            return isAdd
                    ? "You can not add yourself to friends!"
                    : "You can not remove yourself from friends!";
        }

        boolean success = isAdd
                ? userRepository.addFriend(userId, friend.getId())
                : userRepository.removeFriend(userId, friend.getId());

        if (!success) {
            return "Something went wrong!";
        }

        return isAdd
                ? "Successfully added!"
                : "Successfully removed!";
    }

    public List<ShowFriendsListRequestDto> showFriendsList(long userId) {
        if (userId <= 0) {
            return List.of();
        }

        return userRepository.showFriendsList(userId);
    }

    public ViewAccountPageRequestDto viewAccountPage(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return null;
        }

        return userRepository.viewAccountPage(nickname);
    }

    public String editAccountPage(long userId, String content) {
        if (userId <= 0) {
            return "Something went wrong!";
        }
        boolean isUpdated = userRepository.editAccountPage(userId, content);
        return isUpdated ? "Profile bio successfully updated!" : "Failed to update bio!";
    }

    public String changeName(String newName, long userId) {
        if (newName == null || newName.isBlank()) {
            return "Name cannot be empty!";
        }
        boolean isChanged = userRepository.newName(newName, userId);
        return isChanged ? "Name successfully updated!" : "Failed to update name.";
    }

    public String changePass(String newPass, long userId) {
        if (newPass == null || newPass.isBlank()) {
            return "Password cannot be empty!";
        }
        boolean isChanged = userRepository.newPass(newPass, userId);
        return isChanged ? "Password successfully updated!" : "Failed to update password.";
    }

    public String deleteAccount(String delete, long userId, String email, String nickname) {
        if (delete == null) {
            return "Action canceled!";
        }
        if (delete.equalsIgnoreCase("y") && userRepository.deleteUser(userId, email, nickname)) {
            return "Account successfully deleted!";
        } else if (delete.equalsIgnoreCase("n")) {
            return "Canceled!";
        }
        return "Invalid input! Action canceled.";
    }

    public long getUserId(String nickname) { return userRepository.getUserId(nickname); }

    public long getSubscribersCount(long userId) { return userRepository.getSubscribersCount(userId); }

    public long getSubscriptionsCount(long userId) { return userRepository.getSubscriptionsCount(userId); }
}