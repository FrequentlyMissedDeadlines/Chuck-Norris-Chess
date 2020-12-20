package com.contracyclix.chuckchess.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AIFactory {
    @Autowired
    private AIRandomMove aiRandomMove;

    @Autowired
    private AIMinMax aiMinMax;

    @Autowired
    private AIMcts aiMcts;

    public AI getAI(String className) throws NoAIClassException {
        switch (className) {
            case "RandomMove": return aiRandomMove;
            case "MCTS": return aiMcts;
            case "MinMax": return aiMinMax;
            case "Human": return null;

            default: throw new NoAIClassException();
        }
    }
}
