package com.sergio.memo_bot.state;

import com.sergio.memo_bot.dto.ProcessableMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserStateHolder {
    private final ConcurrentHashMap<Long, UserStateType> userStates = new ConcurrentHashMap<>();
    private final UserInputState userInputState;

    public void setUserState(Long userId, UserStateType userStateType) {
        userStates.put(userId, userStateType);
    }

    public UserStateType getUserState(Long userId) {
        return userStates.getOrDefault(userId, UserStateType.MAIN_MENU);
    }

    public void calculateNextState(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
//        log.info("Message id: {}; text: {}", processableMessage.getMessageId(), processableMessage.getText());
        if (containsCommand(processableMessage)) {
            UserStateType userStateByCommand = getUserStateByCommand(processableMessage.getText());
            setUserState(userId, userStateByCommand);
            processableMessage.setCurrentUserStateType(userStateByCommand);
        } else {
            Boolean awaitsUserInput = userInputState.getUserInputState(userId);
            if (awaitsUserInput) {
                UserStateType currentState = getUserState(userId);
                List<UserStateType> possibleNextStates = currentState.getPossibleNextStates();
                UserStateType nextUserState = possibleNextStates.getFirst();
                /*UserStateType nextUserState;
                if (possibleNextStates.size() == 1) {
                    nextUserState = possibleNextStates.getFirst();
                } else {
                    nextUserState = possibleNextStates.stream()
                            .filter(userStateType -> userStateType.name().equals(processableMessage.getText()) || Optional.ofNullable(userStateType.getCommandType()).map(CommandType::getCommandText).map(it -> it.equals(processableMessage.getText())).orElse(false))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Couldn't find appropriate next userState for %s".formatted(processableMessage.getText())));
                }*/
                setUserState(userId, nextUserState);
                processableMessage.setCurrentUserStateType(nextUserState);
                userInputState.clear(userId);
            } else {
                setUserState(userId, UserStateType.MAIN_MENU);
                processableMessage.setCurrentUserStateType(UserStateType.MAIN_MENU);
            }
        }

    }

    private boolean containsCommand(ProcessableMessage processableMessage) {
        return processableMessage.getText().startsWith("/") && CommandType.isCommandType(processableMessage.getText());
    }

    private UserStateType getUserStateByCommand(String text) {
        return EnumUtils.getEnumList(UserStateType.class)
                .stream()
                .filter(it -> it.filterByCommand(CommandType.getByCommandText(text)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find UserStateType by command type [%s]".formatted(text)));
    }

    public void clear(Long userId) {
        userStates.remove(userId);
    }
}
