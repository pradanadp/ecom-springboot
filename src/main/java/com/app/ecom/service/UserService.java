package com.app.ecom.service;

import com.app.ecom.dto.AddressDTO;
import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.exception.ResourceNotFoundException;
import com.app.ecom.model.Address;
import com.app.ecom.model.User;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    public UserResponse fetchUser(Long id) {
        return mapToUserResponse(findUser(id));
    }

    public UserResponse addUser(UserRequest request) {
        return mapToUserResponse(userRepository.save(toUser(request)));
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUser(id);
        applyRequest(user, request);
        return mapToUserResponse(userRepository.save(user));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private User toUser(UserRequest request) {
        User user = new User();
        applyRequest(user, request);
        return user;
    }

    private void applyRequest(User user, UserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getAddress() != null) {
            user.setAddress(toAddress(request.getAddress()));
        }
    }

    private Address toAddress(AddressDTO addressDTO) {
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setZipCode(addressDTO.getZipCode());
        return address;
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                toAddressDTO(user.getAddress())
        );
    }

    private AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZipCode()
        );
    }
}
