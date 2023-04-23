//package com.dtalks.dtalks.user.controller;
//
//import com.dtalks.dtalks.user.dto.UserDto;
//import com.dtalks.dtalks.user.dto.UserResponseDto;
//import com.dtalks.dtalks.user.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/user")
//public class UserController {
//
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping()
//    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserDto userDto) {
//        UserResponseDto userResponseDto = userService.saveUser(userDto);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
//    }
//}
