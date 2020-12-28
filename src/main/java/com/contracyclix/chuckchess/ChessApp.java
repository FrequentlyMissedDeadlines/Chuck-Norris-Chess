package com.contracyclix.chuckchess;

import com.contracyclix.chuckchess.ai.NoAIClassException;
import com.contracyclix.chuckchess.ui.NoUIClassException;
import com.contracyclix.chuckchess.ui.UI;
import com.contracyclix.chuckchess.ui.UIFactory;
import lombok.Getter;
import lombok.Setter;

public class ChessApp {
    @Getter @Setter
    private UIFactory uiFactory = new UIFactory();

    public void run() throws NoUIClassException, NoAIClassException {
        UI ui = uiFactory.getUI();
        ui.run();
    }
}
