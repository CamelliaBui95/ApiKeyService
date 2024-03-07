package com.routard.resources;

import com.routard.dto.ClientDTO;
import com.routard.entities.ClientEntity;
import com.routard.repositories.ClientRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/clients/")
@Tag(name = "Client")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {
    @Inject
    private ClientRepository clientRepository;

    @GET
    @Operation(summary = "All clients", description = "List of all clients")
    public Response getAll() {
        List<ClientEntity> clientEntityList = clientRepository.listAll();
        return Response.ok(ClientDTO.toDTOList(clientEntityList)).build();
    }

    @Transactional
    @POST
    @APIResponse(responseCode = "201", description = "Created !")
    @APIResponse(responseCode = "400", description = "La demande n'est pas correcte.")
    @APIResponse(responseCode = "500", description = "Le serveur a rencontré un problème !")
    public Response create(String nomClient, String email, Integer quota, @Context UriInfo uriInfo) {
        if ((!nomClient.isEmpty()) && (!email.isEmpty())) {
            ClientEntity clientEntity = new ClientEntity(nomClient, email, quota);
            clientRepository.persist(clientEntity);
            //TODO écrire envoyerMail
            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            uriBuilder.path(clientEntity.getId().toString());
            return Response.created(uriBuilder.build()).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }


}
