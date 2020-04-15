package com.example.configmanager;

import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;


@Component
class ConfigPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        String msg = String.format("config-manager> ");
        return new AttributedString(msg);
    }

}