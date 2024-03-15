package com.routard.resources;

import com.routard.dto.ClientDTO;
import com.routard.dto.ClientToCreateDTO;
import com.routard.dto.MailClient;
import com.routard.entities.ClientEntity;
import com.routard.repositories.ClientRepository;
import com.routard.services.MailService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/clients/")
@Tag(name = "Client")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {
    private static final String APIKEYSERVICE_KEY = "Q3pzZDlNQVk1MHFD";
    public static final String EMAILSERVICE_ERROR = "Email sending error.";
    public static final String ACCOUNT_NOT_EXISTED = "This client does not exist.";

    @Inject
    @RestClient
    MailService mailService;

    @Inject
    private ClientRepository clientRepository;


    @GET
    @Operation(summary = "All clients", description = "List of all clients.")
    public Response getAll() {
        List<ClientEntity> clientEntityList = clientRepository.listAll();
        return Response.ok(ClientDTO.toDTOList(clientEntityList)).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Find client by id", description = "Find a client with this id.")
    public Response getById(@PathParam(value = "id") Integer id){
        ClientEntity clientEntity = clientRepository.findById(id);
        if (clientEntity == null)
            return Response.ok(ACCOUNT_NOT_EXISTED, MediaType.TEXT_PLAIN).status(Response.Status.NOT_FOUND).build();
        return Response.ok(new ClientDTO(clientEntity)).build();
    }

    @Transactional
    @POST
    @Operation(summary = "ApiKey creation", description = "Create a new client to generate the ApiKey.")
    @APIResponse(responseCode = "201", description = "Client account created.")
    @APIResponse(responseCode = "400", description = "Bad request.")
    @APIResponse(responseCode = "409", description = "Conflict with existing resource.")
    @APIResponse(responseCode = "500", description = "Server error.")
    public Response insert(ClientToCreateDTO client, @Context UriInfo uriInfo) {
        if (client.getNomClient().isEmpty() || client.getAdresseMail().isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).build();
        if (!client.isValidClientToCreate())
            return Response.ok("You must enter a valid email address. The login must not be identical to the email address.").status(Response.Status.BAD_REQUEST).build();
        if (!clientRepository.isExistingNomClient(client.getNomClient())) {
            ClientEntity clientEntity = new ClientEntity(client.getNomClient(), client.getAdresseMail());
            String apiKey = clientEntity.generateKey(client.getNomClient());
            while (clientRepository.isExistingCle(apiKey)) {
                apiKey = clientEntity.generateKey(client.getNomClient());
            }
            clientEntity.setCle(apiKey);

            MailClient mailClient= MailClient.builder()
                    .recipient(clientEntity.getEmail())
                    .subject("Votre compte client a été créé")
                    .content("Bonjour "+ clientEntity.getNomClient() + " !\nVotre ApiKey personnelle et confidencielle : " + clientEntity.getCle())
                    .build();

            try{
                mailService.send(APIKEYSERVICE_KEY, mailClient);
            }catch (Exception e) {
                e.printStackTrace();
                return Response.ok(EMAILSERVICE_ERROR, MediaType.TEXT_PLAIN).status(Response.Status.SERVICE_UNAVAILABLE).build();
            }

            try{
                clientRepository.persist(clientEntity);
            }catch (Exception e) {
                e.printStackTrace();
                return Response.ok("Creation failed!", MediaType.TEXT_PLAIN).status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            System.out.println(clientEntity.getNomClient() + " : " + clientEntity.getCle() + " lengh="+ clientEntity.getCle().length());

            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            uriBuilder.path(clientEntity.getId().toString());
            return Response.ok("A new client account has been created.", MediaType.TEXT_PLAIN).status(Response.Status.CREATED).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }

    }

    @Transactional
    @PUT
    @Operation(summary = "Update quota", description = "Assign new quota to a client")
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("{id}/newQuota")
    public Response updateQuota(@PathParam("id") Integer id, Integer quota) {
        if (id == null || quota == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity savedClientEntity = clientRepository.findById(id);

        if (savedClientEntity == null)
            return Response.ok(ACCOUNT_NOT_EXISTED, MediaType.TEXT_PLAIN).status(Response.Status.NOT_FOUND).build();

        if (!savedClientEntity.getStatut().equalsIgnoreCase("active"))
            return Response.ok("This account is not active.", MediaType.TEXT_PLAIN).status(Response.Status.UNAUTHORIZED).build();

        savedClientEntity.setQuotaMensuel(quota);

        MailClient mailClient= MailClient.builder()
                .recipient(savedClientEntity.getEmail())
                .subject("Votre quota mensuel a été mis à jour")
                .content("Bonjour "+ savedClientEntity.getNomClient() + " !\nVotre nouveau quota mensuel : " + savedClientEntity.getQuotaMensuel())
                .build();

        try{
            mailService.send(APIKEYSERVICE_KEY, mailClient);
        }catch (Exception e) {
            e.printStackTrace();
            return Response.ok(EMAILSERVICE_ERROR, MediaType.TEXT_PLAIN).status(Response.Status.SERVICE_UNAVAILABLE).build();
        }

        return Response.ok("New quota : "+savedClientEntity.getQuotaMensuel(), MediaType.TEXT_PLAIN).status(Response.Status.OK).build();
//        return Response.ok(new ClientDTO(savedClientEntity)).build();
    }

    @Transactional
    @PUT
    @Operation(summary = "New ApiKey", description = "Generate a new ApiKey to a client")
    @Path("{id}/newKey")
    public Response updateKey(@PathParam("id") Integer id) {
        if (id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity savedClientEntity = clientRepository.findById(id);

        if (savedClientEntity == null)
            return Response.ok(ACCOUNT_NOT_EXISTED, MediaType.TEXT_PLAIN).status(Response.Status.NOT_FOUND).build();

        if (!savedClientEntity.getStatut().equalsIgnoreCase("active"))
            return Response.ok("This account is not active.", MediaType.TEXT_PLAIN).status(Response.Status.UNAUTHORIZED).build();

        String newKey = savedClientEntity.generateKey(savedClientEntity.getNomClient());
        while (clientRepository.isExistingCle(newKey)) {
            newKey = savedClientEntity.generateKey(savedClientEntity.getNomClient());
        }
        savedClientEntity.setCle(newKey);

        MailClient mailClient= MailClient.builder()
                .recipient(savedClientEntity.getEmail())
                .subject("Votre nouvelle ApiKey")
                .content("Bonjour "+ savedClientEntity.getNomClient() + " !\nVotre nouvelle ApiKey personnelle et confidentielle : " + savedClientEntity.getCle())
                .build();

        try{
            mailService.send(APIKEYSERVICE_KEY, mailClient);
        }catch (Exception e) {
            e.printStackTrace();
            return Response.ok(EMAILSERVICE_ERROR, MediaType.TEXT_PLAIN).status(Response.Status.SERVICE_UNAVAILABLE).build();
        }

        return Response.ok("A new ApiKey has been sent.", MediaType.TEXT_PLAIN).status(Response.Status.OK).build();
    }

    @Transactional
    @PUT
    @Operation(summary = "Change status", description = "Activate or desactivate a client account")
    @Path("{id}/statusChange")
    public Response changeStatus(@PathParam("id") Integer id) {
        if (id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity savedClientEntity = clientRepository.findById(id);

        if (savedClientEntity == null)
            return Response.ok(ACCOUNT_NOT_EXISTED, MediaType.TEXT_PLAIN).status(Response.Status.NOT_FOUND).build();

        if (savedClientEntity.getStatut().equalsIgnoreCase("active"))
            savedClientEntity.setStatut("desactive");
        else
            savedClientEntity.setStatut("active");

        return Response.ok(String.format("This account's status: %s", savedClientEntity.getStatut())).build();

    }

}
