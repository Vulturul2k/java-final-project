
package app.controller;

import app.service.OfferService;
import app.service.dto.CreateOfferFromContextDto;
import app.service.dto.OfferDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/offers")
public class OfferController implements SecuredRestController {

    @Autowired
    private OfferService offerService;

    @GetMapping("/received/me")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<OfferDto> getOffersReceivedByAuthenticatedUser(Principal principal) {
        return offerService.getOffersReceivedByName(principal.getName());
    }


    @GetMapping("/received")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<OfferDto> getOffersReceived(@RequestParam String username) {
//        System.out.println("respondToOffer() called with id = " + username);
        return offerService.getOffersReceivedByName(username);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER')")
    public OfferDto respondToOffer(@PathVariable UUID id, @RequestParam String status,
                                   Principal principal) {
        return offerService.respondToOffer(id, status, principal.getName());
    }

    @PostMapping("/me")
    @PreAuthorize("hasAuthority('USER')")
    public OfferDto createOfferFromAuthenticatedUser(
            @RequestBody CreateOfferFromContextDto dto,
            Principal principal) {
        return offerService.createFromAuthenticatedUser(dto, principal.getName());
    }

}
