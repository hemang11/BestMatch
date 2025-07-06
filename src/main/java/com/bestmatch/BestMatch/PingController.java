package com.bestmatch.BestMatch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date : 07/07/2025
 *
 * @author HemangShrimali
 */

@RestController
@RequestMapping(WebAppResources.ping)
public class PingController {

    @GetMapping
    public String pingMe() {
        return "Sab changa si....";
    }
}
