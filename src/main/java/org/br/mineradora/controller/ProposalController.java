package org.br.mineradora.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.br.mineradora.dto.ProposalDetailsDTO;
import org.br.mineradora.service.ProposalService;

@Path("/api/proposal")
@Slf4j
public class ProposalController {

    @Inject
    ProposalService proposalService;

    @GET
    @Path("/{id}")
    //@RolesAllowed({"user", "manager"})
    public ProposalDetailsDTO findDetailsProposal(@PathParam("id") long id){
        return proposalService.findFullProposal(id);
    }

    @POST
    public Response createNewProposal(ProposalDetailsDTO proposalDetailsDTO){
        log.info("-- Receiving new proposal --");
        try {
            proposalService.createNewProposal(proposalDetailsDTO);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response removeProposal(@PathParam("id") long id){
        try {
            proposalService.removeProposal(id);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return e.getResponse();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

}
