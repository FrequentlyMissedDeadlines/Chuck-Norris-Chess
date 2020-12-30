package com.contracyclix.chuckchess;

import com.contracyclix.chuckchess.ai.NoAIClassException;
import com.contracyclix.chuckchess.benchmark.Benchmarks;
import com.contracyclix.chuckchess.config.Config;
import com.contracyclix.chuckchess.ui.NoUIClassException;
import org.openjdk.jmh.runner.RunnerException;

public class MainApp {
    private static final String configOption = "--config.location=";

    public static void main(String... args) throws NoUIClassException, NoAIClassException, RunnerException {
        if (args.length > 0 && "benchmark".equals(args[0])) {
            Benchmarks.main(args);
        } else {
            if (args.length > 0 && args[0].startsWith(configOption)) {
                Config.setConfigFileName(args[0].split(configOption)[1]);
            }
            new ChessApp().run();
        }
    }
}
