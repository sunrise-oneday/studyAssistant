package org.example.studyassistant.service;

import org.example.studyassistant.pojo.dto.UserDTO;
import org.example.studyassistant.pojo.entity.User;
import org.example.studyassistant.repository.UserRepository;
import org.example.studyassistant.Impl.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;//加密

    private static final ConcurrentHashMap<String, Long> TOKEN_STORE = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRE = TimeUnit.HOURS.toMillis(2);
    @Override
    public Map<String, Object> login(UserDTO userDTO){
        User user=userRepository.findByName(userDTO.getName());
        if(user==null){
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        TOKEN_STORE.put(token, System.currentTimeMillis() + TOKEN_EXPIRE);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", user.getRole()); // 从数据库取出的角色
        result.put("name", user.getName());

        return result;
    }


    @Override
    public void register(UserDTO userDTO) {
        User existUser = userRepository.findByName(userDTO.getName());
        if (existUser != null) {
            throw new RuntimeException("该用户名已被注册");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setRole(userDTO.getRole());

        // 3. 密码加密 (核心步骤)
        // 使用 BCrypt 加密密码，存入数据库的是密文
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        // 4. 保存到数据库
        userRepository.save(user);
    }

    public boolean validateToken(String token) {
        Long expireTime = TOKEN_STORE.get(token);
        if (expireTime == null) {
            return false; // Token 不存在
        }
        if (System.currentTimeMillis() > expireTime) {
            TOKEN_STORE.remove(token); // Token 已过期，移除
            return false;
        }
        return true; // Token 有效
    }
}
