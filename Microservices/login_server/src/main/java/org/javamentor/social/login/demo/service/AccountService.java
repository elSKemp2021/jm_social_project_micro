package org.javamentor.social.login.demo.service;

import org.javamentor.social.login.demo.model.Account;
import org.javamentor.social.login.demo.model.dto.AuthorizeDto;
import org.javamentor.social.login.demo.model.request.AuthRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


public interface AccountService{

    Account findByEmailAndPassword(String email, String password);

    void save(Account account);

    void register(AuthRequest authRequest);

    AuthorizeDto getAuthorizeDto(AuthRequest request);

    Account findById(Long userId);
}
