package com.retail.ecom.serviceIMPL;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.retail.ecom.Exception.UserIsNotLoginException;
import com.retail.ecom.cache.CacheStore;
import com.retail.ecom.entity.AccessToken;
import com.retail.ecom.entity.Customer;
import com.retail.ecom.entity.RefreshToken;
import com.retail.ecom.entity.Seller;
import com.retail.ecom.entity.User;
import com.retail.ecom.enums.UserRole;
import com.retail.ecom.jwt.JWTService;
import com.retail.ecom.mail_service.MailService;
import com.retail.ecom.repository.AccessRepository;
import com.retail.ecom.repository.RefreshTokenRepository;
import com.retail.ecom.repository.UserRespository;
import com.retail.ecom.requestDTO.AuthRequest;
import com.retail.ecom.requestDTO.OtpRequest;
import com.retail.ecom.requestDTO.UserRequest;
import com.retail.ecom.responseDTO.AuthResponse;
import com.retail.ecom.responseDTO.UserResponse;
import com.retail.ecom.service.AuthorService;
import com.retail.ecom.utility.MessageModel;
import com.retail.ecom.utility.ResponseStructure;
import com.retail.ecom.utility.SimpleResponseStructure;

import jakarta.mail.MessagingException;

@Service
public class AuthorServiceImpl implements AuthorService{


	private UserRespository userRespository;
	private ResponseStructure<UserResponse> responseStructure;

	private CacheStore<String> otpCache;
	private CacheStore<User> userCache;

	private SimpleResponseStructure simpleResponseStructure;

	private MailService mailService;

	private AuthenticationManager authenticationManager;
	private PasswordEncoder passwordEncoder;
	private JWTService jwtService;

	private AccessRepository accessRepository;

	private RefreshTokenRepository refreshTokenRepository;

	@Value("${myapp.jwt.refresh.expiration}")
	private long refreshExpiration;

	@Value("${myapp.jwt.access.expiration}")
	private long accessExpiration;




	public AuthorServiceImpl(UserRespository userRespository, ResponseStructure<UserResponse> responseStructure,
			CacheStore<String> otpCache, CacheStore<User> userCache, SimpleResponseStructure simpleResponseStructure,
			MailService mailService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
			JWTService jwtService, AccessRepository accessRepository, RefreshTokenRepository refreshTokenRepository) {
		super();
		this.userRespository = userRespository;
		this.responseStructure = responseStructure;
		this.otpCache = otpCache;
		this.userCache = userCache;
		this.simpleResponseStructure = simpleResponseStructure;
		this.mailService = mailService;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.accessRepository = accessRepository;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> registerUser( UserRequest userRequest) {
		if(userRespository.existsByEmail(userRequest.getEmail()))throw new RuntimeException(   );
		User user=mapToChildEntity(userRequest);
		String otp =generateOTP();


		otpCache.add(user.getEmail(), otp);
		userCache.add("user", user);

		//System.err.println(otp);
		//send mail with OTp
		try {
			sendOTp(user,otp);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//return user data
		return ResponseEntity.ok(simpleResponseStructure.setStatus(HttpStatus.ACCEPTED.value())
				.setMessage("Verify OTP sent thrrough mail to complete registration! "+ " expires in 1 minute"));
	}

	private void sendOTp(User user, String otp) throws MessagingException {

		MessageModel model=MessageModel.builder().to(user.getEmail()).subject("verify your OTP")
				.text(
						"<P>hi,<br>"
								+"Thanks for your intrest in E-com"
								+"Please verify your mail Id using the OTP given below,</p>"
								+"<br>"
								+"<h1>"+otp+"</h1>"
								+"</br>"
								+"<p>Please ignore it its not you</p>"
								+"<br>"
								+"with best regards"
								+"<h3>E-Com</h3>"
						).build();

		mailService.sendMailMessage(model);

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpRequest otpRequest){
		if(otpCache.get(otpRequest.getEmail())==null)throw new RuntimeException();//otp expired
		if(!otpCache.get(otpRequest.getEmail()).equals(otpRequest.getOtp())) throw new RuntimeException();//invalid Otp

		User user=userCache.get("user");
		if(user==null) throw new RuntimeException();//registration session expired
		user.setEmailVerified(true);
		user=userRespository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(responseStructure.setBody(mapToUserResponse(user))
						.setStatus(HttpStatus.CREATED.value())
						.setMessage("OTP verification successfull"));

	}

	private UserResponse mapToUserResponse(User user) {
		// TODO Auto-generated method stub
		return new UserResponse
				(user.getUserId(),user.getUserName(),user.getDisplayName(),user.getEmail(),user.getUserRole());
	}

	private String generateOTP() {
		// TODO Auto-generated method stub
		return String.valueOf(new Random().nextInt(100000, 999999));
	}


	@SuppressWarnings("unchecked")
	private <T extends User>T mapToChildEntity(UserRequest userRequest) {
		UserRole role =userRequest.getUserRole();

		User user=null;
		switch(role) {
		case SELLER -> user=new Seller();
		case CUSTOMER->user=new Customer();

		default->throw new RuntimeException();
		}

		user.setDisplayName(userRequest.getName());
		user.setUserName(userRequest.getEmail().split("@gmail.com")[0]);
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setUserRole(userRequest.getUserRole());
		user.setEmailVerified(false);
		return (T)user;
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> userLogin(AuthRequest authRequest) {
		String username=authRequest.getUsername().split("@gmail.com")[0];
		//System.out.println(username);
		Authentication authentication=authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));

		if(!authentication.isAuthenticated()) throw new RuntimeException();

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// generate access and refresh token

		HttpHeaders headers=new HttpHeaders();
		//	   userRespository.findByUserName(username).ifPresent(user->{
		//		   generateAccessToken(user, headers);
		//		   generateRefreshToken(user, headers);
		//	   });


		//add as cookie to the response
		//generate authResponse and return

		return userRespository.findByUserName(username).map(user->{
			generateAccessToken(user, headers);
			generateRefreshToken(user, headers);

			return ResponseEntity.ok().headers(headers).body(new ResponseStructure<AuthResponse>().setBody(mapToAuthResponse(user))
					.setStatus(HttpStatus.CREATED.value()).setMessage("Authentication Sucessfull")
					);

		}).get();

	}


	private AuthResponse mapToAuthResponse(User user) {
		return AuthResponse.builder()
				.userId(user.getUserId())
				.username(user.getUserName())
				.userRole(user.getUserRole())
				.accessExpiration(accessExpiration)
				.refreshExpiration(refreshExpiration)
				.build();
	}

	private void generateAccessToken(User user, HttpHeaders headers) {
		// TODO Auto-generated method stub
		String token=jwtService.genrateRefeshToken(user,user.getUserRole().name());
		headers.add(HttpHeaders.SET_COOKIE, configureCookie("at",token,accessExpiration));

		AccessToken at=new AccessToken();
		at.setToken(token);
		at.setExpiration(LocalDateTime.now());
		at.setBlocked(false);
		at.setUser(user);

		accessRepository.save(at);

	}

	private void generateRefreshToken(User user,HttpHeaders headers) {
		String token=jwtService.genrateRefeshToken(user,user.getUserRole().name());
		headers.add(HttpHeaders.SET_COOKIE, configureCookie("rt",token,refreshExpiration));
		//store the token to the database.

		RefreshToken rt=new RefreshToken();	
		rt.setToken(token);
		rt.setExpiration(LocalDateTime.now());
		rt.setBlocked(false);
		rt.setUser(user);
		refreshTokenRepository.save(rt);
	}

	private String configureCookie(String name, String value, long maxAge) {

		return ResponseCookie.from(name, value)
				.domain("localhost")
				.path("/")
				.httpOnly(false)
				.maxAge(Duration.ofMillis(maxAge))
				.sameSite("Lax")
				.build().toString();
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> userLogout(String accessToken, String refreshToken) {
		if(accessRepository.existsByTokenAndIsBlocked(accessToken,true)||refreshTokenRepository.existsByTokenAndIsBlocked(refreshToken,true)) {
			throw new UserIsNotLoginException("user is not Logins");
		}
		
		HttpHeaders headers=new HttpHeaders();
		accessRepository.findByToken(accessToken).ifPresent(at->{
			refreshTokenRepository.findByToken(refreshToken).ifPresent(rt->{
				
				at.setBlocked(true);
				accessRepository.save(at);
				
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
				
				removeAccess("at",headers);
				removeAccess("rt",headers);
				
			});
		});
		       return ResponseEntity.ok().headers(headers).body(simpleResponseStructure.setMessage("Logout Sucessfully....")
		    		   .setStatus(HttpStatus.OK.value()));
	}

	private void removeAccess(String value, HttpHeaders headers) {
		headers.add(HttpHeaders.SET_COOKIE, removeCookie(value));
		
	}

	private String removeCookie(String name) {
		
		return ResponseCookie.from(name,"")
				.domain("localhost")
				.path("/")
				.httpOnly(true)
				.maxAge(0)
				.sameSite("Lax")
				.build().toString();
	}

}
