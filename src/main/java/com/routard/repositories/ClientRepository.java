package com.routard.repositories;

import com.routard.entities.ClientEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ClientRepository implements PanacheRepositoryBase<ClientEntity, Integer> {

    public ClientEntity findByNomClient(String nomClient) {
        return null;
    }
}
