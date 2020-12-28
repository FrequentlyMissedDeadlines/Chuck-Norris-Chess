package com.contracyclix.chuckchess;

import com.contracyclix.chuckchess.ai.NoAIClassException;
import com.contracyclix.chuckchess.ui.NoUIClassException;

public class MainApp {

    public static void main(String... args) throws NoUIClassException, NoAIClassException {
        new ChessApp().run();
    }
}
