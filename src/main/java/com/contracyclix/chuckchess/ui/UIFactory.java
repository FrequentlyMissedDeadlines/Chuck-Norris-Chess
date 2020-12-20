package com.contracyclix.chuckchess.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component @NoArgsConstructor @AllArgsConstructor @Getter
public class UIFactory {

    @Value("${UI.Class}")
    private String className;

    @Autowired
    private UIConsole uiConsole;

    public UI getUI() throws NoUIClassException {
        switch (className) {
            case "UIConsole": return uiConsole;

            default: throw new NoUIClassException();
        }
    }

}
