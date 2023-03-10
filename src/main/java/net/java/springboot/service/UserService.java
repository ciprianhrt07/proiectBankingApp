package net.java.springboot.service;

import net.java.springboot.model.Transactions;
import net.java.springboot.model.User;
import net.java.springboot.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService{
	User save(UserRegistrationDto registrationDto);
	List<Transactions> getAllTransactions();
	List<User> getAllUsers();
	User getUser(String email);
	List<Transactions> getTransactions(String email);
	double getAmount(String email);
	User update(User user);

	void deleteUser(Long id);

}
