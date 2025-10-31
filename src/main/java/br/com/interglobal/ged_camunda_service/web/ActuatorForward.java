package br.com.interglobal.ged_camunda_service.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ActuatorForward {

    @RequestMapping({"/actuator", "/actuator/"})
    public String forward() {
        return "forward:/workflow/actuator";
    }
}
