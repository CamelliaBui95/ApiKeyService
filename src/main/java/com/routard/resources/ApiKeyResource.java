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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Path("/apiKey/")
@Tag(name = "ApiKey")
@Produces(MediaType.APPLICATION_JSON)
public class ApiKeyResource {

    @Context
    UriInfo uriInfo;
    @Inject
    ClientRepository clientRepository;
    @Inject
    MailHistoryRepository mailHistoryRepository;
    @GET
    @Operation(summary = "Find client by ApiKey", description = "Find the client with this ApiKey.")
    @Path("{apiKey}")
    public Response getClientByApiKey(@PathParam(value = "apiKey") String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty())
            return Response.ok("ApiKey required!", MediaType.TEXT_PLAIN).status(Response.Status.NOT_ACCEPTABLE).build();
        ClientEntity clientEntity = clientRepository.findByCle(apiKey);
        if (clientEntity == null)
            return Response.ok("Client account not found.", MediaType.TEXT_PLAIN).status(Response.Status.NOT_FOUND).build();
        return Response.ok(new ClientDTO(clientEntity)).build();
    }

    @GET
    @Operation(summary = "Count mails", description = "Return the quantity of sent mails with this ApiKey.")
    @Path("/{apiKey}/mailCount")
    public Response getMailCount(@PathParam(value = "apiKey") String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty())
            return Response.ok("ApiKey required!", MediaType.TEXT_PLAIN).status(Response.Status.NOT_ACCEPTABLE).build();
        ClientEntity clientEntity = clientRepository.findByCle(apiKey);
        if (clientEntity == null)
            return Response.ok("Client account not found.", MediaType.TEXT_PLAIN).status(Response.Status.NOT_FOUND).build();
        Integer mailCount = mailHistoryRepository.getMailCount(clientEntity);
        return Response.ok(String.format("You have already sent %d mails", mailCount)).build();
    }

    @Transactional
    @POST
    @Operation(summary = "Record mails", description = "Record an email in database.")
    @Path("{apiKey}/mails")
    public Response saveMail(@PathParam(value = "apiKey") String apiKey, MailClient mailClient) {
        if (apiKey == null || apiKey.trim().isEmpty())
            return Response.ok("ApiKey required!", MediaType.TEXT_PLAIN).status(Response.Status.NOT_ACCEPTABLE).build();
        if ( mailClient == null || mailClient.isValid())
            return Response.ok("Email address should be valid!", MediaType.TEXT_PLAIN).status(Response.Status.BAD_REQUEST).build();

        ClientEntity clientEntity = clientRepository.findByCle(apiKey);
        if (clientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        MailHistoryEntity mailHistoryEntity = new MailHistoryEntity();
        mailHistoryEntity.setObjetMail(mailClient.getSubject());
        mailHistoryEntity.setDestinataire(mailClient.getRecipient());
        mailHistoryEntity.setClientEntity(clientEntity);

        LocalDateTime date = LocalDateTime.now();
        mailHistoryEntity.setDateEnvoi(date);

        try{
        mailHistoryRepository.persist(mailHistoryEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok("Recording failed.", MediaType.TEXT_PLAIN).status(Response.Status.INTERNAL_SERVER_ERROR).build();
//            throw new RuntimeException(e);
        }

        UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
        uriBuilder.path(apiKey);
        return Response.created(uriBuilder.build()).entity(new MailHistoryDTO(mailHistoryEntity)).build();
    }
}
