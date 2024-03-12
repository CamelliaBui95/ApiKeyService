package com.routard.services;

import com.routard.dto.MailClient;
import io.smallrye.common.annotation.Blocking;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/mail")
@RegisterRestClient(configKey = "mail-service")
public interface MailService {
    @POST
    @Blocking
    @Transactional
    Response send(@HeaderParam("x-api-key") String apiKey, MailClient mailClient);

}
