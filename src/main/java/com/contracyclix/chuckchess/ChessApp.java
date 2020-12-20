package com.contracyclix.chuckchess;

import com.contracyclix.chuckchess.ui.UI;
import com.contracyclix.chuckchess.ui.UIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ChessApp implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ChessApp.class);

    @Autowired
    private UIFactory uiFactory;

    @Override
    public void run(String... args) throws Exception {
        UI ui = uiFactory.getUI();
        ui.run();
    }
}
