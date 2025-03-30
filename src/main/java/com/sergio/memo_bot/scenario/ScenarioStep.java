package com.sergio.memo_bot.scenario;

import com.sergio.memo_bot.state.CommandType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
public class ScenarioStep {
    private final String title;
    private final List<CommandType> commands;
}
