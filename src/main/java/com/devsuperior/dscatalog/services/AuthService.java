package com.devsuperior.dscatalog.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.EmailDTO;
import com.devsuperior.dscatalog.entities.PasswordRecover;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.PasswordRecoverRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.validation.Valid;

@Service
public class AuthService {
	
	@Value("${email.password-recover.token.minutes}")
	private Long tokenMinutes;
	
	@Value("${email.password-recover.uri}")
	private String recoverUri;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PasswordRecoverRepository passwordRecoverRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	public void createRecoverToken(@Valid EmailDTO body) {
		
		User user=userRepository.findByEmail(body.getEmail());
		
		if(user == null) {
			throw new ResourceNotFoundException("Email não encontrado");
		}
		
		PasswordRecover entity=new PasswordRecover();
		
		String token=UUID.randomUUID().toString();
		
		entity.setEmail(body.getEmail());
		entity.setToken(token);
		entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
		entity=passwordRecoverRepository.save(entity);
		
		String text="Acesse o link para definir uma nova senha\n\n"
				+ recoverUri + token;
		
		emailService.sendEmail(body.getEmail(), "Recuperação de senha", text);
		
		
		
	}

}
