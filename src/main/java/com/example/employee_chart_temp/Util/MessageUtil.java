package com.example.employee_chart_temp.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageUtil implements MessageSourceAware {

    @Autowired
    MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String str){
        return this.messageSource.getMessage(str,null, Locale.US);
    }

}
