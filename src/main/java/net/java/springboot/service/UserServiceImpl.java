package net.java.springboot.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.java.springboot.model.Cont;
import net.java.springboot.model.Role;
import net.java.springboot.model.Transactions;
import net.java.springboot.model.User;
import net.java.springboot.repository.ContRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.java.springboot.repository.UserRepository;
import net.java.springboot.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService{

	private UserRepository userRepository;
	private ContRepository contRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository , ContRepository contRepository) {
		super();
		this.userRepository = userRepository;
		this.contRepository = contRepository;
	}

	@Override
	public User save(UserRegistrationDto registrationDto) {
		User user = new User(registrationDto.getFirstName(), 
				registrationDto.getLastName(), registrationDto.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()), Arrays.asList(new Role("ROLE_USER")));


		if(getUser(registrationDto.getEmail()).equals(null))
			return userRepository.save(user);
		else
			return null;


	}

	@Override
	public List<Transactions> getAllTransactions() {

		//return  userRepository.findByEmail(email).getCont().getTransactionsList();

		List<User> users = userRepository.findAll();

		List<Transactions> transactions = new ArrayList<>();

		for(User u:users)
		{
			Cont c = u.getCont();
			transactions.addAll(c.getTransactionsList());
		}

		return transactions;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUser(String email) {

		return userRepository.findByEmail(email);

	}

	@Override
	public List<Transactions> getTransactions(String email) {

		User user = userRepository.findByEmail(email);

		//System.out.println(user);

		Cont cont = user.getCont();

		//System.out.println(cont.getTransactionsList());

		List<Transactions> transactionsList = getAllTransactions();

		List<Transactions> printedTransactions = new ArrayList<>();

		for(Transactions t: transactionsList)
		{
			if(t.getIdDestinatie().equals(email) || t.getIdSursa().equals(email))
			{
				printedTransactions.add(t);
			}
		}

		return printedTransactions;

	}

	@Override
	public double getAmount(String email) {

		User user = userRepository.findByEmail(email);

		System.out.println(user);

		Cont cont = user.getCont();

		System.out.println("Found:"+cont.getSold()+" money ");

		return cont.getSold();
	}

	@Override
	public User update(User user) {
		return userRepository.save(user);
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		User user = userRepository.findByEmail(username);

		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));		
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
	
}
