package com.voice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/voice2")
public class VoiceController {
    @RequestMapping(method = RequestMethod.GET)
    public String getVoice(ModelMap model) throws Exception {
        long mills1 = System.currentTimeMillis();
        System.out.println(new Date(mills1));
        String text = QuickstartSample.getTranscription(Constants.REPEATE_THE_QUESTION_MONO_filePath);
        long mills2 = System.currentTimeMillis();
        System.out.println(new Date(mills2));
        int requestDuration = (int) ((mills2 - mills1)/1000);
        String str = text + ", " + requestDuration + " sec";
        model.addAttribute("message", str);
        System.out.println("------> " + str);
        //DispatcherServlet
        return "hello";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String sendVoice(ModelMap model){
        model.addAttribute("message", "send!!!");
        //DispatcherServlet
        return "hello";
    }
}