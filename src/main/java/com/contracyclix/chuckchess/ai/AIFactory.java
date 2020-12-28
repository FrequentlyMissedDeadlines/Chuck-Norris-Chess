package com.contracyclix.chuckchess.ai;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AIFactory {

    public AI getAI(String className) throws NoAIClassException {
        switch (className) {
            case "RandomMove": return new AIRandomMove();
            case "MCTS": return new AIMcts();
            case "MinMax": return new AIMinMax();
            case "Human": return null;

            default: throw new NoAIClassException();
        }
    }
}
