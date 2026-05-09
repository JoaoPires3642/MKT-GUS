package com.mktgus.autoatendimento.app.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

public class CpfMaskingConverter extends MessageConverter {

    // Matches CPF with or without formatting: 529.982.247-25 or 52998224725
    private static final Pattern CPF_FORMATTED = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    private static final Pattern CPF_DIGITS    = Pattern.compile("(?<![\\d])\\d{11}(?![\\d])");

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (message == null) return "";
        message = CPF_FORMATTED.matcher(message).replaceAll("***.***.***-**");
        message = CPF_DIGITS.matcher(message).replaceAll("***********");
        return message;
    }
}