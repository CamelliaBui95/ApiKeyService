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
    @Operation(summary = "ApiKey creation", description = "Create a new client to generate the ApiKey")
    @APIResponse(responseCode = "201", description = "Created !")
    @APIResponse(responseCode = "400", description = "La demande n'est pas correcte.")
    @APIResponse(responseCode = "409", description = "Conflit avec la ressource existante.")
    @APIResponse(responseCode = "500", description = "Le serveur a rencontré un problème !")
    public Response insert(ClientToCreateDTO client, @Context UriInfo uriInfo) {
        if (client.getNomClient().isEmpty() || client.getAdresseMail().isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).build();
        if (!clientRepository.isExistingNomClient(client.getNomClient())) {
            ClientEntity clientEntity = new ClientEntity(client.getNomClient(), client.getAdresseMail());
            String apiKey = clientEntity.generateKey(client.getNomClient());
            while (clientRepository.isExistingCle(apiKey)) {
                apiKey = clientEntity.generateKey(client.getNomClient());
            }
            clientEntity.setCle(apiKey);
            clientRepository.persist(clientEntity);
            System.out.println(clientEntity.getNomClient() + " : " + clientEntity.getCle() + " lengh="+ clientEntity.getCle().length());
            //TODO sendMail
            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            uriBuilder.path(clientEntity.getId().toString());
            return Response.created(uriBuilder.build()).entity(new ClientDTO(clientEntity)).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }

    }

    @Transactional
    @PUT
    @Operation(summary = "Update quota", description = "Assign new quota to a client")
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("{id}")
    public Response updateQuota(@PathParam("id") Integer id, Integer quota) {
        if (id == null || quota == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity savedClientEntity = clientRepository.findById(id);

        if (savedClientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        savedClientEntity.setQuotaMensuel(quota);
        return Response.ok(new ClientDTO(savedClientEntity)).build();
    }

    @Transactional
    @PUT
    @Operation(summary = "New ApiKey", description = "Generate a new ApiKey to a client")
    @Path("{id}/newKey")
    public Response updateKey(@PathParam("id") Integer id
//            , @Context UriInfo uriInfo
    ) {
        if (id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity savedClientEntity = clientRepository.findById(id);
        if (savedClientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        String newKey = savedClientEntity.generateKey(savedClientEntity.getNomClient());
        while (clientRepository.isExistingCle(newKey)) {
            newKey = savedClientEntity.generateKey(savedClientEntity.getNomClient());
        }
        savedClientEntity.setCle(newKey);
        //TODO sendMail
/*        UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
        uriBuilder.path(savedClientEntity.getId().toString());
        uriBuilder.path(newKey);*/
        return Response.ok(new ClientDTO(savedClientEntity)).build();
    }

}
