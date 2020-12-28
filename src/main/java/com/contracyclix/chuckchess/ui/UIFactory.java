package com.contracyclix.chuckchess.ui;

import com.contracyclix.chuckchess.ai.NoAIClassException;
import com.contracyclix.chuckchess.config.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
public class UIFactory {

    @Getter @Setter
    private String className = Config.get().getString("UI.Class");

    public UI getUI() throws NoUIClassException, NoAIClassException {
        switch (className) {
            case "UIConsole": return new UIConsole();
            case "UciGui": return new UciGui();

            default: throw new NoUIClassException();
        }
    }

}
