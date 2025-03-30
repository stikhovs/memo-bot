package com.sergio.memo_bot.scenario;

import com.sergio.memo_bot.state.CommandType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ScenarioConfig {

    @Bean
    public Map<ScenarioStep, List<ScenarioStep>> createSetScenario() {
        Map<ScenarioStep, List<ScenarioStep>> scenarioStepsConnection = new ConcurrentHashMap<>();

//        ScenarioStep createSet = ScenarioStep.builder().title("Create set").commands(List.of(CommandType.CREATE_SET)).build();
        ScenarioStep nameSet = ScenarioStep.builder().title("Name set").commands(List.of(CommandType.NAME_SET)).build();
        ScenarioStep acceptSetName = ScenarioStep.builder().title("Accept set name").commands(List.of(CommandType.ACCEPT_SET_NAME)).build();
        ScenarioStep declineSetName = ScenarioStep.builder().title("Decline set name").commands(List.of(CommandType.DECLINE_SET_NAME)).build();
        ScenarioStep addCard = ScenarioStep.builder().title("Add card").commands(List.of(CommandType.ADD_CARD_REQUEST)).build();
        ScenarioStep acceptFrontSide = ScenarioStep.builder().title("Accept front side").commands(List.of(CommandType.ACCEPT_FRONT_SIDE)).build();
        ScenarioStep declineFrontSide = ScenarioStep.builder().title("Decline front side").commands(List.of(CommandType.DECLINE_FRONT_SIDE)).build();
        ScenarioStep acceptBackSide = ScenarioStep.builder().title("Accept back side").commands(List.of(CommandType.ACCEPT_BACK_SIDE)).build();
        ScenarioStep declineBackSide = ScenarioStep.builder().title("Decline back side").commands(List.of(CommandType.DECLINE_BACK_SIDE)).build();
        ScenarioStep saveCardSet = ScenarioStep.builder().title("Save card set").commands(List.of(CommandType.SAVE_CARD_SET_REQUEST)).build();

        // create linking structure
//        scenarioStepsConnection.put(createSet, List.of(nameSet));
        scenarioStepsConnection.put(nameSet, List.of(acceptSetName, declineSetName));
        scenarioStepsConnection.put(declineSetName, List.of(nameSet));
        scenarioStepsConnection.put(acceptSetName, List.of(addCard));
        scenarioStepsConnection.put(addCard, List.of(acceptFrontSide, declineFrontSide));
        scenarioStepsConnection.put(declineFrontSide, List.of(addCard));
        scenarioStepsConnection.put(acceptFrontSide, List.of(acceptBackSide, declineBackSide)); // TODO: кривой переход
        scenarioStepsConnection.put(declineBackSide, List.of(acceptFrontSide));
        scenarioStepsConnection.put(acceptBackSide, List.of(saveCardSet));

        return scenarioStepsConnection;
    }

    /*@Bean
    public Graph<List<CommandType>, DefaultEdge> createSetScenario() {
        Graph<List<CommandType>, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        graph.addVertex(List.of(CommandType.CREATE_SET));
        graph.addVertex(List.of(CommandType.NAME_SET));
        graph.addVertex(List.of(CommandType.ACCEPT_SET_NAME));
        graph.addVertex(List.of(CommandType.DECLINE_SET_NAME));
        graph.addVertex(List.of(CommandType.ADD_CARD));
        graph.addVertex(List.of(CommandType.ACCEPT_FRONT_SIDE));
        graph.addVertex(List.of(CommandType.DECLINE_FRONT_SIDE));
        graph.addVertex(List.of(CommandType.ACCEPT_BACK_SIDE));
        graph.addVertex(List.of(CommandType.DECLINE_BACK_SIDE));
        graph.addVertex(List.of(CommandType.SAVE_CARD_SET));

        graph.addEdge(List.of(CommandType.CREATE_SET), List.of(CommandType.NAME_SET));
        graph.addEdge(List.of(CommandType.NAME_SET), List.of(CommandType.ACCEPT_SET_NAME));
        graph.addEdge(List.of(CommandType.NAME_SET), List.of(CommandType.DECLINE_SET_NAME));
        graph.addEdge(List.of(CommandType.DECLINE_SET_NAME), List.of(CommandType.NAME_SET));
        graph.addEdge(List.of(CommandType.ACCEPT_SET_NAME), List.of(CommandType.ADD_CARD));
        graph.addEdge(List.of(CommandType.ADD_CARD), List.of(CommandType.ACCEPT_FRONT_SIDE));
        graph.addEdge(List.of(CommandType.ADD_CARD), List.of(CommandType.DECLINE_FRONT_SIDE));
        graph.addEdge(List.of(CommandType.DECLINE_FRONT_SIDE), List.of(CommandType.ADD_CARD));
        graph.addEdge(List.of(CommandType.ACCEPT_FRONT_SIDE), List.of(CommandType.ACCEPT_BACK_SIDE));
        graph.addEdge(List.of(CommandType.ACCEPT_FRONT_SIDE), List.of(CommandType.DECLINE_BACK_SIDE));
        graph.addEdge(List.of(CommandType.DECLINE_BACK_SIDE), List.of(CommandType.ACCEPT_FRONT_SIDE));
        graph.addEdge(List.of(CommandType.ACCEPT_BACK_SIDE), List.of(CommandType.SAVE_CARD_SET));

        System.out.println(graph);

        List<CommandType> start = graph.vertexSet()
                .stream()
                .filter(commandTypes -> commandTypes.contains(CommandType.ADD_CARD))
                .findFirst()
                .get();

        DepthFirstIterator<List<CommandType>, DefaultEdge> iterator = new DepthFirstIterator<>(graph, start);
        if (iterator.hasNext()) {
            List<CommandType> next = iterator.next();
            System.out.println("Next:");
            System.out.println(next);
        }

        return graph;
    }*/


    /*@Bean
    public ScenarioStep createSet() {
        return ScenarioStep.builder()
                .commands(List.of(CommandType.CREATE_SET))
                .nextSteps(List.of(
                                nextStep(NAME_SET,
                                        nextStep(CommandType.ACCEPT_SET_NAME,
                                                nextStep(CommandType.ADD_CARD,
                                                        nextStep(CommandType)
                                                )
                                        ),
                                        nextStep(CommandType.DECLINE_SET_NAME, nextStep(CommandType.CREATE_SET))
                                )
                        )
                )
                .build();
    }

    private ScenarioStep nextStep(List<CommandType> commandTypes, ScenarioStep... nextSteps) {
        return ScenarioStep.builder()
                .commands(commandTypes)
                .nextSteps(Optional.ofNullable(nextSteps).map(it -> Arrays.stream(it).toList()).orElse(List.of()))
                .build();
    }

    private ScenarioStep nextStep(CommandType commandType, ScenarioStep... nextSteps) {
        return ScenarioStep.builder()
                .commands(List.of(commandType))
                .nextSteps(Optional.ofNullable(nextSteps).map(it -> Arrays.stream(it).toList()).orElse(List.of()))
                .build();
    }*/

}
