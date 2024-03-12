package com.routard.resources;

import com.routard.dto.ClientDTO;
import com.routard.dto.MailClient;
import com.routard.dto.MailHistoryDTO;
import com.routard.entities.ClientEntity;
import com.routard.entities.MailHistoryEntity;
import com.routard.repositories.ClientRepository;
import com.routard.repositories.MailHistoryRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/apiKey/")
@Tag(name = "ApiKey")
@Produces(MediaType.APPLICATION_JSON)
public class ApiKeyResource {

    @Context
    UriInfo uriInfo;
    @Inject
    private ClientRepository clientRepository;
    @Inject
    private MailHistoryRepository mailHistoryRepository;
    @GET
    @Path("{apiKey}")
    public Response getClientByApiKey(@PathParam("apiKey") String apiKey) {
        if (apiKey == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        ClientEntity clientEntity = clientRepository.findByCle(apiKey);
        if (clientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(new ClientDTO(clientEntity)).build();
    }

    @GET
    @Path("/mailCount/{apiKey}")
    public Response getMailCount(@PathParam("apiKey") String apiKey) {
        if (apiKey == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        ClientEntity clientEntity = clientRepository.findByCle(apiKey);
        if (clientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        Long mailCount = MailHistoryRepository.getMailCount(clientEntity.getId());
        return Response.ok(mailCount).build();
    }

    @POST
    @Transactional
    @Path("{apiKey}")
    public Response saveMail(@PathParam("apiKey") String apiKey, MailClient mailClient) {
        if (apiKey == null || mailClient == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        ClientEntity clientEntity = clientRepository.findByCle(apiKey);
        if (clientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        MailHistoryEntity mailHistoryEntity = new MailHistoryEntity();
        mailHistoryEntity.setObjetMail(mailClient.getSubject());
        mailHistoryEntity.setDestinataire(mailClient.getRecipient());
        mailHistoryEntity.setClientEntity(clientEntity);

        mailHistoryRepository.persist(mailHistoryEntity);

        UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
        uriBuilder.path(apiKey);
        return Response.created(uriBuilder.build()).entity(new MailHistoryDTO(mailHistoryEntity)).build();
    }
}
