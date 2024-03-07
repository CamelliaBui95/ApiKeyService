package com.routard.resources;

import com.routard.dto.ClientDTO;
import com.routard.dto.ClientToCreateDTO;
import com.routard.entities.ClientEntity;
import com.routard.repositories.ClientRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
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
    @APIResponse(responseCode = "409", description = "Conflit avec la ressource existante.")
    @APIResponse(responseCode = "500", description = "Le serveur a rencontré un problème !")
    public Response insert(ClientToCreateDTO client, @Context UriInfo uriInfo) {
        if (client.getNomClient().isEmpty() || client.getAdresseMail().isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).build();
        if (!clientRepository.isValideNomClient(client.getNomClient())) {
            ClientEntity clientEntity = new ClientEntity(client.getNomClient(), client.getAdresseMail());
            String apiKey = clientEntity.generateKey(client.getNomClient());
            while (!clientRepository.isValideCle(apiKey)) {
                clientEntity.setCle(apiKey);
            }
            clientRepository.persist(clientEntity);
            System.out.println(clientEntity.getNomClient() + " : " + clientEntity.getCle());
            //TODO écrire envoyerMail
            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            uriBuilder.path(clientEntity.getId().toString());
            return Response.created(uriBuilder.build()).entity(new ClientDTO(clientEntity)).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }

    }

    @Transactional
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Integer id, ClientToCreateDTO client) {
        if (id == null || client == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if (!id.equals(client.getId()))
            return Response.status(Response.Status.CONFLICT).build();

        ClientEntity savedClientEntity = clientRepository.findById(id);
        if (savedClientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();
//TODO changer l'entité DTO en paramètre pour clé/quota, compléter update
        return null;

    }


}
