package org.example.studyassistant.Impl;

import org.example.studyassistant.pojo.dto.UserDTO;

import java.util.Map;

public interface IUserService {
    Map<String, Object> login(UserDTO userDTO);

    void register(UserDTO userDTO);
}
