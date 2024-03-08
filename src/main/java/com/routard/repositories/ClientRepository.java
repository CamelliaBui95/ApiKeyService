package com.routard.repositories;

import com.routard.entities.ClientEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ClientRepository implements PanacheRepositoryBase<ClientEntity, Integer> {

    public ClientEntity findByNomClient(String nomClient) {
        return find("nomClient=?1", nomClient).firstResult();
    }
    public ClientEntity findByCle(String apiKey) {
        return find("cle=?1", apiKey).firstResult();
    }

    public boolean isExistingNomClient(String nomClient) {
        ClientEntity clientEntity = findByNomClient(nomClient);
        return clientEntity != null;
    }
    public boolean isExistingCle(String apiKey) {
        ClientEntity clientEntity = findByCle(apiKey);
        return clientEntity != null;
    }

}
